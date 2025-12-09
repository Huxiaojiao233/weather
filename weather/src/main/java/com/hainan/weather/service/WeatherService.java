package com.hainan.weather.service;

import com.hainan.weather.entity.WeatherData;
import com.hainan.weather.entity.WeatherForecast;
import com.hainan.weather.mapper.WeatherDataMapper;
import com.hainan.weather.mapper.WeatherForecastMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class WeatherService {

    @Autowired
    private WeatherDataMapper weatherDataMapper;

    @Autowired
    private WeatherForecastMapper weatherForecastMapper;

    /**
     * 获取指定地点的最新天气数据
     */
    public WeatherData getLatestWeather(String locationCode) {
        try {
            return weatherDataMapper.findLatestByLocationCode(locationCode);
        } catch (Exception e) {
            log.error("获取实时天气数据失败, locationCode: {}", locationCode, e);
            return null;
        }
    }

    /**
     * 获取指定地点的历史天气数据
     */
    public List<WeatherData> getHistoricalWeather(String locationCode, int days) {
        try {
            return weatherDataMapper.findRecentByLocation(locationCode, days);
        } catch (Exception e) {
            log.error("获取历史天气数据失败, locationCode: {}", locationCode, e);
            return null;
        }
    }

    /**
     * 获取指定日期的天气数据
     */
    public WeatherData getWeatherByDate(String locationCode, LocalDate date) {
        try {
            return weatherDataMapper.findByLocationAndDate(locationCode, date);
        } catch (Exception e) {
            log.error("按日期查询天气数据失败", e);
            return null;
        }
    }

    /**
     * 获取一周天气预报
     */
    public List<WeatherForecast> getWeekForecast(String locationCode) {
        try {
            return weatherForecastMapper.findWeekForecast(locationCode);
        } catch (Exception e) {
            log.error("获取一周天气预报失败, locationCode: {}", locationCode, e);
            return null;
        }
    }

    /**
     * 获取指定日期范围的天气预报
     */
    public List<WeatherForecast> getForecastByDateRange(String locationCode,
                                                        LocalDate startDate,
                                                        LocalDate endDate) {
        try {
            return weatherForecastMapper.findByDateRange(locationCode, startDate, endDate);
        } catch (Exception e) {
            log.error("获取日期范围天气预报失败", e);
            return null;
        }
    }

    /**
     * 获取指定日期的天气预报
     */
    public WeatherForecast getForecastByDate(String locationCode, LocalDate date) {
        try {
            return weatherForecastMapper.findByLocationAndDate(locationCode, date);
        } catch (Exception e) {
            log.error("获取指定日期天气预报失败", e);
            return null;
        }
    }

    /**
     * 保存天气数据
     */
    public boolean saveWeatherData(WeatherData weatherData) {
        try {
            int result = weatherDataMapper.insert(weatherData);
            return result > 0;
        } catch (Exception e) {
            log.error("保存天气数据失败", e);
            return false;
        }
    }

    /**
     * 更新天气数据
     */
    public boolean updateWeatherData(WeatherData weatherData) {
        try {
            int result = weatherDataMapper.update(weatherData);
            return result > 0;
        } catch (Exception e) {
            log.error("更新天气数据失败", e);
            return false;
        }
    }

    /**
     * 保存天气预报
     */
    public boolean saveWeatherForecast(WeatherForecast forecast) {
        try {
            int result = weatherForecastMapper.insert(forecast);
            return result > 0;
        } catch (Exception e) {
            log.error("保存天气预报失败", e);
            return false;
        }
    }

    /**
     * 批量保存天气预报
     */
    public boolean batchSaveForecasts(List<WeatherForecast> forecasts) {
        try {
            for (WeatherForecast forecast : forecasts) {
                weatherForecastMapper.insert(forecast);
            }
            return true;
        } catch (Exception e) {
            log.error("批量保存天气预报失败", e);
            return false;
        }
    }

    /**
     * 删除过期的天气预报
     */
    public int deleteExpiredForecasts(LocalDate date) {
        try {
            return weatherForecastMapper.deleteExpired(date);
        } catch (Exception e) {
            log.error("删除过期天气预报失败", e);
            return 0;
        }
    }

    /**
     * 获取所有地点的最新天气数据
     */
    public List<WeatherData> getAllLatestWeather() {
        try {
            // 这里需要扩展Mapper或使用其他方式获取所有地点最新数据
            return weatherDataMapper.findAll();
        } catch (Exception e) {
            log.error("获取所有地点天气数据失败", e);
            return null;
        }
    }

    /**
     * 获取天气预报数量
     */
    public int getForecastCount(String locationCode) {
        return weatherForecastMapper.countByLocation(locationCode);
    }
}