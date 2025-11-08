package com.example.mapper;


import com.example.entity.Club;
import com.example.entity.ClubMember;
import com.example.entity.Notice;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClubMapper {
    List<Club> selectAll(Club club);

    void insert(Club club);

    @Select("select * from `club` where name = #{name}")
    Club selectByName(String title);
    @Select("select * from `club` where id = #{id}")
    Club selectById (String id);
    void update(Club club);

    @Select("select name from `club` where id = #{id}")
    String selectName(Club club);


    @Delete("delete from `club` where id = #{id}")
    void deleteById(String id);


}
