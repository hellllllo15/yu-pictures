package com.example.demo.service;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.model.dto.picture.*;
import com.example.demo.model.entity.Picture;
import com.example.demo.model.entity.User;
import com.example.demo.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import com.example.demo.manager.crawl.CrawlPictures;

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

    /**
     * 自定义更新方法，确保包含spaceId以便ShardingSphere正确路由
     */
    boolean updatePictureWithSpaceId(Picture picture);

    /**
     * 批量抓取和创建图片
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return 成功创建的图片数
     */
    int uploadPictureByBatch(
            PictureUploadByBatchRequest pictureUploadByBatchRequest,
            User loginUser
    );

    

    /**
     * 批量抓取图片原图 URL（支持偏移量分页）
     * first = offset * 20 + 1
     * @param keyword 关键词
     * @param offset 偏移量（1,2,3...），会映射到 first
     * @return 原图链接列表
     */
    default List<String> fetchPictureUrlsByKeyword(String keyword, int offset) {
        return CrawlPictures.crawl(keyword, offset);
    }


    //校验空间图片的权限
    public void checkPictureAuth(User loginUser,Picture picture);


    //删除图片
    public void deletePicture(long pictureId, User loginUser);


    //编辑图片
    public void editPicture(PictureEditRequest pictureEditRequest, User loginUser);
}