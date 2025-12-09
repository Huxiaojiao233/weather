package com.hainan.weather.controller;

import com.hainan.weather.dto.UserLoginDTO;
import com.hainan.weather.dto.WarningPublishDTO;
import com.hainan.weather.entity.*;
import com.hainan.weather.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Api(tags = "管理员API", description = "提供管理员相关操作接口")
@Slf4j
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private WarningService warningService;

    @Autowired
    private TrafficService trafficService;

    @Autowired
    private AttractionService attractionService;

    @Autowired
    private SystemService systemService;

    /**
     * 管理员登录接口
     */
    @PostMapping("/login")
    @ApiOperation(value = "管理员登录", notes = "验证管理员身份并创建会话")
    public ResponseEntity<Map<String, Object>> login(
            @ApiParam(value = "登录信息", required = true)
            @RequestBody UserLoginDTO loginDTO,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.login(loginDTO, request.getRemoteAddr());
            if (user != null && "ADMIN".equals(user.getRole())) {
                HttpSession session = request.getSession(true); // 强制创建新Session
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("isAdmin", true);
                
                // 设置Session超时时间（30分钟）
                session.setMaxInactiveInterval(30 * 60);
                
                log.info("管理员登录成功，Session ID: {}, User ID: {}", session.getId(), user.getId());

                systemService.logOperation(user.getId(), "LOGIN", "ADMIN",
                        "管理员登录系统", request.getRemoteAddr());

                response.put("success", true);
                response.put("message", "登录成功");
                response.put("user", user);
                response.put("sessionId", session.getId()); // 调试用，可以移除
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "用户名或密码错误，或非管理员账户");
                return ResponseEntity.status(401).body(response);
            }
        } catch (Exception e) {
            log.error("管理员登录失败", e);
            response.put("success", false);
            response.put("message", "登录失败，请稍后重试");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 管理员退出接口
     */
    @GetMapping("/logout")
    @ApiOperation(value = "管理员退出", notes = "退出当前管理员会话")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            systemService.logOperation(userId, "LOGOUT", "ADMIN",
                    "管理员退出系统", request.getRemoteAddr());
        }

        session.invalidate();
        response.put("success", true);
        response.put("message", "退出成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 管理员仪表盘数据接口
     */
    @GetMapping("/dashboard")
    @ApiOperation(value = "获取仪表盘数据", notes = "获取管理员仪表盘统计数据")
    public ResponseEntity<Map<String, Object>> dashboard(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (!checkAdminLogin(request)) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.status(401).body(response);
        }

        try {
            HttpSession session = request.getSession();
            Long userId = (Long) session.getAttribute("userId");

            // 获取系统统计数据
            int activeWarningCount = warningService.getActiveWarningCount();
            int publishedWarningCount = warningService.getPublishedComprehensiveWarningCount();
            int abnormalFlightCount = trafficService.getAbnormalFlightCount();
            int closedAttractionCount = attractionService.getClosedAttractionCount();

            // 获取最近的操作日志
            List<SystemLog> recentLogs = systemService.getRecentLogs(10);

            Map<String, Object> data = new HashMap<>();
            data.put("activeWarningCount", activeWarningCount);
            data.put("publishedWarningCount", publishedWarningCount);
            data.put("abnormalFlightCount", abnormalFlightCount);
            data.put("closedAttractionCount", closedAttractionCount);
            data.put("recentLogs", recentLogs);

            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取仪表盘数据失败", e);
            response.put("success", false);
            response.put("message", "获取仪表盘数据失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 用户管理数据接口
     */
    @GetMapping("/users")
    @ApiOperation(value = "获取用户列表", notes = "获取所有用户信息")
    public ResponseEntity<Map<String, Object>> userManagement(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (!checkAdminLogin(request)) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.status(401).body(response);
        }

        try {
            List<User> allUsers = userService.getAllUsers();

            response.put("success", true);
            response.put("data", Map.of("users", allUsers));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            response.put("success", false);
            response.put("message", "获取用户列表失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 天气数据管理接口
     */
    @GetMapping("/weather-data")
    @ApiOperation(value = "获取天气数据", notes = "获取所有地点的最新天气数据")
    public ResponseEntity<Map<String, Object>> weatherDataManagement(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (!checkAdminLogin(request)) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.status(401).body(response);
        }

        try {
            // 获取所有地点的最新天气数据
            List<WeatherData> allWeatherData = weatherService.getAllLatestWeather();

            response.put("success", true);
            response.put("data", Map.of("weatherData", allWeatherData));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取天气数据失败", e);
            response.put("success", false);
            response.put("message", "获取天气数据失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 预警管理接口
     */
    @GetMapping("/warnings")
    @ApiOperation(value = "获取预警管理数据", notes = "获取所有预警信息")
    public ResponseEntity<Map<String, Object>> warningManagement(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (!checkAdminLogin(request)) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.status(401).body(response);
        }

        try {
            // 获取所有预警
            List<WeatherWarning> allWarnings = warningService.getActiveWeatherWarnings();
            List<ComprehensiveWarning> draftWarnings = warningService.getDraftComprehensiveWarnings();
            List<ComprehensiveWarning> publishedWarnings = warningService.getPublishedComprehensiveWarnings();

            Map<String, Object> data = new HashMap<>();
            data.put("allWarnings", allWarnings);
            data.put("draftWarnings", draftWarnings);
            data.put("publishedWarnings", publishedWarnings);

            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取预警管理数据失败", e);
            response.put("success", false);
            response.put("message", "获取预警管理数据失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 创建综合预警页面数据接口
     */
    @GetMapping("/warnings/create")
    @ApiOperation(value = "获取创建预警页面数据", notes = "获取创建综合预警所需的数据")
    public ResponseEntity<Map<String, Object>> createComprehensiveWarningPage(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (!checkAdminLogin(request)) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.status(401).body(response);
        }

        try {
            // 获取活跃的天气预警供关联
            List<WeatherWarning> activeWarnings = warningService.getActiveWeatherWarnings();

            response.put("success", true);
            response.put("data", Map.of("activeWarnings", activeWarnings));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取创建预警页面数据失败", e);
            response.put("success", false);
            response.put("message", "获取创建预警页面数据失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 创建综合预警接口
     */
    @PostMapping("/warnings/create")
    @ApiOperation(value = "创建综合预警", notes = "创建新的综合预警草稿")
    public ResponseEntity<Map<String, Object>> createComprehensiveWarning(
            @ApiParam(value = "预警发布信息", required = true)
            @RequestBody WarningPublishDTO dto,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        if (!checkAdminLogin(request)) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.status(401).body(response);
        }

        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");

        try {
            boolean success = warningService.createComprehensiveWarning(dto, userId);
            if (success) {
                response.put("success", true);
                response.put("message", "综合预警草稿创建成功");
            } else {
                response.put("success", false);
                response.put("message", "创建综合预警失败");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("创建综合预警失败", e);
            response.put("success", false);
            response.put("message", "创建综合预警失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 发布综合预警接口
     */
    @PostMapping("/warnings/publish")
    @ApiOperation(value = "发布综合预警", notes = "将综合预警草稿发布为正式预警")
    public ResponseEntity<Map<String, Object>> publishComprehensiveWarning(
            @ApiParam(value = "预警ID", required = true)
            @RequestParam Long warningId,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        if (!checkAdminLogin(request)) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.status(401).body(response);
        }

        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");

        try {
            boolean success = warningService.publishComprehensiveWarning(warningId, userId);
            if (success) {
                response.put("success", true);
                response.put("message", "综合预警发布成功");
            } else {
                response.put("success", false);
                response.put("message", "发布综合预警失败");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("发布综合预警失败", e);
            response.put("success", false);
            response.put("message", "发布综合预警失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 交通状态管理接口
     */
    @GetMapping("/traffic")
    @ApiOperation(value = "获取交通状态管理数据", notes = "获取航班和火车状态信息")
    public ResponseEntity<Map<String, Object>> trafficManagement(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (!checkAdminLogin(request)) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.status(401).body(response);
        }

        try {
            List<TrafficStatus> flights = trafficService.getFlightStatus();
            List<TrafficStatus> trains = trafficService.getTrainStatus();

            Map<String, Object> data = new HashMap<>();
            data.put("flights", flights);
            data.put("trains", trains);

            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取交通状态管理数据失败", e);
            response.put("success", false);
            response.put("message", "获取交通状态管理数据失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 景点状态管理接口
     */
    @GetMapping("/attractions")
    @ApiOperation(value = "获取景点状态管理数据", notes = "获取所有景点状态信息")
    public ResponseEntity<Map<String, Object>> attractionManagement(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (!checkAdminLogin(request)) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.status(401).body(response);
        }

        try {
            List<AttractionStatus> allAttractions = attractionService.getAllAttractions();

            response.put("success", true);
            response.put("data", Map.of("attractions", allAttractions));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取景点状态管理数据失败", e);
            response.put("success", false);
            response.put("message", "获取景点状态管理数据失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 系统日志接口
     */
    @GetMapping("/logs")
    @ApiOperation(value = "获取系统日志", notes = "获取所有系统操作日志")
    public ResponseEntity<Map<String, Object>> systemLogs(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (!checkAdminLogin(request)) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.status(401).body(response);
        }

        try {
            List<SystemLog> allLogs = systemService.getAllLogs();

            response.put("success", true);
            response.put("data", Map.of("logs", allLogs));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取系统日志失败", e);
            response.put("success", false);
            response.put("message", "获取系统日志失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 更新天气数据接口
     */
    @PostMapping("/weather-data/update")
    @ApiOperation(value = "更新天气数据", notes = "更新指定地点的天气数据")
    public ResponseEntity<Map<String, Object>> updateWeatherData(
            @RequestBody WeatherData weatherData,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        if (!checkAdminLogin(request)) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.status(401).body(response);
        }
        try {
            HttpSession session = request.getSession();
            Long userId = (Long) session.getAttribute("userId");
            boolean success = weatherService.updateWeatherData(weatherData);
            if (success) {
                systemService.logOperation(userId, "UPDATE", "WEATHER", 
                        "更新天气数据: " + weatherData.getLocationCode(), request.getRemoteAddr());
            }
            response.put("success", success);
            response.put("message", success ? "更新成功" : "更新失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("更新天气数据失败", e);
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 更新交通状态接口
     */
    @PostMapping("/traffic/update")
    @ApiOperation(value = "更新交通状态", notes = "更新交通状态信息")
    public ResponseEntity<Map<String, Object>> updateTrafficStatus(
            @RequestParam Long id,
            @RequestBody com.hainan.weather.dto.TrafficUpdateDTO dto,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        if (!checkAdminLogin(request)) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.status(401).body(response);
        }
        try {
            HttpSession session = request.getSession();
            Long userId = (Long) session.getAttribute("userId");
            boolean success = trafficService.updateTrafficStatus(id, dto, userId);
            response.put("success", success);
            response.put("message", success ? "更新成功" : "更新失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("更新交通状态失败", e);
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 添加交通状态接口
     */
    @PostMapping("/traffic/add")
    @ApiOperation(value = "添加交通状态", notes = "添加新的交通状态信息")
    public ResponseEntity<Map<String, Object>> addTrafficStatus(
            @RequestBody com.hainan.weather.dto.TrafficUpdateDTO dto,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        if (!checkAdminLogin(request)) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.status(401).body(response);
        }
        try {
            HttpSession session = request.getSession();
            Long userId = (Long) session.getAttribute("userId");
            boolean success = trafficService.addTrafficStatus(dto, userId);
            response.put("success", success);
            response.put("message", success ? "添加成功" : "添加失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("添加交通状态失败", e);
            response.put("success", false);
            response.put("message", "添加失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 删除交通状态接口
     */
    @DeleteMapping("/traffic/delete")
    @ApiOperation(value = "删除交通状态", notes = "删除指定的交通状态")
    public ResponseEntity<Map<String, Object>> deleteTrafficStatus(
            @RequestParam Long id,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        if (!checkAdminLogin(request)) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.status(401).body(response);
        }
        try {
            HttpSession session = request.getSession();
            Long userId = (Long) session.getAttribute("userId");
            boolean success = trafficService.deleteTrafficStatus(id, userId);
            response.put("success", success);
            response.put("message", success ? "删除成功" : "删除失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("删除交通状态失败", e);
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 更新景点状态接口
     */
    @PostMapping("/attractions/update")
    @ApiOperation(value = "更新景点状态", notes = "更新景点状态信息")
    public ResponseEntity<Map<String, Object>> updateAttractionStatus(
            @RequestParam Long id,
            @RequestBody com.hainan.weather.dto.AttractionUpdateDTO dto,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        if (!checkAdminLogin(request)) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.status(401).body(response);
        }
        try {
            HttpSession session = request.getSession();
            Long userId = (Long) session.getAttribute("userId");
            boolean success = attractionService.updateAttraction(id, dto, userId);
            response.put("success", success);
            response.put("message", success ? "更新成功" : "更新失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("更新景点状态失败", e);
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 添加景点接口
     */
    @PostMapping("/attractions/add")
    @ApiOperation(value = "添加景点", notes = "添加新的景点信息")
    public ResponseEntity<Map<String, Object>> addAttraction(
            @RequestBody com.hainan.weather.dto.AttractionUpdateDTO dto,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        if (!checkAdminLogin(request)) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.status(401).body(response);
        }
        try {
            HttpSession session = request.getSession();
            Long userId = (Long) session.getAttribute("userId");
            boolean success = attractionService.addAttraction(dto, userId);
            response.put("success", success);
            response.put("message", success ? "添加成功" : "添加失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("添加景点失败", e);
            response.put("success", false);
            response.put("message", "添加失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 删除景点接口
     */
    @DeleteMapping("/attractions/delete")
    @ApiOperation(value = "删除景点", notes = "删除指定的景点")
    public ResponseEntity<Map<String, Object>> deleteAttraction(
            @RequestParam Long id,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        if (!checkAdminLogin(request)) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.status(401).body(response);
        }
        try {
            HttpSession session = request.getSession();
            Long userId = (Long) session.getAttribute("userId");
            boolean success = attractionService.deleteAttraction(id, userId);
            response.put("success", success);
            response.put("message", success ? "删除成功" : "删除失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("删除景点失败", e);
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 检查管理员登录状态
     */
    private boolean checkAdminLogin(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 不创建新Session，只获取现有Session
        if (session == null) {
            log.warn("Session不存在，用户未登录");
            return false;
        }
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        Long userId = (Long) session.getAttribute("userId");
        log.debug("检查登录状态 - Session ID: {}, User ID: {}, IsAdmin: {}", session.getId(), userId, isAdmin);
        return isAdmin != null && isAdmin;
    }
}
