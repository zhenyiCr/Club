package com.example.mapper;


import com.example.entity.Introduction;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface IntroductionMapper {

    List<Introduction> selectAll(Introduction introduction);

    void insert(Introduction introduction);

    @Select("select * from `introduction` where title = #{title}")
    Introduction selectByTitle(String title);

    void update(Introduction introduction);

    @Select("select title from `introduction` where id = #{id}")
    String selectTitle(Introduction introduction);


    @Delete("delete from `introduction` where id = #{id}")
    void deleteById(String id);

}
