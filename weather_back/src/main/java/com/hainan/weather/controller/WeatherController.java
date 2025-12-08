package com.hainan.weather.controller;

import com.hainan.weather.entity.WeatherData;
import com.hainan.weather.entity.WeatherForecast;
import com.hainan.weather.service.WeatherService;
import com.hainan.weather.service.WarningService;
import com.hainan.weather.entity.WeatherWarning;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/weather")
@Api(tags = "天气信息API", description = "提供实时天气、预报、历史天气等信息查询")
@Slf4j
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private WarningService warningService;

    /**
     * 实时天气查询接口
     */
    @GetMapping("/realtime")
    @ApiOperation(value = "获取实时天气", notes = "根据地点获取最新的天气信息")
    public ResponseEntity<Map<String, Object>> realtimeWeather(
            @ApiParam(value = "地点代码，默认为三亚(SANYA)", defaultValue = "SANYA")
            @RequestParam(defaultValue = "SANYA") String location) {

        Map<String, Object> response = new HashMap<>();
        try {
            WeatherData weather = weatherService.getLatestWeather(location);
            List<WeatherWarning> locationWarnings = warningService.getActiveWarningsByLocation(location);

            response.put("success", true);
            response.put("data", Map.of(
                "weather", weather,
                "location", location,
                "locationWarnings", locationWarnings
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取实时天气失败, location: {}", location, e);
            response.put("success", false);
            response.put("message", "获取天气信息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 天气预报接口
     */
    @GetMapping("/forecast")
    @ApiOperation(value = "获取天气预报", notes = "根据地点获取一周天气预报信息")
    public ResponseEntity<Map<String, Object>> weatherForecast(
            @ApiParam(value = "地点代码，默认为三亚(SANYA)", defaultValue = "SANYA")
            @RequestParam(defaultValue = "SANYA") String location) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<WeatherForecast> forecasts = weatherService.getWeekForecast(location);

            response.put("success", true);
            response.put("data", Map.of(
                "forecasts", forecasts,
                "location", location
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取天气预报失败, location: {}", location, e);
            response.put("success", false);
            response.put("message", "获取天气预报失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 历史天气查询接口
     */
    @GetMapping("/history")
    @ApiOperation(value = "获取历史天气", notes = "根据地点和日期获取历史天气信息")
    public ResponseEntity<Map<String, Object>> weatherHistory(
            @ApiParam(value = "地点代码，默认为三亚(SANYA)", defaultValue = "SANYA")
            @RequestParam(defaultValue = "SANYA") String location,
            @ApiParam(value = "查询日期，格式为 yyyy-MM-dd")
            @RequestParam(required = false) String date) {

        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("location", location);
            data.put("selectedDate", date);

            if (date != null && !date.isEmpty()) {
                LocalDate queryDate = LocalDate.parse(date);
                WeatherData historicalWeather = weatherService.getWeatherByDate(location, queryDate);
                data.put("historicalWeather", historicalWeather);
            }

            // 获取最近7天的历史数据
            List<WeatherData> recentWeather = weatherService.getHistoricalWeather(location, 7);
            data.put("recentWeather", recentWeather);

            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取历史天气失败, location: {}, date: {}", location, date, e);
            response.put("success", false);
            response.put("message", "获取历史天气失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 天气预警列表接口
     */
    @GetMapping("/warnings")
    @ApiOperation(value = "获取天气预警列表", notes = "获取所有活跃的天气预警信息")
    public ResponseEntity<Map<String, Object>> weatherWarnings() {

        Map<String, Object> response = new HashMap<>();
        try {
            List<WeatherWarning> allActiveWarnings = warningService.getActiveWeatherWarnings();

            response.put("success", true);
            response.put("data", Map.of("allWarnings", allActiveWarnings));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取天气预警列表失败", e);
            response.put("success", false);
            response.put("message", "获取天气预警列表失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 按类型查看天气预警接口
     */
    @GetMapping("/warnings/type")
    @ApiOperation(value = "按类型获取天气预警", notes = "根据预警类型获取活跃的天气预警信息")
    public ResponseEntity<Map<String, Object>> warningsByType(
            @ApiParam(value = "预警类型", required = true)
            @RequestParam String warningType) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<WeatherWarning> warnings = warningService.getActiveWarningsByType(warningType);

            response.put("success", true);
            response.put("data", Map.of(
                "warnings", warnings,
                "warningType", warningType
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("按类型获取天气预警失败, warningType: {}", warningType, e);
            response.put("success", false);
            response.put("message", "按类型获取天气预警失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 按等级查看天气预警接口
     */
    @GetMapping("/warnings/level")
    @ApiOperation(value = "按等级获取天气预警", notes = "根据预警等级获取活跃的天气预警信息")
    public ResponseEntity<Map<String, Object>> warningsByLevel(
            @ApiParam(value = "预警等级", required = true)
            @RequestParam String warningLevel) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<WeatherWarning> warnings = warningService.getActiveWarningsByLevel(warningLevel);

            response.put("success", true);
            response.put("data", Map.of(
                "warnings", warnings,
                "warningLevel", warningLevel
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("按等级获取天气预警失败, warningLevel: {}", warningLevel, e);
            response.put("success", false);
            response.put("message", "按等级获取天气预警失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 详细天气预警信息接口
     */
    @GetMapping("/warning/detail")
    @ApiOperation(value = "获取天气预警详情", notes = "根据预警ID获取详细的天气预警信息")
    public ResponseEntity<Map<String, Object>> warningDetail(
            @ApiParam(value = "预警ID", required = true)
            @RequestParam Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<WeatherWarning> allWarnings = warningService.getActiveWeatherWarnings();
            WeatherWarning warning = allWarnings.stream()
                    .filter(w -> w.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            response.put("success", true);
            response.put("data", Map.of("warning", warning));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取天气预警详情失败, id: {}", id, e);
            response.put("success", false);
            response.put("message", "获取天气预警详情失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 天气对比接口
     */
    @GetMapping("/compare")
    @ApiOperation(value = "天气对比", notes = "比较两个地点的实时天气信息")
    public ResponseEntity<Map<String, Object>> weatherCompare(
            @ApiParam(value = "地点1代码")
            @RequestParam(required = false) String location1,
            @ApiParam(value = "地点2代码")
            @RequestParam(required = false) String location2) {

        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> data = new HashMap<>();

            if (location1 != null && !location1.isEmpty()) {
                WeatherData weather1 = weatherService.getLatestWeather(location1);
                data.put("weather1", weather1);
                data.put("location1", location1);
            }

            if (location2 != null && !location2.isEmpty()) {
                WeatherData weather2 = weatherService.getLatestWeather(location2);
                data.put("weather2", weather2);
                data.put("location2", location2);
            }

            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("天气对比失败, location1: {}, location2: {}", location1, location2, e);
            response.put("success", false);
            response.put("message", "天气对比失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
