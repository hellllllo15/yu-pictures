package com.example.demo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class SpaceUser {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long spaceId;
    private Long userId;
    private String spaceRole;
    private Date createTime;
    private Date updateTime;
}