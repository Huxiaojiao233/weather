package com.hainan.weather.mapper;

import com.hainan.weather.entity.TrafficStatus;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TrafficStatusMapper {

    @Select("SELECT * FROM traffic_status WHERE id = #{id}")
    TrafficStatus findById(@Param("id") Long id);

    @Select("SELECT * FROM traffic_status WHERE type = #{type} AND number = #{number} " +
            "ORDER BY scheduled_time DESC LIMIT 1")
    TrafficStatus findByNumber(@Param("type") String type, @Param("number") String number);

    @Select("SELECT * FROM traffic_status WHERE type = #{type} " +
            "AND (departure_city = #{city} OR arrival_city = #{city}) " +
            "AND scheduled_time >= #{startTime} AND scheduled_time <= #{endTime} " +
            "ORDER BY scheduled_time")
    List<TrafficStatus> findByCityAndTimeRange(@Param("type") String type,
                                               @Param("city") String city,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    @Select("SELECT * FROM traffic_status WHERE type = #{type} AND status = #{status} " +
            "ORDER BY scheduled_time")
    List<TrafficStatus> findByTypeAndStatus(@Param("type") String type,
                                            @Param("status") String status);

    @Select("SELECT * FROM traffic_status WHERE status != 'NORMAL' " +
            "ORDER BY updated_time DESC LIMIT 20")
    List<TrafficStatus> findAbnormalStatus();

    @Select("SELECT * FROM traffic_status WHERE type = #{type} " +
            "ORDER BY scheduled_time DESC")
    List<TrafficStatus> findByType(@Param("type") String type);

    @Select("SELECT * FROM traffic_status ORDER BY updated_time DESC")
    List<TrafficStatus> findAll();

    @Insert("INSERT INTO traffic_status(type, number, departure_city, arrival_city, " +
            "scheduled_time, estimated_time, status, delay_reason, updated_by, updated_time) " +
            "VALUES(#{type}, #{number}, #{departureCity}, #{arrivalCity}, " +
            "#{scheduledTime}, #{estimatedTime}, #{status}, #{delayReason}, #{updatedBy}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TrafficStatus trafficStatus);

    @Update("UPDATE traffic_status SET " +
            "estimated_time = #{estimatedTime}, " +
            "status = #{status}, " +
            "delay_reason = #{delayReason}, " +
            "updated_by = #{updatedBy}, " +
            "updated_time = NOW() " +
            "WHERE id = #{id}")
    int update(TrafficStatus trafficStatus);

    @Update("UPDATE traffic_status SET status = #{status}, delay_reason = #{delayReason}, " +
            "updated_by = #{updatedBy}, updated_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id,
                     @Param("status") String status,
                     @Param("delayReason") String delayReason,
                     @Param("updatedBy") Long updatedBy);

    @Delete("DELETE FROM traffic_status WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM traffic_status WHERE status != 'NORMAL' AND type = #{type}")
    int countAbnormalByType(@Param("type") String type);
}