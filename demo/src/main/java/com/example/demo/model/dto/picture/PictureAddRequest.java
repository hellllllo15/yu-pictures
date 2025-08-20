package com.example.demo.model.dto.picture;


import lombok.Data;

import java.io.Serializable;
import java.util.List;


// 用户上传图片
@Data
public class PictureAddRequest implements Serializable {


    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}