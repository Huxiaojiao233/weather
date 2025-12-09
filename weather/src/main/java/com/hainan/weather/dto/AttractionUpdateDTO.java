// src/main/java/com/hainan/weather/dto/AttractionUpdateDTO.java
package com.hainan.weather.dto;

import lombok.Data;
import java.time.LocalTime;

@Data
public class AttractionUpdateDTO {
    private String name;
    private String locationCode;
    private String address;
    private String openStatus; // OPEN, CLOSED, LIMITED
    private String closeReason;
    private LocalTime openTime;
    private LocalTime closeTime;
}