package com.example.mapper;


import com.example.entity.Club;
import com.example.entity.ClubMember;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ClubMemberMapper {
    List<ClubMember> selectAll(ClubMember clubMember);

    void insert(ClubMember clubMember);


    void update(ClubMember clubMember);


    ClubMember selectByClubAndStudent(String ClubId,String StudentId);

    @Delete("delete from club_member where club_id = #{clubId} and student_id = #{studentId}")
    void delete(String ClubId,String StudentId);
    @Select("select * from club_member where club_id = #{clubId}")
    List<ClubMember> selectByClubId(String clubId);
    @Update("update club_member set role = #{manager} where club_id = #{clubId} and student_id = #{studentId}")
    void updateRole(String clubId, String studentId, String manager);
}
