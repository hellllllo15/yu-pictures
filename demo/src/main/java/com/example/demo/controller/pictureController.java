package com.example.demo.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.annotation.AuthCheck;
import com.example.demo.common.BaseResponse;
import com.example.demo.common.DeleteRequest;
import com.example.demo.common.ResultUtils;
import com.example.demo.constant.UserConstant;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.exception.ThrowUtils;
import com.example.demo.manager.auth.annotation.SaSpaceCheckPermission;
import com.example.demo.manager.auth.model.SpaceUserPermissionConstant;
import com.example.demo.model.dto.picture.*;
import com.example.demo.model.dto.space.SpaceAddRequest;
import com.example.demo.model.entity.Picture;
import com.example.demo.model.entity.Space;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.PictureReviewStatusEnum;
import com.example.demo.model.vo.PictureTagCategory;
import com.example.demo.model.vo.PictureVO;
import com.example.demo.service.PictureService;
import com.example.demo.service.SpaceService;
import com.example.demo.service.UserService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.example.demo.manager.crawl.CrawlPictures;
import com.example.demo.api.imagesearch.sub.BaiduUploadClient;
import com.example.demo.api.imagesearch.sub.GetImageFirstUrlApi;

//@Slf4j
@RestController
@RequestMapping("/picture")
public class pictureController {

    private final Cache<String, String> LOCAL_CACHE =
            Caffeine.newBuilder().initialCapacity(1024)
                    .maximumSize(10000L)
                    // 缓存 5 分钟移除
                    .expireAfterWrite(5L, TimeUnit.MINUTES)
                    .build();


    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private PictureService pictureService;


    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 上传图片（可重新上传）  前端未使用
     */
    @PostMapping("/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_UPLOAD)
    public BaseResponse<PictureVO> uploadPicture(
            @RequestParam("file") MultipartFile multipartFile,
            PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request) {


        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }


    /**
     * 删除图片
     */
    //@PostMapping("/delete")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_DELETE)
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest , HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
     pictureService.deletePicture(deleteRequest.getId(),loginUser);
        return ResultUtils.success(true);
    }

