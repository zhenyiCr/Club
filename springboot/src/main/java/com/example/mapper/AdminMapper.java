package com.example.mapper;


import com.example.entity.Admin;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AdminMapper {
    List<Admin> selectAll(Admin admin);

    void insert(Admin admin);

    @Select("select * from `admin` where username = #{username}")
    Admin selectByUsername(String username);

    void update(Admin admin);

    @Select("select username from `admin` where id = #{id}")
    String selectUsername(Admin admin);


    @Delete("delete from `admin` where id = #{id}")
    void deleteById(String id);

    @Select("select * from `admin` where id = #{id}")
    Admin selectById(String id);
}
