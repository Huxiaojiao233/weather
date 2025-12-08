package com.hainan.weather.controller;

import com.hainan.weather.entity.WeatherData;
import com.hainan.weather.entity.WeatherWarning;
import com.hainan.weather.entity.TrafficStatus;
import com.hainan.weather.entity.AttractionStatus;
import com.hainan.weather.entity.ComprehensiveWarning;
import com.hainan.weather.service.WeatherService;
import com.hainan.weather.service.WarningService;
import com.hainan.weather.service.TrafficService;
import com.hainan.weather.service.AttractionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Api(tags = "首页API", description = "提供系统首页相关信息")
@Slf4j
public class HomeController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private WarningService warningService;

    @Autowired
    private TrafficService trafficService;

    @Autowired
    private AttractionService attractionService;

    /**
     * 系统首页数据接口
     */
    @GetMapping("/index")
    @ApiOperation(value = "获取首页数据", notes = "获取系统首页展示的所有数据")
    public ResponseEntity<Map<String, Object>> index() {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取三亚最新天气
            WeatherData sanyaWeather = weatherService.getLatestWeather("SANYA");

            // 获取活跃的天气预警
            List<WeatherWarning> activeWarnings = warningService.getActiveWeatherWarnings();

            // 获取已发布的综合预警
            List<ComprehensiveWarning> publishedWarnings = warningService.getPublishedComprehensiveWarnings();

            // 获取异常交通状态
            List<TrafficStatus> abnormalTraffic = trafficService.getAbnormalTrafficStatus();

            // 获取关闭的景点
            List<AttractionStatus> closedAttractions = attractionService.getClosedOrLimitedAttractions();

            Map<String, Object> data = new HashMap<>();
            data.put("sanyaWeather", sanyaWeather);
            data.put("activeWarnings", activeWarnings);
            data.put("publishedWarnings", publishedWarnings);
            data.put("abnormalTraffic", abnormalTraffic);
            data.put("closedAttractions", closedAttractions);

            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取首页数据失败", e);
            response.put("success", false);
            response.put("message", "获取首页数据失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 天气信息页面数据接口
     */
    @GetMapping("/weather/info")
    @ApiOperation(value = "获取天气信息", notes = "获取天气信息页面的数据")
    public ResponseEntity<Map<String, Object>> weatherInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Weather information page");
        return ResponseEntity.ok(response);
    }

    /**
     * 交通信息页面数据接口
     */
    @GetMapping("/traffic/info")
    @ApiOperation(value = "获取交通信息", notes = "获取交通信息页面的数据")
    public ResponseEntity<Map<String, Object>> trafficInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Traffic information page");
        return ResponseEntity.ok(response);
    }

    /**
     * 景点信息页面数据接口
     */
    @GetMapping("/attractions/info")
    @ApiOperation(value = "获取景点信息", notes = "获取景点信息页面的数据")
    public ResponseEntity<Map<String, Object>> attractionsInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Attractions information page");
        return ResponseEntity.ok(response);
    }

    /**
     * 预警信息页面数据接口
     */
    @GetMapping("/warnings/info")
    @ApiOperation(value = "获取预警信息", notes = "获取预警信息页面的数据")
    public ResponseEntity<Map<String, Object>> warningsInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Warnings information page");
        return ResponseEntity.ok(response);
    }

    /**
     * 关于页面数据接口
     */
    @GetMapping("/about")
    @ApiOperation(value = "获取关于信息", notes = "获取关于页面的数据")
    public ResponseEntity<Map<String, Object>> about() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "About page");
        return ResponseEntity.ok(response);
    }

    /**
     * 帮助页面数据接口
     */
    @GetMapping("/help")
    @ApiOperation(value = "获取帮助信息", notes = "获取帮助页面的数据")
    public ResponseEntity<Map<String, Object>> help() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Help page");
        return ResponseEntity.ok(response);
    }

    /**
     * 实时天气地图数据接口
     */
    @GetMapping("/weather-map")
    @ApiOperation(value = "获取天气地图信息", notes = "获取实时天气地图页面的数据")
    public ResponseEntity<Map<String, Object>> weatherMap() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Weather map page");
        return ResponseEntity.ok(response);
    }
}
