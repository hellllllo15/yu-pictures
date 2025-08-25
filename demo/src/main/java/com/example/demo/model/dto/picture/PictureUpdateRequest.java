package com.example.demo.model.dto.picture;


import lombok.Data;

import java.io.Serializable;
import java.util.List;


//图片更新请求   用户上传图片
@Data
public class PictureUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

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



    /**
     * 用户空间ID，如果携带，上传到私密空间
     */
    private  Long spaceId;


    //用于判断是否要上传到公共空间
    Boolean isPublic;




    private static final long serialVersionUID = 1L;
}