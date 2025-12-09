package com.hainan.weather.mapper;

import com.hainan.weather.entity.WeatherWarning;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface WeatherWarningMapper {

    @Select("SELECT * FROM weather_warnings WHERE id = #{id}")
    WeatherWarning findById(@Param("id") Long id);

    @Select("SELECT * FROM weather_warnings WHERE location_code = #{locationCode} " +
            "AND status = 'ACTIVE' AND expire_time > NOW() ORDER BY issue_time DESC")
    List<WeatherWarning> findActiveByLocation(@Param("locationCode") String locationCode);

    @Select("SELECT * FROM weather_warnings WHERE status = 'ACTIVE' AND expire_time > NOW() " +
            "ORDER BY warning_level DESC, issue_time DESC")
    List<WeatherWarning> findAllActive();

    @Select("SELECT * FROM weather_warnings WHERE location_code = #{locationCode} " +
            "ORDER BY issue_time DESC")
    List<WeatherWarning> findByLocationCode(@Param("locationCode") String locationCode);

    @Select("SELECT * FROM weather_warnings WHERE warning_type = #{warningType} " +
            "AND status = 'ACTIVE' AND expire_time > NOW() ORDER BY issue_time DESC")
    List<WeatherWarning> findActiveByType(@Param("warningType") String warningType);

    @Select("SELECT * FROM weather_warnings WHERE warning_level = #{warningLevel} " +
            "AND status = 'ACTIVE' AND expire_time > NOW() ORDER BY issue_time DESC")
    List<WeatherWarning> findActiveByLevel(@Param("warningLevel") String warningLevel);

    @Select("SELECT * FROM weather_warnings WHERE status = #{status} ORDER BY issue_time DESC")
    List<WeatherWarning> findByStatus(@Param("status") String status);

    @Select("SELECT * FROM weather_warnings ORDER BY issue_time DESC")
    List<WeatherWarning> findAll();

    @Insert("INSERT INTO weather_warnings(location_code, warning_type, warning_level, title, content, " +
            "issue_time, effective_time, expire_time, status) " +
            "VALUES(#{locationCode}, #{warningType}, #{warningLevel}, #{title}, #{content}, " +
            "#{issueTime}, #{effectiveTime}, #{expireTime}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WeatherWarning warning);

    @Update("UPDATE weather_warnings SET " +
            "warning_type = #{warningType}, " +
            "warning_level = #{warningLevel}, " +
            "title = #{title}, " +
            "content = #{content}, " +
            "effective_time = #{effectiveTime}, " +
            "expire_time = #{expireTime}, " +
            "status = #{status} " +
            "WHERE id = #{id}")
    int update(WeatherWarning warning);

    @Update("UPDATE weather_warnings SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Update("UPDATE weather_warnings SET status = 'EXPIRED' WHERE expire_time < NOW() AND status = 'ACTIVE'")
    int updateExpiredWarnings();

    @Delete("DELETE FROM weather_warnings WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM weather_warnings WHERE status = 'ACTIVE' AND expire_time > NOW()")
    int countActive();
}