package com.hainan.weather.mapper;

import com.hainan.weather.entity.WeatherForecast;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface WeatherForecastMapper {

    @Select("SELECT * FROM weather_forecast WHERE id = #{id}")
    WeatherForecast findById(@Param("id") Long id);

    @Select("SELECT * FROM weather_forecast WHERE location_code = #{locationCode} " +
            "ORDER BY forecast_date ASC LIMIT 7")
    List<WeatherForecast> findWeekForecast(@Param("locationCode") String locationCode);

    @Select("SELECT * FROM weather_forecast WHERE location_code = #{locationCode} " +
            "AND forecast_date BETWEEN #{startDate} AND #{endDate} ORDER BY forecast_date")
    List<WeatherForecast> findByDateRange(@Param("locationCode") String locationCode,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    @Select("SELECT * FROM weather_forecast WHERE location_code = #{locationCode} " +
            "AND forecast_date = #{date}")
    WeatherForecast findByLocationAndDate(@Param("locationCode") String locationCode,
                                          @Param("date") LocalDate date);

    @Select("SELECT * FROM weather_forecast WHERE location_code = #{locationCode} " +
            "ORDER BY forecast_date DESC")
    List<WeatherForecast> findByLocationCode(@Param("locationCode") String locationCode);

    @Select("SELECT * FROM weather_forecast WHERE forecast_date = #{date}")
    List<WeatherForecast> findByForecastDate(@Param("date") LocalDate date);

    @Insert("INSERT INTO weather_forecast(location_code, forecast_date, high_temp, low_temp, " +
            "day_condition, night_condition, wind_speed, humidity, precipitation_prob, created_time) " +
            "VALUES(#{locationCode}, #{forecastDate}, #{highTemp}, #{lowTemp}, " +
            "#{dayCondition}, #{nightCondition}, #{windSpeed}, #{humidity}, #{precipitationProb}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WeatherForecast forecast);

    @Update("UPDATE weather_forecast SET " +
            "high_temp = #{highTemp}, " +
            "low_temp = #{lowTemp}, " +
            "day_condition = #{dayCondition}, " +
            "night_condition = #{nightCondition}, " +
            "wind_speed = #{windSpeed}, " +
            "humidity = #{humidity}, " +
            "precipitation_prob = #{precipitationProb} " +
            "WHERE id = #{id}")
    int update(WeatherForecast forecast);

    @Delete("DELETE FROM weather_forecast WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Delete("DELETE FROM weather_forecast WHERE forecast_date < #{date}")
    int deleteExpired(@Param("date") LocalDate date);

    @Select("SELECT COUNT(*) FROM weather_forecast WHERE location_code = #{locationCode}")
    int countByLocation(@Param("locationCode") String locationCode);
}