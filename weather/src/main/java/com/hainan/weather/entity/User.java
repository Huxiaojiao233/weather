// src/main/java/com/hainan/weather/entity/User.java
package com.hainan.weather.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String role; // ADMIN, USER
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}