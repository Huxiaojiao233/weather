// src/main/java/com/hainan/weather/dto/WeatherQueryDTO.java
package com.hainan.weather.dto;

import lombok.Data;

@Data
public class WeatherQueryDTO {
    private String locationCode;
    private String date; // yyyy-MM-dd格式
    private String startDate;
    private String endDate;
    private String cityName;
}