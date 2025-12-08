// src/main/java/com/hainan/weather/entity/WeatherData.java
package com.hainan.weather.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
public class WeatherData {
    private Long id;
    private String locationCode;
    private BigDecimal temperature;      // 温度
    private BigDecimal humidity;         // 湿度
    private BigDecimal windSpeed;        // 风速
    private String windDirection;        // 风向
    private BigDecimal precipitation;    // 降水量
    private String weatherCondition;     // 天气状况
    private BigDecimal pressure;         // 气压
    private BigDecimal visibility;       // 能见度
    private LocalDateTime updateTime;    // 更新时间
    private LocalDate dataDate;          // 数据日期
}