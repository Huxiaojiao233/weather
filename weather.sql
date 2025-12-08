/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80039
 Source Host           : localhost:3306
 Source Schema         : weather

 Target Server Type    : MySQL
 Target Server Version : 80039
 File Encoding         : 65001

 Date: 07/12/2025 18:26:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for attraction_status
-- ----------------------------
DROP TABLE IF EXISTS `attraction_status`;
CREATE TABLE `attraction_status`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `location_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `open_status` enum('OPEN','CLOSED','LIMITED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'OPEN',
  `close_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `open_time` time(0) NULL DEFAULT NULL,
  `close_time` time(0) NULL DEFAULT NULL,
  `updated_by` bigint(0) NULL DEFAULT NULL,
  `updated_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `location_code`(`location_code`) USING BTREE,
  INDEX `updated_by`(`updated_by`) USING BTREE,
  CONSTRAINT `attraction_status_ibfk_1` FOREIGN KEY (`location_code`) REFERENCES `locations` (`location_code`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `attraction_status_ibfk_2` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attraction_status
-- ----------------------------
INSERT INTO `attraction_status` VALUES (1, '天涯海角', 'SANYA', '三亚市天涯区天涯镇', 'OPEN', NULL, '08:00:00', '18:00:00', 1, '2025-12-07 17:54:00');
INSERT INTO `attraction_status` VALUES (2, '蜈支洲岛', 'SANYA', '三亚市海棠湾镇蜈支洲岛', 'LIMITED', '天气原因，部分项目暂停', '09:00:00', '17:00:00', 2, '2025-12-07 17:54:00');
INSERT INTO `attraction_status` VALUES (3, '亚龙湾热带天堂森林公园', 'SANYA', '三亚市亚龙湾国家旅游度假区', 'OPEN', NULL, '07:30:00', '17:30:00', 1, '2025-12-07 17:54:00');
INSERT INTO `attraction_status` VALUES (4, '火山口地质公园', 'HAIKOU', '海口市秀英区石山镇', 'OPEN', NULL, '08:30:00', '17:30:00', 2, '2025-12-07 17:54:00');
INSERT INTO `attraction_status` VALUES (5, '东坡书院', 'DANZHOU', '儋州市中和镇', 'CLOSED', '台风预警，景区临时关闭', '09:00:00', '17:00:00', 1, '2025-12-07 17:54:00');

-- ----------------------------
-- Table structure for comprehensive_warnings
-- ----------------------------
DROP TABLE IF EXISTS `comprehensive_warnings`;
CREATE TABLE `comprehensive_warnings`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `warning_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `affected_locations` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `weather_warning_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `traffic_effects` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `attraction_effects` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `published_by` bigint(0) NULL DEFAULT NULL,
  `publish_time` datetime(0) NULL DEFAULT NULL,
  `status` enum('DRAFT','PUBLISHED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'DRAFT',
  `created_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `published_by`(`published_by`) USING BTREE,
  CONSTRAINT `comprehensive_warnings_ibfk_1` FOREIGN KEY (`published_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comprehensive_warnings
-- ----------------------------
INSERT INTO `comprehensive_warnings` VALUES (1, '台风\"海燕\"影响海南岛北部地区', '受今年第22号台风\"海燕\"影响，海南岛北部地区将出现强风暴雨天气，请相关部门做好防范工作。', '橙色', '海口,儋州,琼海,临高', '1,2,4', '儋州、海口方向部分航班、列车取消或延误', '儋州东坡书院、海口火山口公园等景点临时关闭', 1, '2025-12-07 08:00:00', 'PUBLISHED', '2025-12-07 17:54:00');
INSERT INTO `comprehensive_warnings` VALUES (2, '强对流天气预警', '受冷暖空气交汇影响，海南岛中部和东部地区将出现短时强降水、雷暴大风等强对流天气。', '黄色', '五指山,万宁,琼海', '4', '山区道路注意行车安全', '山区景点注意防范山洪和滑坡', 2, '2025-12-07 09:15:00', 'PUBLISHED', '2025-12-07 17:54:00');

-- ----------------------------
-- Table structure for locations
-- ----------------------------
DROP TABLE IF EXISTS `locations`;
CREATE TABLE `locations`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `city_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `district_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `location_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `latitude` decimal(10, 6) NULL DEFAULT NULL,
  `longitude` decimal(10, 6) NULL DEFAULT NULL,
  `status` tinyint(0) NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `location_code`(`location_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of locations
-- ----------------------------
INSERT INTO `locations` VALUES (1, '海口市', '龙华区', 'HAIKOU', NULL, NULL, 1);
INSERT INTO `locations` VALUES (2, '三亚市', '吉阳区', 'SANYA', NULL, NULL, 1);
INSERT INTO `locations` VALUES (3, '儋州市', '那大镇', 'DANZHOU', NULL, NULL, 1);
INSERT INTO `locations` VALUES (4, '三沙市', '西沙区', 'SANSA', NULL, NULL, 1);
INSERT INTO `locations` VALUES (5, '琼海市', '嘉积镇', 'QIONGHAI', NULL, NULL, 1);
INSERT INTO `locations` VALUES (6, '万宁市', '万城镇', 'WANNING', NULL, NULL, 1);
INSERT INTO `locations` VALUES (7, '东方市', '八所镇', 'DONGFANG', NULL, NULL, 1);
INSERT INTO `locations` VALUES (8, '五指山市', '通什镇', 'WUZHISHAN', NULL, NULL, 1);
INSERT INTO `locations` VALUES (9, '澄迈县', '金江镇', 'CHENGMAI', NULL, NULL, 1);
INSERT INTO `locations` VALUES (10, '临高县', '临城镇', 'LINGAO', NULL, NULL, 1);

-- ----------------------------
-- Table structure for system_logs
-- ----------------------------
DROP TABLE IF EXISTS `system_logs`;
CREATE TABLE `system_logs`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NULL DEFAULT NULL,
  `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `operation_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `system_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_logs
-- ----------------------------
INSERT INTO `system_logs` VALUES (1, 1, '登录系统', '用户管理', '管理员admin登录系统', '192.168.1.100', '2025-12-07 17:54:00');
INSERT INTO `system_logs` VALUES (2, 2, '更新天气数据', '天气数据', '更新了海口市的实时天气数据', '192.168.1.101', '2025-12-07 17:54:00');
INSERT INTO `system_logs` VALUES (3, 1, '发布综合预警', '预警管理', '发布了台风\"海燕\"影响预警', '192.168.1.100', '2025-12-07 17:54:00');
INSERT INTO `system_logs` VALUES (4, 2, '修改景点状态', '景点管理', '将蜈支洲岛状态改为限时开放', '192.168.1.101', '2025-12-07 17:54:00');
INSERT INTO `system_logs` VALUES (5, 1, '更新交通信息', '交通管理', '更新了航班HU7281的延误信息', '192.168.1.100', '2025-12-07 17:54:00');
INSERT INTO `system_logs` VALUES (6, 3, '查询天气预报', '天气服务', '用户visitor1查询了三亚未来3天天气', '192.168.2.50', '2025-12-07 17:54:00');

-- ----------------------------
-- Table structure for traffic_status
-- ----------------------------
DROP TABLE IF EXISTS `traffic_status`;
CREATE TABLE `traffic_status`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `type` enum('FLIGHT','TRAIN') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `departure_city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `arrival_city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `scheduled_time` datetime(0) NULL DEFAULT NULL,
  `estimated_time` datetime(0) NULL DEFAULT NULL,
  `status` enum('NORMAL','DELAYED','CANCELLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'NORMAL',
  `delay_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `updated_by` bigint(0) NULL DEFAULT NULL,
  `updated_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `updated_by`(`updated_by`) USING BTREE,
  CONSTRAINT `traffic_status_ibfk_1` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of traffic_status
-- ----------------------------
INSERT INTO `traffic_status` VALUES (1, 'FLIGHT', 'HU7281', '北京', '海口', '2025-12-07 14:30:00', '2025-12-07 15:45:00', 'DELAYED', '天气原因，航班延误', 2, '2025-12-07 17:54:00');
INSERT INTO `traffic_status` VALUES (2, 'FLIGHT', 'CZ6723', '广州', '三亚', '2025-12-07 16:00:00', '2025-12-07 16:00:00', 'NORMAL', NULL, 2, '2025-12-07 17:54:00');
INSERT INTO `traffic_status` VALUES (3, 'FLIGHT', 'CA1357', '上海', '三亚', '2025-12-07 18:30:00', NULL, 'CANCELLED', '台风影响，航班取消', 1, '2025-12-07 17:54:00');
INSERT INTO `traffic_status` VALUES (4, 'TRAIN', 'Z501', '北京西', '海口', '2025-12-07 15:20:00', '2025-12-07 15:35:00', 'DELAYED', '线路检修', 2, '2025-12-07 17:54:00');
INSERT INTO `traffic_status` VALUES (5, 'TRAIN', 'K511', '上海南', '三亚', '2025-12-07 17:40:00', '2025-12-07 17:40:00', 'NORMAL', NULL, 1, '2025-12-07 17:54:00');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role` enum('ADMIN','USER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'USER',
  `created_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '$2a$10$rDkPvvAFV8kqwvKJzwlRv.i.q.wl8Lp6b9PQMZGY2pQxZ/3o8WQy', 'admin@weather.com', NULL, 'ADMIN', '2025-12-01 20:17:31', '2025-12-01 20:17:31');
INSERT INTO `users` VALUES (2, 'operator1', '$2a$10$rDkPvvAFV8kqwvKJzwlRv.i.q.wl8Lp6b9PQMZGY2pQxZ/3o8WQy', 'operator1@weather.com', '13800138001', 'USER', '2025-12-07 17:54:00', '2025-12-07 17:54:00');
INSERT INTO `users` VALUES (3, 'operator2', '$2a$10$rDkPvvAFV8kqwvKJzwlRv.i.q.wl8Lp6b9PQMZGY2pQxZ/3o8WQy', 'operator2@weather.com', '13800138002', 'USER', '2025-12-07 17:54:00', '2025-12-07 17:54:00');
INSERT INTO `users` VALUES (4, 'visitor1', '$2a$10$rDkPvvAFV8kqwvKJzwlRv.i.q.wl8Lp6b9PQMZGY2pQxZ/3o8WQy', 'visitor@test.com', '13800138003', 'USER', '2025-12-07 17:54:00', '2025-12-07 17:54:00');

-- ----------------------------
-- Table structure for weather_data
-- ----------------------------
DROP TABLE IF EXISTS `weather_data`;
CREATE TABLE `weather_data`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `location_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `temperature` decimal(5, 2) NULL DEFAULT NULL,
  `humidity` decimal(5, 2) NULL DEFAULT NULL,
  `wind_speed` decimal(5, 2) NULL DEFAULT NULL,
  `wind_direction` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `precipitation` decimal(5, 2) NULL DEFAULT NULL,
  `weather_condition` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `pressure` decimal(7, 2) NULL DEFAULT NULL,
  `visibility` decimal(6, 2) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `data_date` date NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `location_code`(`location_code`) USING BTREE,
  CONSTRAINT `weather_data_ibfk_1` FOREIGN KEY (`location_code`) REFERENCES `locations` (`location_code`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of weather_data
-- ----------------------------
INSERT INTO `weather_data` VALUES (1, 'HAIKOU', 25.50, 78.00, 12.50, '东北风', 0.00, '多云', 1013.25, 10.00, '2025-12-07 08:00:00', '2025-12-07');
INSERT INTO `weather_data` VALUES (2, 'SANYA', 28.30, 75.50, 8.20, '东南风', 0.00, '晴', 1012.80, 15.00, '2025-12-07 08:00:00', '2025-12-07');
INSERT INTO `weather_data` VALUES (3, 'DANZHOU', 26.80, 80.20, 10.50, '东风', 2.50, '小雨', 1013.50, 8.00, '2025-12-07 08:00:00', '2025-12-07');
INSERT INTO `weather_data` VALUES (4, 'SANSA', 27.50, 77.80, 9.80, '南风', 0.00, '多云', 1012.90, 12.00, '2025-12-07 08:00:00', '2025-12-07');
INSERT INTO `weather_data` VALUES (5, 'QIONGHAI', 26.20, 82.50, 11.20, '东北风', 1.50, '阴', 1013.80, 6.00, '2025-12-07 08:00:00', '2025-12-07');

-- ----------------------------
-- Table structure for weather_forecast
-- ----------------------------
DROP TABLE IF EXISTS `weather_forecast`;
CREATE TABLE `weather_forecast`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `location_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `forecast_date` date NOT NULL,
  `high_temp` decimal(5, 2) NULL DEFAULT NULL,
  `low_temp` decimal(5, 2) NULL DEFAULT NULL,
  `day_condition` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `night_condition` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `wind_speed` decimal(5, 2) NULL DEFAULT NULL,
  `humidity` decimal(5, 2) NULL DEFAULT NULL,
  `precipitation_prob` decimal(5, 2) NULL DEFAULT NULL,
  `created_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `location_code`(`location_code`) USING BTREE,
  CONSTRAINT `weather_forecast_ibfk_1` FOREIGN KEY (`location_code`) REFERENCES `locations` (`location_code`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of weather_forecast
-- ----------------------------
INSERT INTO `weather_forecast` VALUES (1, 'HAIKOU', '2025-12-08', 26.00, 22.00, '多云转小雨', '小雨', 15.00, 85.00, 60.00, '2025-12-07 17:54:00');
INSERT INTO `weather_forecast` VALUES (2, 'HAIKOU', '2025-12-09', 24.50, 20.50, '中雨', '大雨', 20.00, 90.00, 85.00, '2025-12-07 17:54:00');
INSERT INTO `weather_forecast` VALUES (3, 'HAIKOU', '2025-12-10', 23.00, 19.00, '大雨转中雨', '小雨', 18.00, 88.00, 75.00, '2025-12-07 17:54:00');
INSERT INTO `weather_forecast` VALUES (4, 'SANYA', '2025-12-08', 29.00, 25.00, '晴转多云', '多云', 10.00, 80.00, 20.00, '2025-12-07 17:54:00');
INSERT INTO `weather_forecast` VALUES (5, 'SANYA', '2025-12-09', 28.50, 24.50, '多云转小雨', '小雨', 12.00, 85.00, 40.00, '2025-12-07 17:54:00');
INSERT INTO `weather_forecast` VALUES (6, 'SANYA', '2025-12-10', 27.00, 23.00, '中雨', '中雨', 15.00, 88.00, 65.00, '2025-12-07 17:54:00');
INSERT INTO `weather_forecast` VALUES (7, 'DANZHOU', '2025-12-08', 27.50, 23.50, '小雨', '中雨', 18.00, 88.00, 70.00, '2025-12-07 17:54:00');
INSERT INTO `weather_forecast` VALUES (8, 'DANZHOU', '2025-12-09', 26.00, 22.00, '大雨', '暴雨', 22.00, 92.00, 90.00, '2025-12-07 17:54:00');
INSERT INTO `weather_forecast` VALUES (9, 'DANZHOU', '2025-12-10', 25.00, 21.00, '暴雨转大雨', '中雨', 20.00, 90.00, 80.00, '2025-12-07 17:54:00');

-- ----------------------------
-- Table structure for weather_warnings
-- ----------------------------
DROP TABLE IF EXISTS `weather_warnings`;
CREATE TABLE `weather_warnings`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `location_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `warning_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `warning_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `issue_time` datetime(0) NULL DEFAULT NULL,
  `effective_time` datetime(0) NULL DEFAULT NULL,
  `expire_time` datetime(0) NULL DEFAULT NULL,
  `status` enum('ACTIVE','EXPIRED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ACTIVE',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `location_code`(`location_code`) USING BTREE,
  CONSTRAINT `weather_warnings_ibfk_1` FOREIGN KEY (`location_code`) REFERENCES `locations` (`location_code`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of weather_warnings
-- ----------------------------
INSERT INTO `weather_warnings` VALUES (1, 'DANZHOU', '台风', '橙色', '儋州市台风橙色预警', '预计未来24小时内，儋州市将受台风\"海燕\"影响，风力可达10-12级，请做好防范准备。', '2025-12-07 06:00:00', '2025-12-07 12:00:00', '2025-12-08 12:00:00', 'ACTIVE');
INSERT INTO `weather_warnings` VALUES (2, 'HAIKOU', '暴雨', '黄色', '海口市暴雨黄色预警', '预计未来6小时，海口市大部分地区将出现50毫米以上降水，请注意防范城市内涝。', '2025-12-07 07:30:00', '2025-12-07 08:00:00', '2025-12-07 14:00:00', 'ACTIVE');
INSERT INTO `weather_warnings` VALUES (3, 'SANYA', '大风', '蓝色', '三亚市大风蓝色预警', '受冷空气影响，三亚市沿海地区将出现6-7级大风，海上作业请注意安全。', '2025-12-07 05:45:00', '2025-12-07 06:00:00', '2025-12-08 06:00:00', 'ACTIVE');
INSERT INTO `weather_warnings` VALUES (4, 'QIONGHAI', '雷电', '黄色', '琼海市雷电黄色预警', '预计未来3小时内，琼海市将出现雷雨天气，请注意防范雷电灾害。', '2025-12-07 09:00:00', '2025-12-07 09:30:00', '2025-12-07 12:30:00', 'ACTIVE');

SET FOREIGN_KEY_CHECKS = 1;
