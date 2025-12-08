// src/main/java/com/hainan/weather/entity/WeatherForecast.java
package com.hainan.weather.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WeatherForecast {
    private Long id;
    private String locationCode;
    private LocalDate forecastDate;      // 预报日期
    private BigDecimal highTemp;         // 最高温度
    private BigDecimal lowTemp;          // 最低温度
    private String dayCondition;         // 白天天气
    private String nightCondition;       // 夜间天气
    private BigDecimal windSpeed;        // 风速
    private BigDecimal humidity;         // 湿度
    private BigDecimal precipitationProb; // 降水概率
    private LocalDateTime createdTime;   // 创建时间
}