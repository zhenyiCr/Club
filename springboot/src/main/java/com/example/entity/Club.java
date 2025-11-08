package com.example.entity;

import lombok.Data;

@Data
public class Club {
        private String id;
        private String name;
        private String description;
        private String status;
        private String leaderId;
        private String avatar;

        // 社团负责人姓名
        private String leaderName;

}
