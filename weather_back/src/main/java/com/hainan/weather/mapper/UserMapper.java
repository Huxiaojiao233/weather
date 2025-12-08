package com.hainan.weather.mapper;

import com.hainan.weather.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(@Param("id") Long id);

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    @Select("SELECT * FROM users WHERE role = #{role}")
    List<User> findByRole(@Param("role") String role);

    @Select("SELECT * FROM users ORDER BY created_time DESC")
    List<User> findAll();

    @Insert("INSERT INTO users(username, password, email, phone, role, created_time, updated_time) " +
            "VALUES(#{username}, #{password}, #{email}, #{phone}, #{role}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE users SET " +
            "email = #{email}, " +
            "phone = #{phone}, " +
            "role = #{role}, " +
            "updated_time = NOW() " +
            "WHERE id = #{id}")
    int update(User user);

    @Update("UPDATE users SET password = #{password}, updated_time = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Delete("DELETE FROM users WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM users")
    int count();
}