    /**
     * 更新图片（仅管理员可用）
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest) {
        if (pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // 设置编辑时间
        picture.setEditTime(new Date());
        // 数据校验
        pictureService.validPicture(picture);

        // 判断是否存在
        long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);




        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取图片（仅管理员可用）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Picture> getPictureById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(picture);
    }

    /**
     * 根据 id 获取图片（封装类）  前端未使用
     */
    @GetMapping("/get/vo")
    public BaseResponse<PictureVO> getPictureVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);

        //空间权限校验
        Long spaceId=picture.getSpaceId();
        if (spaceId != null) {
            User user=userService.getLoginUser(request);
            pictureService.checkPictureAuth(user,picture);
        }
        // 获取封装类
        return ResultUtils.success(pictureService.getPictureVO(picture, request));
    }

    /**
     * 分页获取图片列表（仅管理员可用）   前端使用了
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();

        // 创建分页对象
        Page<Picture> page = new Page<>(current, size);

        // 创建查询条件
        QueryWrapper<Picture> queryWrapper = pictureService.getQueryWrapper(pictureQueryRequest);

        // 添加空间ID为空的条件（且的关系）
        queryWrapper.isNull("spaceId");

        // 查询数据库
        Page<Picture> picturePage = pictureService.page(page, queryWrapper);

        // 返回结果
        return ResultUtils.success(picturePage);
    }

    /**
     * 分页获取图片列表（封装类）    前端使用了
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest , HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        //   ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        // 空间权限校验
        Long spaceId = pictureQueryRequest.getSpaceId();
// 公开图库
        if (spaceId == null) {
            // 普通用户默认只能查看已过审的公开数据
            pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            pictureQueryRequest.setNullSpaceId(true);
        } else {
            // 私有空间
            User loginUser = userService.getLoginUser(request);
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            if (!loginUser.getId().equals(space.getUserId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间权限");
            }

            //需要让用户能够查到自己所有的图片，所以要把空间ID的限制去掉
            //将有无空间ID的查询结果拼接
           // pictureQueryRequest.setSpaceId(null);
        }



        QueryWrapper<Picture>  pictureQueryWrapper=    pictureService.getQueryWrapper(pictureQueryRequest);
// 查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureQueryWrapper);






        // 获取封装返回结果
        return ResultUtils.success(pictureService.getPictureVOPage(picturePage,request));
    }

    /**
     * 编辑图片（给用户使用）  前端未使用
     */
    @PostMapping("/edit")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_EDIT)
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest , HttpServletRequest request) {
        if (pictureEditRequest == null || pictureEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
       pictureService.editPicture(pictureEditRequest,loginUser);
        return ResultUtils.success(true);
    }



    /**
     * 给前端硬编码的标签和分类
     */
    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("热门","搞笑","生活","高清","艺术","校园","背景","简历","创意");
        List<String> categoryList = Arrays.asList("模板","电商","表情包","素材","海报");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return ResultUtils.success(pictureTagCategory);
    }


    /**
     * 用户上传/更新图片/文件上传   前端使用了
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addPicture(   @RequestParam("file") MultipartFile multipartFile,
                                              PictureUpdateRequest pictureUpdateRequest,
                                             HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);

       Long spaceId= pictureUpdateRequest.getSpaceId();
     Boolean IsPublic=   pictureUpdateRequest.getIsPublic();

        ThrowUtils.throwIf(IsPublic == null, ErrorCode.NOT_FOUND_ERROR);

        //判断是上传还是更新图片  为空就是上传图片
        if(pictureUpdateRequest.getId()==null){
            PictureUploadRequest uploadRequest=new PictureUploadRequest();

            //如果不是上传到公共空间，就携带空间ID
            if(!IsPublic){

                //如果空间ID为空，报错
                if(spaceId==null){
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");

                }

                uploadRequest.setSpaceId(spaceId);
            }



            PictureVO pictureVO = pictureService.uploadPicture(multipartFile, uploadRequest, loginUser);
            pictureUpdateRequest.setId(pictureVO.getId());
        }





        if ( pictureUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // 设置编辑时间
        picture.setEditTime(new Date());
        // 数据校验
        pictureService.validPicture(picture);

        // 判断是否存在
        long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);


        //补充审核参数
        pictureService.fillReviewParams(picture,loginUser);

        // 操作数据库
        boolean result = pictureService.updateById(picture);

//
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }



    /**
     * 通过 URL 上传图片（可重新上传）  前端未使用
     */
    @PostMapping("/upload/url")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.PICTURE_UPLOAD)
    public BaseResponse<PictureVO> uploadPictureByUrl(
            @RequestBody PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String fileUrl = pictureUploadRequest.getFileUrl();
        PictureVO pictureVO = pictureService.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }



    /**
     * 通过 URL 上传/更新图片   前端使用
     */
    @PostMapping("/add/url")
    public BaseResponse<Boolean> addPictureByUrl(
            @RequestBody PictureUploadRequest pictureUploadRequest,
            PictureUpdateRequest pictureUpdateRequest,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);

        Long spaceId= pictureUpdateRequest.getSpaceId();
        Boolean IsPublic=   pictureUpdateRequest.getIsPublic();

        ThrowUtils.throwIf(IsPublic == null, ErrorCode.NOT_FOUND_ERROR);

        //判断是上传还是更新图片  为空就是上传图片
        if(pictureUpdateRequest.getId()==null){
            PictureUploadRequest uploadRequest=new PictureUploadRequest();

            //如果不是上传到公共空间，就携带空间ID
            if(!IsPublic){

                //如果空间ID为空,报错
                if(spaceId==null){
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");

                }

                uploadRequest.setSpaceId(spaceId);
            }


            String fileUrl = pictureUploadRequest.getFileUrl();
            PictureVO pictureVO = pictureService.uploadPicture(fileUrl, uploadRequest, loginUser);
            pictureUpdateRequest.setId(pictureVO.getId());

        }







        if ( pictureUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // 设置编辑时间
        picture.setEditTime(new Date());
        // 数据校验
        pictureService.validPicture(picture);

        // 判断是否存在
        long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);




        //补充审核参数
        pictureService.fillReviewParams(picture,loginUser);


        // 操作数据库
        boolean result = pictureService.updateById(picture);

