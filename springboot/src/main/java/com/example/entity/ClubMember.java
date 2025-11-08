package com.example.entity;

import lombok.Data;

@Data
public class ClubMember {
        private String id;
        private String clubId;
        private String studentId;
        private String role; // 社团内角色：MEMBER（普通成员）、LEADER（社团管理员）
        private String joinTime;

        // 非数据库字段，用于前端显示
        private String clubName;
        private String studentName;
}
