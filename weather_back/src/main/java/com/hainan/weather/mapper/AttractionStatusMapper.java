package com.hainan.weather.mapper;

import com.hainan.weather.entity.AttractionStatus;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AttractionStatusMapper {

    @Select("SELECT * FROM attraction_status WHERE id = #{id}")
    AttractionStatus findById(@Param("id") Long id);

    @Select("SELECT * FROM attraction_status WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<AttractionStatus> findByName(@Param("name") String name);

    @Select("SELECT * FROM attraction_status WHERE location_code = #{locationCode}")
    List<AttractionStatus> findByLocationCode(@Param("locationCode") String locationCode);

    @Select("SELECT * FROM attraction_status WHERE open_status = #{openStatus} " +
            "ORDER BY updated_time DESC")
    List<AttractionStatus> findByOpenStatus(@Param("openStatus") String openStatus);

    @Select("SELECT * FROM attraction_status WHERE open_status != 'OPEN' " +
            "ORDER BY updated_time DESC")
    List<AttractionStatus> findAbnormalStatus();

    @Select("SELECT * FROM attraction_status ORDER BY updated_time DESC")
    List<AttractionStatus> findAll();

    @Insert("INSERT INTO attraction_status(name, location_code, address, open_status, " +
            "close_reason, open_time, close_time, updated_by, updated_time) " +
            "VALUES(#{name}, #{locationCode}, #{address}, #{openStatus}, " +
            "#{closeReason}, #{openTime}, #{closeTime}, #{updatedBy}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AttractionStatus attractionStatus);

    @Update("UPDATE attraction_status SET " +
            "name = #{name}, " +
            "location_code = #{locationCode}, " +
            "address = #{address}, " +
            "open_status = #{openStatus}, " +
            "close_reason = #{closeReason}, " +
            "open_time = #{openTime}, " +
            "close_time = #{closeTime}, " +
            "updated_by = #{updatedBy}, " +
            "updated_time = NOW() " +
            "WHERE id = #{id}")
    int update(AttractionStatus attractionStatus);

    @Update("UPDATE attraction_status SET open_status = #{openStatus}, " +
            "close_reason = #{closeReason}, updated_by = #{updatedBy}, updated_time = NOW() " +
            "WHERE id = #{id}")
    int updateStatus(@Param("id") Long id,
                     @Param("openStatus") String openStatus,
                     @Param("closeReason") String closeReason,
                     @Param("updatedBy") Long updatedBy);

    @Delete("DELETE FROM attraction_status WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM attraction_status WHERE open_status != 'OPEN'")
    int countClosedAttractions();
}