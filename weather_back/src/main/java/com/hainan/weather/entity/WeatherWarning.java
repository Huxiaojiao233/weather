// src/main/java/com/hainan/weather/entity/WeatherWarning.java
package com.hainan.weather.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WeatherWarning {
    private Long id;
    private String locationCode;
    private String warningType;          // 预警类型
    private String warningLevel;         // 预警等级
    private String title;                // 标题
    private String content;              // 内容
    private LocalDateTime issueTime;     // 发布时间
    private LocalDateTime effectiveTime; // 生效时间
    private LocalDateTime expireTime;    // 过期时间
    private String status;               // ACTIVE, EXPIRED
}