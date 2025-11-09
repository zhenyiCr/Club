// Club/springboot/src/main/java/com/example/mapper/ClubMemberMapper.java
package com.example.mapper;

import com.example.entity.ClubMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClubMemberMapper {
    // 根据社团ID查询成员
    List<ClubMember> selectByClubId(String clubId);

    // 添加社团成员
    void insert(ClubMember clubMember);

    // 移除社团成员
    void deleteById(String id);

    // 更新成员角色
    void updateRole(ClubMember clubMember);

    // 查询所有社团成员
    List<ClubMember> selectAll(ClubMember clubMember);

    // 根据ID查询社团成员
    @Select("select * from club_member where id = #{id}")
    ClubMember selectById(String id);

    @Select("select * from club_member where student_id = #{studentId} limit 1")
    ClubMember selectByStudentId(String studentId);

    @Select("select * from club_member where student_id = #{studentId} and club_id = #{clubId}")
    ClubMember selectByStudentIdAndClubId(String studentId, String clubId);

    @Select("select * from club_member where student_id = #{studentId} and role = 'LEADER'")
    ClubMember selectLeaderByStudentId(String studentId);
}