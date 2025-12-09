package com.hainan.weather.service;

import com.hainan.weather.entity.AttractionStatus;
import com.hainan.weather.mapper.AttractionStatusMapper;
import com.hainan.weather.dto.AttractionUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class AttractionService {

    @Autowired
    private AttractionStatusMapper attractionStatusMapper;

    @Autowired
    private SystemService systemService;

    /**
     * 获取所有景点状态
     */
    public List<AttractionStatus> getAllAttractions() {
        try {
            return attractionStatusMapper.findAll();
        } catch (Exception e) {
            log.error("获取所有景点状态失败", e);
            return null;
        }
    }

    /**
     * 获取指定地点的景点状态
     */
    public List<AttractionStatus> getAttractionsByLocation(String locationCode) {
        try {
            return attractionStatusMapper.findByLocationCode(locationCode);
        } catch (Exception e) {
            log.error("获取地点景点状态失败", e);
            return null;
        }
    }

    /**
     * 获取关闭或受限的景点
     */
    public List<AttractionStatus> getClosedOrLimitedAttractions() {
        try {
            return attractionStatusMapper.findAbnormalStatus();
        } catch (Exception e) {
            log.error("获取关闭或受限景点失败", e);
            return null;
        }
    }

    /**
     * 根据名称搜索景点
     */
    public List<AttractionStatus> searchAttractions(String name) {
        try {
            return attractionStatusMapper.findByName(name);
        } catch (Exception e) {
            log.error("搜索景点失败", e);
            return null;
        }
    }

    /**
     * 获取指定开放状态的景点
     */
    public List<AttractionStatus> getAttractionsByStatus(String openStatus) {
        try {
            return attractionStatusMapper.findByOpenStatus(openStatus);
        } catch (Exception e) {
            log.error("获取开放状态景点失败", e);
            return null;
        }
    }

    /**
     * 添加景点状态
     */
    @Transactional
    public boolean addAttraction(AttractionUpdateDTO dto, Long operatorId) {
        try {
            AttractionStatus attraction = new AttractionStatus();
            attraction.setName(dto.getName());
            attraction.setLocationCode(dto.getLocationCode());
            attraction.setAddress(dto.getAddress());
            attraction.setOpenStatus(dto.getOpenStatus());
            attraction.setCloseReason(dto.getCloseReason());
            attraction.setOpenTime(dto.getOpenTime());
            attraction.setCloseTime(dto.getCloseTime());
            attraction.setUpdatedBy(operatorId);

            int result = attractionStatusMapper.insert(attraction);

            if (result > 0) {
                systemService.logOperation(operatorId, "CREATE", "ATTRACTION",
                        "添加景点: " + dto.getName(), null);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("添加景点失败", e);
            return false;
        }
    }

    /**
     * 更新景点状态
     */
    @Transactional
    public boolean updateAttraction(Long id, AttractionUpdateDTO dto, Long operatorId) {
        try {
            AttractionStatus attraction = attractionStatusMapper.findById(id);
            if (attraction == null) {
                return false;
            }

            attraction.setName(dto.getName());
            attraction.setLocationCode(dto.getLocationCode());
            attraction.setAddress(dto.getAddress());
            attraction.setOpenStatus(dto.getOpenStatus());
            attraction.setCloseReason(dto.getCloseReason());
            attraction.setOpenTime(dto.getOpenTime());
            attraction.setCloseTime(dto.getCloseTime());
            attraction.setUpdatedBy(operatorId);

            int result = attractionStatusMapper.update(attraction);

            if (result > 0) {
                systemService.logOperation(operatorId, "UPDATE", "ATTRACTION",
                        "更新景点: " + dto.getName(), null);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新景点失败", e);
            return false;
        }
    }

    /**
     * 批量更新景点状态
     */
    @Transactional
    public boolean batchUpdateAttractionStatus(List<Long> attractionIds,
                                               String openStatus,
                                               String closeReason,
                                               Long operatorId) {
        try {
            for (Long id : attractionIds) {
                int result = attractionStatusMapper.updateStatus(id, openStatus, closeReason, operatorId);
                if (result <= 0) {
                    log.warn("更新景点状态失败，ID: {}", id);
                }
            }

            systemService.logOperation(operatorId, "BATCH_UPDATE", "ATTRACTION",
                    "批量更新景点状态，数量: " + attractionIds.size(), null);
            return true;
        } catch (Exception e) {
            log.error("批量更新景点状态失败", e);
            return false;
        }
    }

    /**
     * 删除景点
     */
    @Transactional
    public boolean deleteAttraction(Long id, Long operatorId) {
        try {
            AttractionStatus attraction = attractionStatusMapper.findById(id);
            if (attraction == null) {
                return false;
            }

            int result = attractionStatusMapper.delete(id);

            if (result > 0) {
                systemService.logOperation(operatorId, "DELETE", "ATTRACTION",
                        "删除景点: " + attraction.getName(), null);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除景点失败", e);
            return false;
        }
    }

    /**
     * 获取关闭景点数量
     */
    public int getClosedAttractionCount() {
        return attractionStatusMapper.countClosedAttractions();
    }

    /**
     * 检查景点是否存在
     */
    public boolean isAttractionExists(String name) {
        List<AttractionStatus> attractions = attractionStatusMapper.findByName(name);
        return attractions != null && !attractions.isEmpty();
    }
}