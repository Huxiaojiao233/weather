// src/main/java/com/hainan/weather/entity/ComprehensiveWarning.java
package com.hainan.weather.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ComprehensiveWarning {
    private Long id;
    private String title;               // 综合预警标题
    private String content;             // 综合预警内容
    private String warningLevel;        // 预警等级
    private String affectedLocations;   // 受影响地区（JSON数组字符串）
    private String weatherWarningIds;   // 关联的天气预警ID（JSON数组字符串）
    private String trafficEffects;      // 交通影响说明
    private String attractionEffects;   // 景点影响说明
    private Long publishedBy;           // 发布人ID
    private LocalDateTime publishTime;   // 发布时间
    private String status;              // DRAFT, PUBLISHED
    private LocalDateTime createdTime;   // 创建时间
}