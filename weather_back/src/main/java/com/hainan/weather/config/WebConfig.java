// src/main/java/com/hainan/weather/config/WebConfig.java
package com.hainan.weather.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源处理
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // 资源
        registry.addResourceHandler("//**")
                .addResourceLocations("classpath:/META-INF/resources//");
    }
}