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
}
