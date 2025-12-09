// src/main/java/com/hainan/weather/entity/AttractionStatus.java
package com.hainan.weather.entity;

import lombok.Data;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
public class AttractionStatus {
    private Long id;
    private String name;                // 景点名称
    private String locationCode;        // 所属地区代码
    private String address;             // 详细地址
    private String openStatus;          // OPEN, CLOSED, LIMITED
    private String closeReason;         // 关闭原因
    private LocalTime openTime;         // 开放时间
    private LocalTime closeTime;        // 关闭时间
    private Long updatedBy;             // 更新人ID
    private LocalDateTime updatedTime;   // 更新时间
}