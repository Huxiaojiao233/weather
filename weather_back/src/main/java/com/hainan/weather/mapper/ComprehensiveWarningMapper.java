package com.hainan.weather.mapper;

import com.hainan.weather.entity.ComprehensiveWarning;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ComprehensiveWarningMapper {

    @Select("SELECT * FROM comprehensive_warnings WHERE id = #{id}")
    ComprehensiveWarning findById(@Param("id") Long id);

    @Select("SELECT * FROM comprehensive_warnings WHERE status = 'PUBLISHED' " +
            "ORDER BY publish_time DESC")
    List<ComprehensiveWarning> findPublishedWarnings();

    @Select("SELECT * FROM comprehensive_warnings WHERE status = 'DRAFT' " +
            "ORDER BY created_time DESC")
    List<ComprehensiveWarning> findDraftWarnings();

    @Select("SELECT * FROM comprehensive_warnings WHERE warning_level = #{warningLevel} " +
            "AND status = 'PUBLISHED' ORDER BY publish_time DESC")
    List<ComprehensiveWarning> findPublishedByLevel(@Param("warningLevel") String warningLevel);

    @Select("SELECT * FROM comprehensive_warnings WHERE status = 'PUBLISHED' " +
            "AND publish_time >= #{startTime} AND publish_time <= #{endTime} " +
            "ORDER BY publish_time DESC")
    List<ComprehensiveWarning> findByPublishTimeRange(@Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);

    @Select("SELECT * FROM comprehensive_warnings WHERE published_by = #{userId} " +
            "ORDER BY created_time DESC")
    List<ComprehensiveWarning> findByPublisher(@Param("userId") Long userId);

    @Select("SELECT * FROM comprehensive_warnings ORDER BY created_time DESC")
    List<ComprehensiveWarning> findAll();

    @Insert("INSERT INTO comprehensive_warnings(title, content, warning_level, affected_locations, " +
            "weather_warning_ids, traffic_effects, attraction_effects, published_by, " +
            "publish_time, status, created_time) " +
            "VALUES(#{title}, #{content}, #{warningLevel}, #{affectedLocations}, " +
            "#{weatherWarningIds}, #{trafficEffects}, #{attractionEffects}, #{publishedBy}, " +
            "#{publishTime}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ComprehensiveWarning warning);

    @Update("UPDATE comprehensive_warnings SET " +
            "title = #{title}, " +
            "content = #{content}, " +
            "warning_level = #{warningLevel}, " +
            "affected_locations = #{affectedLocations}, " +
            "weather_warning_ids = #{weatherWarningIds}, " +
            "traffic_effects = #{trafficEffects}, " +
            "attraction_effects = #{attractionEffects}, " +
            "published_by = #{publishedBy}, " +
            "publish_time = #{publishTime}, " +
            "status = #{status} " +
            "WHERE id = #{id}")
    int update(ComprehensiveWarning warning);

    @Update("UPDATE comprehensive_warnings SET status = 'PUBLISHED', " +
            "publish_time = NOW(), published_by = #{publishedBy} WHERE id = #{id}")
    int publishWarning(@Param("id") Long id, @Param("publishedBy") Long publishedBy);

    @Update("UPDATE comprehensive_warnings SET status = 'DRAFT', publish_time = NULL " +
            "WHERE id = #{id}")
    int unpublishWarning(@Param("id") Long id);

    @Delete("DELETE FROM comprehensive_warnings WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM comprehensive_warnings WHERE status = 'PUBLISHED'")
    int countPublished();
}