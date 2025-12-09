// src/main/java/com/hainan/weather/entity/Location.java
package com.hainan.weather.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Location {
    private Long id;
    private String cityName;
    private String districtName;
    private String locationCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String locationId; // 和风天气的location_id
    private Integer status; // 1-启用，0-禁用
}