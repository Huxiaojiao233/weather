package com.hainan.weather.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WeatherSyncTask {

    @Autowired
    private QWeatherApiService qWeatherApiService;

    /**
     * 每小时00分执行一次天气和预警数据同步
     * cron表达式：0 0 * * * ? 表示每小时的第0分钟执行
     * 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void syncWeatherData() {
        log.info("========== 开始执行定时任务：同步天气和预警数据 ==========");
        try {
            qWeatherApiService.syncAllLocations();
            log.info("========== 定时任务执行完成：同步天气和预警数据 ==========");
        } catch (Exception e) {
            log.error("========== 定时任务执行失败：同步天气和预警数据 ==========", e);
        }
    }

    /**
     * 应用启动后立即执行一次，然后每5分钟执行一次（用于测试和验证）
     * 可以通过这个任务验证定时任务是否正常工作
     * 注意：这个任务会在启动后10秒执行，然后每5分钟执行一次
     * 如果需要禁用测试任务，可以注释掉@Scheduled注解
     */
    @Scheduled(initialDelay = 10000, fixedDelay = 300000) // 启动10秒后执行，然后每5分钟执行一次
    public void syncWeatherDataTest() {
        log.info("========== [测试定时任务] 开始执行：同步天气和预警数据 ==========");
        try {
            qWeatherApiService.syncAllLocations();
            log.info("========== [测试定时任务] 执行完成：同步天气和预警数据 ==========");
        } catch (Exception e) {
            log.error("========== [测试定时任务] 执行失败：同步天气和预警数据 ==========", e);
        }
    }
}
