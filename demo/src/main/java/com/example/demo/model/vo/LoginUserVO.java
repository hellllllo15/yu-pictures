package com.example.demo.model.vo;

import lombok.Data;

import java.time.LocalDateTime;


//已登录用户视图脱敏
@Data
public class LoginUserVO {

    private Long id;


    private String userAccount;





    private String userName;


    private String userAvatar;


    private String userProfile;


    private String userRole;


    private LocalDateTime editText;


    private LocalDateTime createTime;


    private LocalDateTime updateTime;


    private static final long serialVersionUID = 1L;


}