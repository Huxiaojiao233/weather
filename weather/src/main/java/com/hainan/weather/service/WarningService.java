package com.hainan.weather.service;

import com.hainan.weather.entity.WeatherWarning;
import com.hainan.weather.entity.ComprehensiveWarning;
import com.hainan.weather.mapper.WeatherWarningMapper;
import com.hainan.weather.mapper.ComprehensiveWarningMapper;
import com.hainan.weather.dto.WarningPublishDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class WarningService {

    @Autowired
    private WeatherWarningMapper weatherWarningMapper;

    @Autowired
    private ComprehensiveWarningMapper comprehensiveWarningMapper;

    @Autowired
    private SystemService systemService;

    /**
     * 获取所有活跃的天气预警
     */
    public List<WeatherWarning> getActiveWeatherWarnings() {
        try {
            return weatherWarningMapper.findAllActive();
        } catch (Exception e) {
            log.error("获取活跃天气预警失败", e);
            return null;
        }
    }

    /**
     * 获取指定地点的活跃天气预警
     */
    public List<WeatherWarning> getActiveWarningsByLocation(String locationCode) {
        try {
            return weatherWarningMapper.findActiveByLocation(locationCode);
        } catch (Exception e) {
            log.error("获取地点天气预警失败", e);
            return null;
        }
    }

    /**
     * 获取指定类型的活跃天气预警
     */
    public List<WeatherWarning> getActiveWarningsByType(String warningType) {
        try {
            return weatherWarningMapper.findActiveByType(warningType);
        } catch (Exception e) {
            log.error("获取类型天气预警失败", e);
            return null;
        }
    }

    /**
     * 获取指定等级的活跃天气预警
     */
    public List<WeatherWarning> getActiveWarningsByLevel(String warningLevel) {
        try {
            return weatherWarningMapper.findActiveByLevel(warningLevel);
        } catch (Exception e) {
            log.error("获取等级天气预警失败", e);
            return null;
        }
    }

    /**
     * 保存天气预警
     */
    @Transactional
    public boolean saveWeatherWarning(WeatherWarning warning, Long operatorId) {
        try {
            if (warning.getIssueTime() == null) {
                warning.setIssueTime(LocalDateTime.now());
            }
            if (warning.getStatus() == null) {
                warning.setStatus("ACTIVE");
            }

            int result = weatherWarningMapper.insert(warning);

            if (result > 0) {
                systemService.logOperation(operatorId, "CREATE", "WARNING",
                        "创建天气预警: " + warning.getTitle(), null);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("保存天气预警失败", e);
            return false;
        }
    }

    /**
     * 更新天气预警状态
     */
    @Transactional
    public boolean updateWeatherWarningStatus(Long warningId, String status, Long operatorId) {
        try {
            int result = weatherWarningMapper.updateStatus(warningId, status);

            if (result > 0) {
                systemService.logOperation(operatorId, "UPDATE", "WARNING",
                        "更新天气预警状态, ID: " + warningId, null);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新天气预警状态失败", e);
            return false;
        }
    }

    /**
     * 更新过期的预警状态
     */
    @Transactional
    public int updateExpiredWarnings() {
        try {
            return weatherWarningMapper.updateExpiredWarnings();
        } catch (Exception e) {
            log.error("更新过期预警失败", e);
            return 0;
        }
    }

    /**
     * 获取已发布的综合预警
     */
    public List<ComprehensiveWarning> getPublishedComprehensiveWarnings() {
        try {
            return comprehensiveWarningMapper.findPublishedWarnings();
        } catch (Exception e) {
            log.error("获取已发布综合预警失败", e);
            return null;
        }
    }

    /**
     * 获取草稿综合预警
     */
    public List<ComprehensiveWarning> getDraftComprehensiveWarnings() {
        try {
            return comprehensiveWarningMapper.findDraftWarnings();
        } catch (Exception e) {
            log.error("获取草稿综合预警失败", e);
            return null;
        }
    }

    /**
     * 创建综合预警
     */
    @Transactional
    public boolean createComprehensiveWarning(WarningPublishDTO dto, Long operatorId) {
        try {
            ComprehensiveWarning warning = new ComprehensiveWarning();
            warning.setTitle(dto.getTitle());
            warning.setContent(dto.getContent());
            warning.setWarningLevel(dto.getWarningLevel());

            // 转换为JSON字符串存储
            warning.setAffectedLocations(String.join(",", dto.getAffectedLocations()));
            warning.setWeatherWarningIds(dto.getWeatherWarningIds().toString());
            warning.setTrafficEffects(dto.getTrafficEffects());
            warning.setAttractionEffects(dto.getAttractionEffects());
            warning.setPublishedBy(operatorId);
            warning.setStatus("DRAFT");
            warning.setPublishTime(null);

            int result = comprehensiveWarningMapper.insert(warning);

            if (result > 0) {
                systemService.logOperation(operatorId, "CREATE", "COMPREHENSIVE_WARNING",
                        "创建综合预警草稿: " + dto.getTitle(), null);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("创建综合预警失败", e);
            return false;
        }
    }

    /**
     * 发布综合预警
     */
    @Transactional
    public boolean publishComprehensiveWarning(Long warningId, Long operatorId) {
        try {
            int result = comprehensiveWarningMapper.publishWarning(warningId, operatorId);

            if (result > 0) {
                systemService.logOperation(operatorId, "PUBLISH", "COMPREHENSIVE_WARNING",
                        "发布综合预警, ID: " + warningId, null);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("发布综合预警失败", e);
            return false;
        }
    }

    /**
     * 撤回综合预警
     */
    @Transactional
    public boolean unpublishComprehensiveWarning(Long warningId, Long operatorId) {
        try {
            int result = comprehensiveWarningMapper.unpublishWarning(warningId);

            if (result > 0) {
                systemService.logOperation(operatorId, "UNPUBLISH", "COMPREHENSIVE_WARNING",
                        "撤回综合预警, ID: " + warningId, null);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("撤回综合预警失败", e);
            return false;
        }
    }

    /**
     * 获取活跃预警数量
     */
    public int getActiveWarningCount() {
        return weatherWarningMapper.countActive();
    }

    /**
     * 获取已发布综合预警数量
     */
    public int getPublishedComprehensiveWarningCount() {
        return comprehensiveWarningMapper.countPublished();
    }
}