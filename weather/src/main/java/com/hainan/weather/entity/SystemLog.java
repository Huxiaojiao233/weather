// src/main/java/com/hainan/weather/entity/SystemLog.java
package com.hainan.weather.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SystemLog {
    private Long id;
    private Long userId;                // 操作用户ID
    private String operation;           // 操作类型
    private String module;              // 操作模块
    private String description;         // 操作描述
    private String ipAddress;           // IP地址
    private LocalDateTime operationTime; // 操作时间
}