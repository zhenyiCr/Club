package com.example.entity;

import lombok.Data;

@Data
public class Student extends Account {
    private String major;
    private String grade;
    private String college;

    //非数据库属性
    private String ids;
    private String[] idArr;

    // 新增社团相关字段
    private String clubRole; // 社团内角色：LEADER/MEMBER/null
    private String clubId;   // 所属社团ID：xxx/null
}
