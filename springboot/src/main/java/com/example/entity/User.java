package com.example.entity;

import lombok.Data;

@Data

public class User extends Account{
    private String id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private String email;
    private String role;
    private String avatar;

    //非数据库属性
    private String ids;
    private String[] idArr;

}
