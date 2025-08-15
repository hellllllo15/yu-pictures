package com.example.demo.service;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.model.dto.picture.PictureQueryRequest;
import com.example.demo.model.dto.picture.PictureUploadRequest;
import com.example.demo.model.entity.Picture;
import com.example.demo.model.entity.User;
import com.example.demo.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface PictureService extends IService<Picture> {





    /**
     * 上传图片
     *
     * @param multipartFile 文件
     * @param pictureUploadRequest 图片上传请求
     * @param loginUser 登录用户
     * @return PictureVO
     */
    PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser);


    /**分页查询
     */
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) ;


    /**
     * 获取脱敏后的单个图片
     * @param picture 图片
     *
     */
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request);


    /**
     * 分页获取图片封装,脱敏
     */
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) ;



    /**
     * 校验图片
     */
    public void validPicture(Picture picture);
}