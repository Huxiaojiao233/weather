package com.hainan.weather.mapper;

import com.hainan.weather.entity.Location;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LocationMapper {

    @Select("SELECT * FROM locations WHERE id = #{id}")
    Location findById(@Param("id") Long id);

    @Select("SELECT * FROM locations WHERE location_code = #{locationCode}")
    Location findByCode(@Param("locationCode") String locationCode);

    @Select("SELECT * FROM locations WHERE city_name LIKE CONCAT('%', #{cityName}, '%')")
    List<Location> findByCityName(@Param("cityName") String cityName);

    @Select("SELECT * FROM locations WHERE status = #{status}")
    List<Location> findByStatus(@Param("status") Integer status);

    @Select("SELECT * FROM locations ORDER BY id")
    List<Location> findAll();

    @Insert("INSERT INTO locations(city_name, district_name, location_code, latitude, longitude, status) " +
            "VALUES(#{cityName}, #{districtName}, #{locationCode}, #{latitude}, #{longitude}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Location location);

    @Update("UPDATE locations SET " +
            "city_name = #{cityName}, " +
            "district_name = #{districtName}, " +
            "location_code = #{locationCode}, " +
            "latitude = #{latitude}, " +
            "longitude = #{longitude}, " +
            "status = #{status} " +
            "WHERE id = #{id}")
    int update(Location location);

    @Update("UPDATE locations SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("DELETE FROM locations WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM locations")
    int count();
}