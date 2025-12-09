package com.hainan.weather.controller;

import com.hainan.weather.entity.AttractionStatus;
import com.hainan.weather.service.AttractionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attractions")
@Api(tags = "景点信息API", description = "提供景点状态相关信息查询")
@Slf4j
public class AttractionController {

    @Autowired
    private AttractionService attractionService;

    /**
     * 景点状态总览接口
     */
    @GetMapping("/status")
    @ApiOperation(value = "获取景点状态总览", notes = "获取所有景点状态信息及关闭景点统计")
    public ResponseEntity<Map<String, Object>> attractionsStatus() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<AttractionStatus> allAttractions = attractionService.getAllAttractions();
            List<AttractionStatus> closedAttractions = attractionService.getClosedOrLimitedAttractions();

            Map<String, Object> data = new HashMap<>();
            data.put("allAttractions", allAttractions);
            data.put("closedAttractions", closedAttractions);
            data.put("closedCount", closedAttractions != null ? closedAttractions.size() : 0);

            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取景点状态总览失败", e);
            response.put("success", false);
            response.put("message", "获取景点状态总览失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 按地区查看景点状态接口
     */
    @GetMapping("/by-location")
    @ApiOperation(value = "按地区获取景点状态", notes = "根据地点代码获取该地区的景点状态信息")
    public ResponseEntity<Map<String, Object>> attractionsByLocation(
            @ApiParam(value = "地点代码，默认为三亚(SANYA)", defaultValue = "SANYA")
            @RequestParam(defaultValue = "SANYA") String locationCode) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<AttractionStatus> attractions = attractionService.getAttractionsByLocation(locationCode);

            response.put("success", true);
            response.put("data", Map.of(
                "attractions", attractions,
                "locationCode", locationCode
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("按地区获取景点状态失败, locationCode: {}", locationCode, e);
            response.put("success", false);
            response.put("message", "按地区获取景点状态失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 景点搜索接口
     */
    @GetMapping("/search")
    @ApiOperation(value = "搜索景点", notes = "根据名称或状态搜索景点")
    public ResponseEntity<Map<String, Object>> attractionSearch(
            @ApiParam(value = "景点名称")
            @RequestParam(required = false) String name,
            @ApiParam(value = "景点状态")
            @RequestParam(required = false) String status) {

        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("searchName", name);
            data.put("searchStatus", status);

            if (name != null && !name.isEmpty()) {
                List<AttractionStatus> results = attractionService.searchAttractions(name);
                data.put("searchResults", results);
            } else if (status != null && !status.isEmpty()) {
                List<AttractionStatus> results = attractionService.getAttractionsByStatus(status);
                data.put("searchResults", results);
            }

            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("搜索景点失败, name: {}, status: {}", name, status, e);
            response.put("success", false);
            response.put("message", "搜索景点失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 景点详情接口
     */
    @GetMapping("/detail")
    @ApiOperation(value = "获取景点详情", notes = "根据景点名称获取详细信息")
    public ResponseEntity<Map<String, Object>> attractionDetail(
            @ApiParam(value = "景点名称", required = true)
            @RequestParam String name) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<AttractionStatus> attractions = attractionService.searchAttractions(name);
            AttractionStatus attraction = null;
            if (attractions != null && !attractions.isEmpty()) {
                attraction = attractions.get(0);
            }

            response.put("success", true);
            response.put("data", Map.of("attraction", attraction));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取景点详情失败, name: {}", name, e);
            response.put("success", false);
            response.put("message", "获取景点详情失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 今日开放景点接口
     */
    @GetMapping("/open-today")
    @ApiOperation(value = "获取今日开放景点", notes = "获取当前时间正在开放的景点")
    public ResponseEntity<Map<String, Object>> openToday() {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取开放状态的景点
            List<AttractionStatus> openAttractions = attractionService.getAttractionsByStatus("OPEN");
            LocalTime now = LocalTime.now();

            // 筛选当前时间在开放时间内的景点
            List<AttractionStatus> nowOpenAttractions = openAttractions.stream()
                    .filter(a -> {
                        LocalTime openTime = a.getOpenTime();
                        LocalTime closeTime = a.getCloseTime();
                        return openTime != null && closeTime != null &&
                                now.isAfter(openTime) && now.isBefore(closeTime);
                    })
                    .collect(Collectors.toList());

            response.put("success", true);
            response.put("data", Map.of("nowOpenAttractions", nowOpenAttractions));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取今日开放景点失败", e);
            response.put("success", false);
            response.put("message", "获取今日开放景点失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 景点推荐接口
     */
    @GetMapping("/recommendations")
    @ApiOperation(value = "获取景点推荐", notes = "根据天气条件推荐适合的景点")
    public ResponseEntity<Map<String, Object>> weatherRecommendations() {
        Map<String, Object> response = new HashMap<>();
        // 这里可以根据天气条件推荐适合的景点
        response.put("success", true);
        response.put("message", "Weather-based attraction recommendations");
        return ResponseEntity.ok(response);
    }

    /**
     * 景点地图接口
     */
    @GetMapping("/map")
    @ApiOperation(value = "获取景点地图数据", notes = "获取所有景点的位置信息用于地图展示")
    public ResponseEntity<Map<String, Object>> attractionsMap() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<AttractionStatus> allAttractions = attractionService.getAllAttractions();

            response.put("success", true);
            response.put("data", Map.of("attractions", allAttractions));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取景点地图数据失败", e);
            response.put("success", false);
            response.put("message", "获取景点地图数据失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
