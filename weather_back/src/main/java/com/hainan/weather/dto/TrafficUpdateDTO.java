// src/main/java/com/hainan/weather/dto/TrafficUpdateDTO.java
package com.hainan.weather.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TrafficUpdateDTO {
    private String type; // FLIGHT, TRAIN
    private String number;
    private String departureCity;
    private String arrivalCity;
    private LocalDateTime scheduledTime;
    private LocalDateTime estimatedTime;
    private String status; // NORMAL, DELAYED, CANCELLED
    private String delayReason;
}