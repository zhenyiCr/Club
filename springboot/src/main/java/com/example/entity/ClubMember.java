package com.example.entity;

import lombok.Data;

@Data
public class ClubMember {
        private String id;
        private String clubId;
        private String studentId;
        private String role;
        private String joinTime;
        private String leaveTime;
}
