package com.example.demo.model.dto.picture;

import lombok.Data;

import java.io.Serializable;


//用户上传或更新的第二个参数，通过是否有ID进行判断是上传还是更新操作
@Data
public class PictureUploadRequest implements Serializable {

   // 图片ID
    private Long id;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 图片名称
     */
    private String picName;


    private static final long serialVersionUID=1L;



}
