package com.example.demo.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;


//已登录用户视图脱敏
@Data
public class LoginUserVO {

    private Long id;


    private String userAccount;





    private String userName;


    private String userAvatar;


    private String userProfile;


    private String userRole;


    private Date editText;


    private Date createTime;


    private Date updateTime;


    private static final long serialVersionUID = 1L;


}