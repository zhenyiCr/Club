package com.example.mapper;


import com.example.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CategoryMapper {

    List<Category> selectAll(Category category);

    void insert(Category category);

    @Select("select * from `category` where title = #{title}")
    Category selectByTitle(String title);

    void update(Category category);

    @Select("select title from `category` where id = #{id}")
    String selectTitle(Category category);

    @Select("select title from `category` where id = #{id}")
    String selectById(String id);
    @Delete("delete from `category` where id = #{id}")
    void deleteById(String id);

}
