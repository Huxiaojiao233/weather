package com.hainan.weather.service;

import com.hainan.weather.entity.SystemLog;
import com.hainan.weather.mapper.SystemLogMapper;
import com.hainan.weather.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SystemService {

    @Autowired
    private SystemLogMapper systemLogMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 记录系统操作日志
     */
    @Transactional
    public void logOperation(Long userId, String operation, String module,
                             String description, String ipAddress) {
        try {
            SystemLog log = new SystemLog();
            log.setUserId(userId);
            log.setOperation(operation);
            log.setModule(module);
            log.setDescription(description);
            log.setIpAddress(ipAddress);
            log.setOperationTime(LocalDateTime.now());

            systemLogMapper.insert(log);
        } catch (Exception e) {
            // 这里不能抛出异常，否则会影响业务操作
            log.error("记录系统日志失败", e);
        }
    }

    /**
     * 获取最近的操作日志
     */
    public List<SystemLog> getRecentLogs(int limit) {
        try {
            return systemLogMapper.findRecentLogs(limit);
        } catch (Exception e) {
            log.error("获取最近日志失败", e);
            return null;
        }
    }

    /**
     * 根据用户ID获取操作日志
     */
    public List<SystemLog> getLogsByUser(Long userId) {
        try {
            return systemLogMapper.findByUserId(userId);
        } catch (Exception e) {
            log.error("获取用户日志失败", e);
            return null;
        }
    }

    /**
     * 根据模块获取操作日志
     */
    public List<SystemLog> getLogsByModule(String module) {
        try {
            return systemLogMapper.findByModule(module);
        } catch (Exception e) {
            log.error("获取模块日志失败", e);
            return null;
        }
    }

    /**
     * 根据操作类型获取日志
     */
    public List<SystemLog> getLogsByOperation(String operation) {
        try {
            return systemLogMapper.findByOperation(operation);
        } catch (Exception e) {
            log.error("获取操作类型日志失败", e);
            return null;
        }
    }

    /**
     * 根据时间范围获取日志
     */
    public List<SystemLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            return systemLogMapper.findByTimeRange(startTime, endTime);
        } catch (Exception e) {
            log.error("获取时间范围日志失败", e);
            return null;
        }
    }

    /**
     * 根据IP地址获取日志
     */
    public List<SystemLog> getLogsByIpAddress(String ipAddress) {
        try {
            return systemLogMapper.findByIpAddress(ipAddress);
        } catch (Exception e) {
            log.error("获取IP地址日志失败", e);
            return null;
        }
    }

    /**
     * 获取所有操作日志
     */
    public List<SystemLog> getAllLogs() {
        try {
            return systemLogMapper.findAll();
        } catch (Exception e) {
            log.error("获取所有日志失败", e);
            return null;
        }
    }

    /**
     * 删除旧日志
     */
    @Transactional
    public int deleteOldLogs(LocalDateTime cutoffTime) {
        try {
            int deletedCount = systemLogMapper.deleteOldLogs(cutoffTime);
            if (deletedCount > 0) {
                log.info("删除了 {} 条旧日志", deletedCount);
            }
            return deletedCount;
        } catch (Exception e) {
            log.error("删除旧日志失败", e);
            return 0;
        }
    }

    /**
     * 获取系统统计信息
     */
    public SystemStatistics getSystemStatistics() {
        try {
            SystemStatistics stats = new SystemStatistics();
            stats.setUserCount(userMapper.count());  // 直接使用UserMapper而不是UserService
            stats.setLogCount(systemLogMapper.count());

            return stats;
        } catch (Exception e) {
            log.error("获取系统统计信息失败", e);
            return null;
        }
    }

    /**
     * 系统统计信息内部类
     */
    public static class SystemStatistics {
        private int userCount;
        private int logCount;

        public int getUserCount() {
            return userCount;
        }

        public void setUserCount(int userCount) {
            this.userCount = userCount;
        }

        public int getLogCount() {
            return logCount;
        }

        public void setLogCount(int logCount) {
            this.logCount = logCount;
        }
    }

    /**
     * 清理过期数据（定时任务用）
     */
    @Transactional
    public void cleanupExpiredData() {
        try {
            // 删除30天前的日志
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            int deletedLogs = deleteOldLogs(thirtyDaysAgo);

            // 更新过期的天气预警
            // 这里可以调用其他服务的清理方法

            log.info("系统清理完成，删除日志: {} 条", deletedLogs);
        } catch (Exception e) {
            log.error("清理过期数据失败", e);
        }
    }
}