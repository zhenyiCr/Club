package com.example.entity;

import lombok.Data;

@Data
public class Admin extends Account{

    //非数据库属性
    private String ids;
    private String[] idArr;
    private String token;

}