//
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }





    /**
     * 管理员审核图片
     */
    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> doPictureReview(@RequestBody PictureReviewRequest pictureReviewRequest,
                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(pictureReviewRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        pictureService.doPictureReview(pictureReviewRequest, loginUser);
        return ResultUtils.success(true);
    }




    /**
     * 普通批量抓取图片  前端未调用
     */
    @PostMapping("/upload/batch")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> uploadPictureByBatch(
            @RequestBody PictureUploadByBatchRequest pictureUploadByBatchRequest,
            HttpServletRequest request
    ) {
        ThrowUtils.throwIf(pictureUploadByBatchRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        int uploadCount = pictureService.uploadPictureByBatch(pictureUploadByBatchRequest, loginUser);
        return ResultUtils.success(uploadCount);
    }

    /**
     * 批量抓取原图 URL（仅抓取，不入库）
     */
    @GetMapping("/crawl/urls")
    public BaseResponse<List<String>> crawlOriginalUrls(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset
    ) {
        if (offset == null || offset < 0) {
            offset = 0;
        }
        List<String> urls = CrawlPictures.crawl(keyword, offset);
        return ResultUtils.success(urls);
    }


    /**
     * 批量抓取原图 URL（仅抓取，添加到数据库）
     */
    @GetMapping("/crawl/urls/hahaha")
    public BaseResponse<List<String>> crawlOriginalUrlsHAHAHA(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset
    ) {
        if (offset == null || offset < 0) {
            offset = 0;
        }
        List<String> urls = CrawlPictures.crawl(keyword, offset);
Picture picture=new Picture();
        picture.setName("图片");
        picture.setUserId(11L);
        picture.setReviewStatus(1);
        for (String url : urls) {
picture.setId(null);
            picture.setUrl(url);
            pictureService.save(picture);



        }

        return ResultUtils.success(urls);
    }








    /**
     * Redis缓冲获取分页数据   前端未调用
     */
    @PostMapping("/list/page/vo/cache")
    public BaseResponse<Page<PictureVO>> listPictureVOByPageWithCache(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                                      HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 普通用户默认只能查看已过审的数据
        pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());

        // 构建缓存 key
        String queryCondition = JSONUtil.toJsonStr(pictureQueryRequest);
        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
        String redisKey = "yunPicture:listPictureVOByPage:" + hashKey;

       // 1. 查询本地缓存（Caffeine）
        String cachedValue = LOCAL_CACHE.getIfPresent(redisKey);
        if (cachedValue != null) {
            // 如果缓存命中，返回结果
            Page<PictureVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
            return ResultUtils.success(cachedPage);
        }

        // 本地缓存未命中，从 Redis 缓存中查询
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();
        cachedValue = valueOps.get(redisKey);
        if (cachedValue != null) {
            // 如果命中 Redis，存入本地缓存并返回
            LOCAL_CACHE.put(redisKey, cachedValue);
            Page<PictureVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
            return ResultUtils.success(cachedPage);
        }

        // 查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        // 获取封装类
        Page<PictureVO> pictureVOPage = pictureService.getPictureVOPage(picturePage, request);

        //存入本地缓存
        LOCAL_CACHE.put(redisKey, cachedValue);
        // 存入 Redis 缓存
        String cacheValue = JSONUtil.toJsonStr(pictureVOPage);
        // 5 - 10 分钟随机过期，防止雪崩
        int cacheExpireTime = 300 +  RandomUtil.randomInt(0, 300);
        valueOps.set(redisKey, cacheValue, cacheExpireTime, TimeUnit.SECONDS);

        // 返回结果
        return ResultUtils.success(pictureVOPage);
    }

    /**
     * 以图搜图接口
     * 前端传入图片URL，后端调用百度识图API获取相似图片URL列表
     */
    @PostMapping("/search/by-image")
    public BaseResponse<List<String>> searchImageByUrl(@RequestParam("imageUrl") String imageUrl) {
        try {
            // 参数校验
            ThrowUtils.throwIf(imageUrl == null || imageUrl.trim().isEmpty(), ErrorCode.PARAMS_ERROR, "图片URL不能为空");
            
         //   log.info("开始以图搜图，输入图片URL: {}", imageUrl);
            
            // 1. 调用BaiduUploadClient.getImagePageUrl获取搜索结果页面URL
            String searchPageUrl;
            try {
                searchPageUrl = BaiduUploadClient.getImagePageUrl(imageUrl.trim());
             //   log.info("获取到搜索结果页面URL: {}", searchPageUrl);
            } catch (Exception e) {
             //   log.error("调用百度识图API失败", e);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "图片搜索失败：" + e.getMessage());
            }
            
            // 2. 调用GetImageFirstUrlApi.getImageUrls解析图片URL列表
            List<String> imageUrls;
            try {
                imageUrls = GetImageFirstUrlApi.getImageUrls(searchPageUrl);
              //  log.info("解析到 {} 个图片URL", imageUrls.size());
            } catch (Exception e) {
            //    log.error("解析搜索结果页面失败", e);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "解析搜索结果失败：" + e.getMessage());
            }
            
            // 3. 返回图片URL列表
            return ResultUtils.success(imageUrls);
            
        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
          //  log.error("以图搜图接口发生未知错误", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统内部错误");
        }
    }


}
