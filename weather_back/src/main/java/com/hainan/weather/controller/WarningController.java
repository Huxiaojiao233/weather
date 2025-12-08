package com.hainan.weather.controller;

import com.hainan.weather.entity.AttractionStatus;
import com.hainan.weather.entity.TrafficStatus;
import com.hainan.weather.entity.WeatherWarning;
import com.hainan.weather.entity.ComprehensiveWarning;
import com.hainan.weather.service.WarningService;
import com.hainan.weather.service.WeatherService;
import com.hainan.weather.service.TrafficService;
import com.hainan.weather.service.AttractionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warnings")
@Api(tags = "预警信息API", description = "提供预警相关信息查询和管理")
@Slf4j
public class WarningController {

    @Autowired
    private WarningService warningService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private TrafficService trafficService;

    @Autowired
    private AttractionService attractionService;

    /**
     * 综合预警列表接口
     */
    @GetMapping("/comprehensive")
    @ApiOperation(value = "获取综合预警列表", notes = "获取所有已发布的综合预警信息")
    public ResponseEntity<Map<String, Object>> comprehensiveWarnings() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ComprehensiveWarning> publishedWarnings = warningService.getPublishedComprehensiveWarnings();

            response.put("success", true);
            response.put("data", Map.of("publishedWarnings", publishedWarnings));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取综合预警列表失败", e);
            response.put("success", false);
            response.put("message", "获取综合预警列表失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 查看综合预警详情接口
     */
    @GetMapping("/comprehensive/{id}")
    @ApiOperation(value = "获取综合预警详情", notes = "根据预警ID获取详细的综合预警信息")
    public ResponseEntity<Map<String, Object>> comprehensiveWarningDetail(
            @ApiParam(value = "预警ID", required = true)
            @PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<ComprehensiveWarning> allWarnings = warningService.getPublishedComprehensiveWarnings();
            ComprehensiveWarning warning = allWarnings.stream()
                    .filter(w -> w.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            Map<String, Object> data = new HashMap<>();
            data.put("warning", warning);

            if (warning != null && warning.getAffectedLocations() != null) {
                String[] locations = warning.getAffectedLocations().split(",");
                data.put("affectedLocationsList", Arrays.asList(locations));
            }

            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取综合预警详情失败, id: {}", id, e);
            response.put("success", false);
            response.put("message", "获取综合预警详情失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 预警信息汇总接口
     */
    @GetMapping("/summary")
    @ApiOperation(value = "获取预警信息汇总", notes = "获取各类预警统计信息")
    public ResponseEntity<Map<String, Object>> warningSummary() {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取各类预警统计
            List<WeatherWarning> typhoonWarnings = warningService.getActiveWarningsByType("台风");
            List<WeatherWarning> rainWarnings = warningService.getActiveWarningsByType("暴雨");
            List<WeatherWarning> thunderWarnings = warningService.getActiveWarningsByType("雷电");

            // 获取异常统计
            int abnormalFlightCount = trafficService.getAbnormalFlightCount();
            int abnormalTrainCount = trafficService.getAbnormalTrainCount();
            int closedAttractionCount = attractionService.getClosedAttractionCount();

            Map<String, Object> data = new HashMap<>();
            data.put("typhoonWarnings", typhoonWarnings);
            data.put("rainWarnings", rainWarnings);
            data.put("thunderWarnings", thunderWarnings);
            data.put("abnormalFlightCount", abnormalFlightCount);
            data.put("abnormalTrainCount", abnormalTrainCount);
            data.put("closedAttractionCount", closedAttractionCount);

            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取预警信息汇总失败", e);
            response.put("success", false);
            response.put("message", "获取预警信息汇总失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 预警影响分析接口
     */
    @GetMapping("/analysis")
    @ApiOperation(value = "获取预警影响分析", notes = "分析特定预警对交通和景点的影响")
    public ResponseEntity<Map<String, Object>> warningAnalysis(
            @ApiParam(value = "预警ID")
            @RequestParam(required = false) String warningId) {

        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> data = new HashMap<>();

            if (warningId != null && !warningId.isEmpty()) {
                // 分析特定预警的影响
                List<WeatherWarning> allWarnings = warningService.getActiveWeatherWarnings();
                WeatherWarning selectedWarning = allWarnings.stream()
                        .filter(w -> w.getId().equals(Long.parseLong(warningId)))
                        .findFirst()
                        .orElse(null);

                if (selectedWarning != null) {
                    data.put("selectedWarning", selectedWarning);
                    // 获取受影响的交通和景点
                    List<TrafficStatus> affectedTraffic = trafficService.getAbnormalTrafficStatus();
                    List<AttractionStatus> affectedAttractions = attractionService.getClosedOrLimitedAttractions();

                    data.put("affectedTraffic", affectedTraffic);
                    data.put("affectedAttractions", affectedAttractions);
                }
            }

            // 获取所有活跃预警供选择
            List<WeatherWarning> allActiveWarnings = warningService.getActiveWeatherWarnings();
            data.put("allWarnings", allActiveWarnings);

            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取预警影响分析失败, warningId: {}", warningId, e);
            response.put("success", false);
            response.put("message", "获取预警影响分析失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 预警订阅接口
     */
    @GetMapping("/subscription")
    @ApiOperation(value = "获取预警订阅信息", notes = "获取预警订阅页面所需数据")
    public ResponseEntity<Map<String, Object>> warningSubscription(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            HttpSession session = request.getSession();
            Long userId = (Long) session.getAttribute("userId");

            // 获取所有预警类型供选择
            List<WeatherWarning> allWarnings = warningService.getActiveWeatherWarnings();

            Map<String, Object> data = new HashMap<>();
            data.put("userId", userId);
            data.put("allWarnings", allWarnings);

            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取预警订阅信息失败", e);
            response.put("success", false);
            response.put("message", "获取预警订阅信息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 接收预警推送接口
     */
    @PostMapping("/receive")
    @ApiOperation(value = "接收预警推送", notes = "接收外部系统推送的预警信息")
    public ResponseEntity<Map<String, Object>> receiveWarning(
            @ApiParam(value = "预警类型", required = true)
            @RequestParam String warningType,
            @ApiParam(value = "预警等级", required = true)
            @RequestParam String warningLevel,
            @ApiParam(value = "预警内容", required = true)
            @RequestParam String content) {

        Map<String, Object> response = new HashMap<>();
        try {
            log.info("收到预警推送 - 类型: {}, 等级: {}, 内容: {}", warningType, warningLevel, content);
            // 这里可以调用Service处理预警接收逻辑

            response.put("success", true);
            response.put("message", "预警接收成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("接收预警推送失败, warningType: {}, warningLevel: {}", warningType, warningLevel, e);
            response.put("success", false);
            response.put("message", "接收预警推送失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
