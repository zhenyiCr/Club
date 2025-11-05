package com.example.entity;

import lombok.Data;

@Data
public class application {
        private String id;
        private String studentId;
        private String clubId;
        private String status;
        private String reason;
        private String approverId;
        private String createTime;
        private String approveTime;
        private String remark;
}
