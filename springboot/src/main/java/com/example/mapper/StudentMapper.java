package com.example.mapper;


import com.example.entity.Student;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StudentMapper {
    List<Student> selectAll(Student student);

    void insert(Student student);

    @Select("select * from `student` where username = #{username}")
    Student selectByUsername(String username);

    void update(Student student);

    @Select("select username from `student` where id = #{id}")
    String selectUsername(Student student);


    @Delete("delete from `student` where id = #{id}")
    void deleteById(String id);

    @Select("select * from `student` where id = #{id}")
    Student selectById(String id);
}
