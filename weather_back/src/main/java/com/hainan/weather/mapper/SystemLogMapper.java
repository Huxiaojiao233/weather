package com.hainan.weather.mapper;

import com.hainan.weather.entity.SystemLog;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SystemLogMapper {

    @Select("SELECT * FROM system_logs WHERE id = #{id}")
    SystemLog findById(@Param("id") Long id);

    @Select("SELECT * FROM system_logs WHERE user_id = #{userId} " +
            "ORDER BY operation_time DESC")
    List<SystemLog> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM system_logs WHERE module = #{module} " +
            "ORDER BY operation_time DESC")
    List<SystemLog> findByModule(@Param("module") String module);

    @Select("SELECT * FROM system_logs WHERE operation = #{operation} " +
            "ORDER BY operation_time DESC")
    List<SystemLog> findByOperation(@Param("operation") String operation);

    @Select("SELECT * FROM system_logs WHERE operation_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY operation_time DESC")
    List<SystemLog> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);

    @Select("SELECT * FROM system_logs WHERE ip_address = #{ipAddress} " +
            "ORDER BY operation_time DESC")
    List<SystemLog> findByIpAddress(@Param("ipAddress") String ipAddress);

    @Select("SELECT * FROM system_logs ORDER BY operation_time DESC LIMIT #{limit}")
    List<SystemLog> findRecentLogs(@Param("limit") int limit);

    @Select("SELECT * FROM system_logs ORDER BY operation_time DESC")
    List<SystemLog> findAll();

    @Insert("INSERT INTO system_logs(user_id, operation, module, description, ip_address, operation_time) " +
            "VALUES(#{userId}, #{operation}, #{module}, #{description}, #{ipAddress}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SystemLog systemLog);

    @Delete("DELETE FROM system_logs WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Delete("DELETE FROM system_logs WHERE operation_time < #{time}")
    int deleteOldLogs(@Param("time") LocalDateTime time);

    @Select("SELECT COUNT(*) FROM system_logs WHERE user_id = #{userId}")
    int countByUser(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM system_logs")
    int count();
}