package com.hainan.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hainan.weather.entity.Location;
import com.hainan.weather.entity.WeatherData;
import com.hainan.weather.entity.WeatherWarning;
import com.hainan.weather.mapper.LocationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

@Service
@Slf4j
public class QWeatherApiService {

    // API配置（写死在代码中）
    private static final String API_BASE_URL = "https://mp52qby2rg.re.qweatherapi.com";
    private static final String API_KEY = "f8626752a27f4789a1df8e692333435d";
    private static final String API_KEY_HEADER = "X-QW-Api-Key";

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private WarningService warningService;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public QWeatherApiService() {
        ClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        this.restTemplate = new RestTemplate(factory);
        this.objectMapper = new ObjectMapper();
    }

    // 或者使用@Autowired注入RestTemplate Bean（如果配置了的话）
    // 当前使用直接创建的方式

    /**
     * 处理可能的gzip压缩响应
     */
    private String decompressResponse(byte[] body, String contentEncoding) {
        try {
            if (contentEncoding != null && contentEncoding.contains("gzip")) {
                GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(body));
                Reader reader = new InputStreamReader(gzipInputStream, "UTF-8");
                StringBuilder sb = new StringBuilder();
                char[] buffer = new char[1024];
                int len;
                while ((len = reader.read(buffer)) > 0) {
                    sb.append(buffer, 0, len);
                }
                reader.close();
                return sb.toString();
            } else {
                return new String(body, "UTF-8");
            }
        } catch (Exception e) {
            log.error("解压响应失败", e);
            return new String(body);
        }
    }

    /**
     * 获取实时天气数据
     */
    public WeatherData fetchWeatherData(Long locationId) {
        try {
            Location location = locationMapper.findById(locationId);
            if (location == null) {
                log.warn("未找到地点信息, locationId: {}", locationId);
                return null;
            }

            // 使用和风天气的location_id字段（数据库中的location_id列）
            String qweatherLocationId = location.getLocationId();
            if (qweatherLocationId == null || qweatherLocationId.isEmpty()) {
                log.warn("地点缺少和风天气location_id, locationId: {}, locationCode: {}", locationId, location.getLocationCode());
                return null;
            }

            String url = API_BASE_URL + "/v7/weather/now?location=" + qweatherLocationId;
            HttpHeaders headers = new HttpHeaders();
            headers.set(API_KEY_HEADER, API_KEY);
            // 明确指定不接受gzip压缩，让服务器返回未压缩的JSON
            headers.set("Accept-Encoding", "identity");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String responseBody = decompressResponse(response.getBody(),
                    response.getHeaders().getFirst("Content-Encoding"));

                JsonNode rootNode = objectMapper.readTree(responseBody);

                if ("200".equals(rootNode.path("code").asText())) {
                    JsonNode nowNode = rootNode.path("now");

                    WeatherData weatherData = new WeatherData();
                    weatherData.setLocationCode(location.getLocationCode());
                    weatherData.setTemperature(new BigDecimal(nowNode.path("temp").asText()));
                    weatherData.setHumidity(new BigDecimal(nowNode.path("humidity").asText()));
                    weatherData.setWindSpeed(new BigDecimal(nowNode.path("windSpeed").asText()));
                    weatherData.setWindDirection(nowNode.path("windDir").asText());
                    weatherData.setPrecipitation(new BigDecimal(nowNode.path("precip").asText()));
                    weatherData.setWeatherCondition(nowNode.path("text").asText());
                    weatherData.setPressure(new BigDecimal(nowNode.path("pressure").asText()));
                    weatherData.setVisibility(new BigDecimal(nowNode.path("vis").asText()));

                    // 解析更新时间
                    String updateTimeStr = rootNode.path("updateTime").asText();
                    if (updateTimeStr != null && !updateTimeStr.isEmpty()) {
                        try {
                            weatherData.setUpdateTime(LocalDateTime.parse(updateTimeStr.replace("+08:00", ""),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
                        } catch (Exception e) {
                            log.warn("解析更新时间失败，使用当前时间", e);
                            weatherData.setUpdateTime(LocalDateTime.now());
                        }
                    } else {
                        weatherData.setUpdateTime(LocalDateTime.now());
                    }

                    weatherData.setDataDate(LocalDate.now());

                    return weatherData;
                } else {
                    log.warn("API返回错误, code: {}, message: {}, locationId: {}",
                        rootNode.path("code").asText(),
                        rootNode.path("message").asText(),
                        qweatherLocationId);
                }
            } else {
                log.warn("API请求失败, status: {}, locationId: {}",
                    response.getStatusCode(), qweatherLocationId);
            }
        } catch (Exception e) {
            log.error("获取天气数据失败, locationId: {}", locationId, e);
        }
        return null;
    }

    /**
     * 获取天气预警数据
     */
    public List<WeatherWarning> fetchWeatherWarnings(Long locationId) {
        List<WeatherWarning> warnings = new ArrayList<>();
        try {
            Location location = locationMapper.findById(locationId);
            if (location == null || location.getLatitude() == null || location.getLongitude() == null) {
                log.warn("未找到地点信息或缺少经纬度, locationId: {}", locationId);
                return warnings;
            }

            String url = API_BASE_URL + "/weatheralert/v1/current/" +
                        location.getLatitude() + "/" + location.getLongitude();
            HttpHeaders headers = new HttpHeaders();
            headers.set(API_KEY_HEADER, API_KEY);
            // 明确指定不接受gzip压缩
            headers.set("Accept-Encoding", "identity");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String responseBody = decompressResponse(response.getBody(),
                    response.getHeaders().getFirst("Content-Encoding"));
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode alertsNode = rootNode.path("alerts");

                if (alertsNode.isArray()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

                    for (JsonNode alertNode : alertsNode) {
                        WeatherWarning warning = new WeatherWarning();
                        warning.setLocationCode(location.getLocationCode());

                        // 预警类型
                        JsonNode eventTypeNode = alertNode.path("eventType");
                        if (eventTypeNode.has("name")) {
                            warning.setWarningType(eventTypeNode.path("name").asText());
                        }

                        // 预警等级（severity映射到warningLevel）
                        String severity = alertNode.path("severity").asText();
                        if (severity != null && !severity.isEmpty()) {
                            // 将severity映射到中文等级
                            switch (severity.toLowerCase()) {
                                case "extreme":
                                    warning.setWarningLevel("红色");
                                    break;
                                case "severe":
                                    warning.setWarningLevel("橙色");
                                    break;
                                case "moderate":
                                    warning.setWarningLevel("黄色");
                                    break;
                                case "minor":
                                    warning.setWarningLevel("蓝色");
                                    break;
                                default:
                                    warning.setWarningLevel("蓝色");
                            }
                        } else {
                            warning.setWarningLevel("蓝色");
                        }

                        // 标题
                        warning.setTitle(alertNode.path("headline").asText());

                        // 内容
                        String description = alertNode.path("description").asText();
                        String criteria = alertNode.path("criteria").asText();
                        String instruction = alertNode.path("instruction").asText();
                        StringBuilder content = new StringBuilder();
                        if (description != null && !description.isEmpty()) {
                            content.append(description);
                        }
                        if (criteria != null && !criteria.isEmpty()) {
                            content.append("\n\n").append("标准：").append(criteria);
                        }
                        if (instruction != null && !instruction.isEmpty()) {
                            content.append("\n\n").append("防御指南：").append(instruction);
                        }
                        warning.setContent(content.toString());

                        // 发布时间
                        String issuedTimeStr = alertNode.path("issuedTime").asText();
                        if (issuedTimeStr != null && !issuedTimeStr.isEmpty()) {
                            try {
                                warning.setIssueTime(LocalDateTime.parse(issuedTimeStr.replace("+08:00", ""), formatter));
                            } catch (Exception e) {
                                log.warn("解析发布时间失败", e);
                                warning.setIssueTime(LocalDateTime.now());
                            }
                        } else {
                            warning.setIssueTime(LocalDateTime.now());
                        }

                        // 生效时间
                        String effectiveTimeStr = alertNode.path("effectiveTime").asText();
                        if (effectiveTimeStr != null && !effectiveTimeStr.isEmpty()) {
                            try {
                                warning.setEffectiveTime(LocalDateTime.parse(effectiveTimeStr.replace("+08:00", ""), formatter));
                            } catch (Exception e) {
                                warning.setEffectiveTime(warning.getIssueTime());
                            }
                        } else {
                            warning.setEffectiveTime(warning.getIssueTime());
                        }

                        // 过期时间
                        String expireTimeStr = alertNode.path("expireTime").asText();
                        if (expireTimeStr != null && !expireTimeStr.isEmpty()) {
                            try {
                                warning.setExpireTime(LocalDateTime.parse(expireTimeStr.replace("+08:00", ""), formatter));
                            } catch (Exception e) {
                                // 如果没有过期时间，默认24小时后过期
                                warning.setExpireTime(warning.getIssueTime().plusHours(24));
                            }
                        } else {
                            warning.setExpireTime(warning.getIssueTime().plusHours(24));
                        }

                        warning.setStatus("ACTIVE");

                        warnings.add(warning);
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取天气预警数据失败, locationId: {}", locationId, e);
        }
        return warnings;
    }

    /**
     * 同步所有地点的天气和预警数据
     */
    public void syncAllLocations() {
        try {
            List<Location> locations = locationMapper.findAll();
            int weatherCount = 0;
            int warningCount = 0;

            for (Location location : locations) {
                // 只同步启用的地点
                if (location.getStatus() != null && location.getStatus() == 1) {
                    // 同步天气数据
                    WeatherData weatherData = fetchWeatherData(location.getId());
                    if (weatherData != null) {
                        weatherService.saveWeatherData(weatherData);
                        weatherCount++;
                    }

                    // 同步预警数据
                    List<WeatherWarning> warnings = fetchWeatherWarnings(location.getId());
                    for (WeatherWarning warning : warnings) {
                        // 检查是否已存在相同的预警（根据标题和发布时间判断）
                        List<WeatherWarning> existingWarnings = warningService.getActiveWarningsByLocation(location.getLocationCode());
                        boolean exists = false;
                        for (WeatherWarning existing : existingWarnings) {
                            if (existing.getTitle() != null && existing.getTitle().equals(warning.getTitle()) &&
                                existing.getIssueTime() != null && warning.getIssueTime() != null &&
                                existing.getIssueTime().equals(warning.getIssueTime())) {
                                exists = true;
                                break;
                            }
                        }

                        if (!exists) {
                            warningService.saveWeatherWarning(warning, null);
                            warningCount++;
                        }
                    }
                }
            }

            log.info("同步完成，天气数据: {} 条，预警数据: {} 条", weatherCount, warningCount);
        } catch (Exception e) {
            log.error("同步所有地点数据失败", e);
        }
    }
}
