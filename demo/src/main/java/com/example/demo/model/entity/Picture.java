package com.example.demo.model.entity;



import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("picture")
public class Picture {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String url;
    private String name;
    private String introduction;
    private String category;
    private String tags;
    private Long picSize;
    private Integer picWidth;
    private Integer picHeight;
    private Double picScale;
    private String picFormat;
    private Long userId;

    private Date createTime;

    private Date editTime;

    private Date updateTime;
    @TableLogic
    private Boolean isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}