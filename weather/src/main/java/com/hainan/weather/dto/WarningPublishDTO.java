// src/main/java/com/hainan/weather/dto/WarningPublishDTO.java
package com.hainan.weather.dto;

import lombok.Data;
import java.util.List;

@Data
public class WarningPublishDTO {
    private String title;
    private String content;
    private String warningLevel;
    private List<String> affectedLocations;
    private List<Long> weatherWarningIds;
    private String trafficEffects;
    private String attractionEffects;
}