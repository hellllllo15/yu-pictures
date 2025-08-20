package com.example.demo.service;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.model.dto.picture.PictureQueryRequest;
import com.example.demo.model.dto.picture.PictureUploadRequest;
import com.example.demo.model.dto.picture.PictureReviewRequest;
import com.example.demo.model.entity.Picture;
import com.example.demo.model.entity.User;
import com.example.demo.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface PictureService extends IService<Picture> {





    /**
     * 上传图片
     *
     * @param inputSource 输入源
     * @param pictureUploadRequest 图片上传请求
     * @param loginUser 登录用户
     * @return PictureVO
     */
    PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser);


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

    /**
     * 图片审核
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 填充审核参数
     */
    public void fillReviewParams(Picture picture, User loginUser);
}