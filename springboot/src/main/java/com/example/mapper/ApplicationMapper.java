package com.example.mapper;


import com.example.entity.Application;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ApplicationMapper {
    List<Application> selectAll(Application application);

    void insert(Application application);

    @Select("select * from `application` where id = #{id}")
    Application selectById(String id);

    void update(Application application);



    @Delete("delete from `application` where id = #{id}")
    void deleteById(String id);

    // 更新申请状态（审核用）
    void updateStatus(Application application);
    @Select("select * from `application` where student_id = #{StudentId} and club_id = #{ClubId}")
    Application selectByStudentAndClub(String StudentId , String ClubId);
}
