package com.hainan.weather.service;

import com.hainan.weather.entity.TrafficStatus;
import com.hainan.weather.mapper.TrafficStatusMapper;
import com.hainan.weather.dto.TrafficUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class TrafficService {

    @Autowired
    private TrafficStatusMapper trafficStatusMapper;

    @Autowired
    private SystemService systemService;

    /**
     * 获取航班状态
     */
    public List<TrafficStatus> getFlightStatus() {
        try {
            return trafficStatusMapper.findByType("FLIGHT");
        } catch (Exception e) {
            log.error("获取航班状态失败", e);
            return null;
        }
    }

    /**
     * 获取火车状态
     */
    public List<TrafficStatus> getTrainStatus() {
        try {
            return trafficStatusMapper.findByType("TRAIN");
        } catch (Exception e) {
            log.error("获取火车状态失败", e);
            return null;
        }
    }

    /**
     * 获取异常交通状态
     */
    public List<TrafficStatus> getAbnormalTrafficStatus() {
        try {
            return trafficStatusMapper.findAbnormalStatus();
        } catch (Exception e) {
            log.error("获取异常交通状态失败", e);
            return null;
        }
    }

    /**
     * 根据城市和时间范围查询航班
     */
    public List<TrafficStatus> getFlightsByCityAndTime(String city,
                                                       LocalDateTime startTime,
                                                       LocalDateTime endTime) {
        try {
            return trafficStatusMapper.findByCityAndTimeRange("FLIGHT", city, startTime, endTime);
        } catch (Exception e) {
            log.error("查询航班失败", e);
            return null;
        }
    }

    /**
     * 根据城市和时间范围查询火车
     */
    public List<TrafficStatus> getTrainsByCityAndTime(String city,
                                                      LocalDateTime startTime,
                                                      LocalDateTime endTime) {
        try {
            return trafficStatusMapper.findByCityAndTimeRange("TRAIN", city, startTime, endTime);
        } catch (Exception e) {
            log.error("查询火车失败", e);
            return null;
        }
    }

    /**
     * 获取指定航班/车次的状态
     */
    public TrafficStatus getTrafficStatus(String type, String number) {
        try {
            return trafficStatusMapper.findByNumber(type, number);
        } catch (Exception e) {
            log.error("获取交通状态失败", e);
            return null;
        }
    }

    /**
     * 添加交通状态
     */
    @Transactional
    public boolean addTrafficStatus(TrafficUpdateDTO dto, Long operatorId) {
        try {
            TrafficStatus status = new TrafficStatus();
            status.setType(dto.getType());
            status.setNumber(dto.getNumber());
            status.setDepartureCity(dto.getDepartureCity());
            status.setArrivalCity(dto.getArrivalCity());
            status.setScheduledTime(dto.getScheduledTime());
            status.setEstimatedTime(dto.getEstimatedTime());
            status.setStatus(dto.getStatus());
            status.setDelayReason(dto.getDelayReason());
            status.setUpdatedBy(operatorId);

            int result = trafficStatusMapper.insert(status);

            if (result > 0) {
                systemService.logOperation(operatorId, "CREATE", "TRAFFIC",
                        "添加交通状态: " + dto.getType() + " " + dto.getNumber(), null);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("添加交通状态失败", e);
            return false;
        }
    }

    /**
     * 更新交通状态
     */
    @Transactional
    public boolean updateTrafficStatus(Long id, TrafficUpdateDTO dto, Long operatorId) {
        try {
            TrafficStatus status = trafficStatusMapper.findById(id);
            if (status == null) {
                return false;
            }

            status.setEstimatedTime(dto.getEstimatedTime());
            status.setStatus(dto.getStatus());
            status.setDelayReason(dto.getDelayReason());
            status.setUpdatedBy(operatorId);

            int result = trafficStatusMapper.update(status);

            if (result > 0) {
                systemService.logOperation(operatorId, "UPDATE", "TRAFFIC",
                        "更新交通状态: " + status.getType() + " " + status.getNumber(), null);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新交通状态失败", e);
            return false;
        }
    }

    /**
     * 批量更新航班状态
     */
    @Transactional
    public boolean batchUpdateFlightStatus(List<TrafficUpdateDTO> updates, Long operatorId) {
        try {
            for (TrafficUpdateDTO dto : updates) {
                TrafficStatus existing = trafficStatusMapper.findByNumber("FLIGHT", dto.getNumber());
                if (existing != null) {
                    existing.setEstimatedTime(dto.getEstimatedTime());
                    existing.setStatus(dto.getStatus());
                    existing.setDelayReason(dto.getDelayReason());
                    existing.setUpdatedBy(operatorId);
                    trafficStatusMapper.update(existing);
                } else {
                    TrafficStatus status = new TrafficStatus();
                    status.setType("FLIGHT");
                    status.setNumber(dto.getNumber());
                    status.setDepartureCity(dto.getDepartureCity());
                    status.setArrivalCity(dto.getArrivalCity());
                    status.setScheduledTime(dto.getScheduledTime());
                    status.setEstimatedTime(dto.getEstimatedTime());
                    status.setStatus(dto.getStatus());
                    status.setDelayReason(dto.getDelayReason());
                    status.setUpdatedBy(operatorId);
                    trafficStatusMapper.insert(status);
                }
            }
            systemService.logOperation(operatorId, "BATCH_UPDATE", "TRAFFIC",
                    "批量更新航班状态，数量: " + updates.size(), null);
            return true;
        } catch (Exception e) {
            log.error("批量更新航班状态失败", e);
            return false;
        }
    }

    /**
     * 删除交通状态
     */
    @Transactional
    public boolean deleteTrafficStatus(Long id, Long operatorId) {
        try {
            TrafficStatus status = trafficStatusMapper.findById(id);
            if (status == null) {
                return false;
            }

            int result = trafficStatusMapper.delete(id);

            if (result > 0) {
                systemService.logOperation(operatorId, "DELETE", "TRAFFIC",
                        "删除交通状态: " + status.getType() + " " + status.getNumber(), null);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除交通状态失败", e);
            return false;
        }
    }

    /**
     * 获取异常航班数量
     */
    public int getAbnormalFlightCount() {
        return trafficStatusMapper.countAbnormalByType("FLIGHT");
    }

    /**
     * 获取异常火车数量
     */
    public int getAbnormalTrainCount() {
        return trafficStatusMapper.countAbnormalByType("TRAIN");
    }
}