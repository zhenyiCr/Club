package com.example.mapper;


import com.example.entity.Notice;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface NoticeMapper {
    List<Notice> selectAll(Notice notice);

    void insert(Notice notice);

    @Select("select * from `notice` where title = #{title}")
    Notice selectByTitle(String title);

    void update(Notice notice);

    @Select("select title from `notice` where id = #{id}")
    String selectTitle(Notice notice);


    @Delete("delete from `notice` where id = #{id}")
    void deleteById(String id);

}
