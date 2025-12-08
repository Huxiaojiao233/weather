package com.hainan.weather.mapper;

import com.hainan.weather.entity.WeatherData;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface WeatherDataMapper {

    @Select("SELECT * FROM weather_data WHERE id = #{id}")
    WeatherData findById(@Param("id") Long id);

    @Select("SELECT * FROM weather_data WHERE location_code = #{locationCode} ORDER BY update_time DESC LIMIT 1")
    WeatherData findLatestByLocationCode(@Param("locationCode") String locationCode);

    @Select("SELECT * FROM weather_data WHERE location_code = #{locationCode} AND data_date = #{date}")
    WeatherData findByLocationAndDate(@Param("locationCode") String locationCode,
                                      @Param("date") LocalDate date);

    @Select("SELECT * FROM weather_data WHERE location_code = #{locationCode} ORDER BY data_date DESC LIMIT #{limit}")
    List<WeatherData> findRecentByLocation(@Param("locationCode") String locationCode,
                                           @Param("limit") int limit);

    @Select("SELECT * FROM weather_data WHERE location_code = #{locationCode} " +
            "AND data_date BETWEEN #{startDate} AND #{endDate} ORDER BY data_date DESC")
    List<WeatherData> findByDateRange(@Param("locationCode") String locationCode,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    @Select("SELECT * FROM weather_data WHERE data_date = #{date}")
    List<WeatherData> findByDate(@Param("date") LocalDate date);

    @Select("SELECT * FROM weather_data ORDER BY update_time DESC")
    List<WeatherData> findAll();

    @Insert("INSERT INTO weather_data(location_code, temperature, humidity, wind_speed, wind_direction, " +
            "precipitation, weather_condition, pressure, visibility, update_time, data_date) " +
            "VALUES(#{locationCode}, #{temperature}, #{humidity}, #{windSpeed}, #{windDirection}, " +
            "#{precipitation}, #{weatherCondition}, #{pressure}, #{visibility}, NOW(), #{dataDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WeatherData weatherData);

    @Update("UPDATE weather_data SET " +
            "temperature = #{temperature}, " +
            "humidity = #{humidity}, " +
            "wind_speed = #{windSpeed}, " +
            "wind_direction = #{windDirection}, " +
            "precipitation = #{precipitation}, " +
            "weather_condition = #{weatherCondition}, " +
            "pressure = #{pressure}, " +
            "visibility = #{visibility}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    int update(WeatherData weatherData);

    @Delete("DELETE FROM weather_data WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM weather_data WHERE location_code = #{locationCode}")
    int countByLocation(@Param("locationCode") String locationCode);
}