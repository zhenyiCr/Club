package com.example.entity;

import lombok.Data;

@Data
public class Introduction {
    private String id;
    private String img;
    private String title;
    private String content;
    private String time;
    private String categoryId;
    private String userId;

    // 不是数据库的字段 而是用来存储数据库里categeory_id 关联category表查出的title字段
    private String categoryTitle;
    private String userName;
}
