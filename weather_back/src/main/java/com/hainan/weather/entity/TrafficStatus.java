// src/main/java/com/hainan/weather/entity/TrafficStatus.java
package com.hainan.weather.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TrafficStatus {
    private Long id;
    private String type;                // FLIGHT, TRAIN
    private String number;              // 航班号/车次
    private String departureCity;       // 出发城市
    private String arrivalCity;         // 到达城市
    private LocalDateTime scheduledTime; // 计划时间
    private LocalDateTime estimatedTime; // 预计时间
    private String status;              // NORMAL, DELAYED, CANCELLED
    private String delayReason;         // 延误原因
    private Long updatedBy;             // 更新人ID
    private LocalDateTime updatedTime;   // 更新时间
}