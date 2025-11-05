package com.example.mapper;


import com.example.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper {
    List<User> selectAll(User user);

    void insert(User user);

    @Select("select * from `user` where username = #{username}")
    User selectByUsername(String username);

    void update(User user);

    @Select("select username from `user` where id = #{id}")
    String selectUsername(User user);


    @Delete("delete from `user` where id = #{id}")
    void deleteById(String id);

    @Select("select * from `user` where id = #{id}")
    User selectById(String id);
}
