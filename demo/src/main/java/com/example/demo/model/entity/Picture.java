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
    private  Long spaceId;
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

    private String sourceUrl;

    @TableLogic
    private Boolean isDelete;

    /**
     * 状态：0-待审核；1-通过；2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 审核人 id
     */
    private Long reviewerId;

    /**
     * 审核时间
     */
    private Date reviewTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}