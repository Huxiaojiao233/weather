package com.hainan.weather.controller;

import com.hainan.weather.entity.TrafficStatus;
import com.hainan.weather.service.TrafficService;
import com.hainan.weather.service.SystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/traffic")
@Api(tags = "交通信息API", description = "提供交通状态相关信息查询")
@Slf4j
public class TrafficController {

    @Autowired
    private TrafficService trafficService;

    @Autowired
    private SystemService systemService;

    /**
     * 航班状态查询接口
     */
    @GetMapping("/flights")
    @ApiOperation(value = "获取航班状态", notes = "获取所有航班状态信息")
    public ResponseEntity<Map<String, Object>> flightStatus() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<TrafficStatus> flights = trafficService.getFlightStatus();

            response.put("success", true);
            response.put("data", Map.of("flights", flights));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取航班状态失败", e);
            response.put("success", false);
            response.put("message", "获取航班状态失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 火车状态查询接口
     */
    @GetMapping("/trains")
    @ApiOperation(value = "获取火车状态", notes = "获取所有火车状态信息")
    public ResponseEntity<Map<String, Object>> trainStatus() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<TrafficStatus> trains = trafficService.getTrainStatus();

            response.put("success", true);
            response.put("data", Map.of("trains", trains));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取火车状态失败", e);
            response.put("success", false);
            response.put("message", "获取火车状态失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 交通状态查询接口（按条件）
     */
    @GetMapping("/search")
    @ApiOperation(value = "条件查询交通状态", notes = "根据类型、城市和时间范围查询交通状态")
    public ResponseEntity<Map<String, Object>> trafficSearch(
            @ApiParam(value = "交通类型(FLIGHT/TRAIN)")
            @RequestParam(required = false) String type,
            @ApiParam(value = "城市")
            @RequestParam(required = false) String city,
            @ApiParam(value = "开始日期(yyyy-MM-dd)")
            @RequestParam(required = false) String startDate,
            @ApiParam(value = "结束日期(yyyy-MM-dd)")
            @RequestParam(required = false) String endDate) {

        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("type", type);
            data.put("city", city);
            data.put("startDate", startDate);
            data.put("endDate", endDate);

            if (type != null && city != null && startDate != null && endDate != null) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime startTime = LocalDateTime.parse(startDate + " 00:00", formatter);
                    LocalDateTime endTime = LocalDateTime.parse(endDate + " 23:59", formatter);

                    List<TrafficStatus> results = null;
                    if ("FLIGHT".equals(type)) {
                        results = trafficService.getFlightsByCityAndTime(city, startTime, endTime);
                    } else if ("TRAIN".equals(type)) {
                        results = trafficService.getTrainsByCityAndTime(city, startTime, endTime);
                    }

                    data.put("searchResults", results);
                } catch (Exception e) {
                    log.error("查询交通状态失败", e);
                    response.put("success", false);
                    response.put("message", "查询参数格式错误");
                    return ResponseEntity.status(400).body(response);
                }
            }

            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("条件查询交通状态失败", e);
            response.put("success", false);
            response.put("message", "条件查询交通状态失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 交通状态详情接口
     */
    @GetMapping("/detail")
    @ApiOperation(value = "获取交通状态详情", notes = "根据类型和编号获取交通状态详情")
    public ResponseEntity<Map<String, Object>> trafficDetail(
            @ApiParam(value = "交通类型", required = true)
            @RequestParam String type,
            @ApiParam(value = "交通编号", required = true)
            @RequestParam String number) {

        Map<String, Object> response = new HashMap<>();
        try {
            TrafficStatus status = trafficService.getTrafficStatus(type, number);

            response.put("success", true);
            response.put("data", Map.of("status", status));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取交通状态详情失败, type: {}, number: {}", type, number, e);
            response.put("success", false);
            response.put("message", "获取交通状态详情失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 实时交通信息接口
     */
    @GetMapping("/realtime")
    @ApiOperation(value = "获取实时交通信息", notes = "获取实时交通信息")
    public ResponseEntity<Map<String, Object>> realtimeTraffic() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Real-time traffic information");
        return ResponseEntity.ok(response);
    }

    /**
     * 交通影响分析接口
     */
    @GetMapping("/impact")
    @ApiOperation(value = "获取交通影响分析", notes = "获取异常交通状态及其统计信息")
    public ResponseEntity<Map<String, Object>> trafficImpact() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<TrafficStatus> abnormalTraffic = trafficService.getAbnormalTrafficStatus();

            // 统计不同类型的影响
            long delayedFlights = abnormalTraffic.stream()
                    .filter(t -> "FLIGHT".equals(t.getType()) && "DELAYED".equals(t.getStatus()))
                    .count();
            long cancelledFlights = abnormalTraffic.stream()
                    .filter(t -> "FLIGHT".equals(t.getType()) && "CANCELLED".equals(t.getStatus()))
                    .count();
            long delayedTrains = abnormalTraffic.stream()
                    .filter(t -> "TRAIN".equals(t.getType()) && "DELAYED".equals(t.getStatus()))
                    .count();
            long cancelledTrains = abnormalTraffic.stream()
                    .filter(t -> "TRAIN".equals(t.getType()) && "CANCELLED".equals(t.getStatus()))
                    .count();

            Map<String, Object> data = new HashMap<>();
            data.put("abnormalTraffic", abnormalTraffic);
            data.put("delayedFlights", delayedFlights);
            data.put("cancelledFlights", cancelledFlights);
            data.put("delayedTrains", delayedTrains);
            data.put("cancelledTrains", cancelledTrains);

            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取交通影响分析失败", e);
            response.put("success", false);
            response.put("message", "获取交通影响分析失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
