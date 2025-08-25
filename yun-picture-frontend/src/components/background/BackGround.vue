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

//如果空间ID为空，说明用户需要新建空间
if(spaceId==null){


SpaceAddRequest spaceAddRequest=new SpaceAddRequest();
spaceAddRequest.setSpaceName("默认空间");
spaceAddRequest.setSpaceLevel(0);

spaceId = spaceService.addSpace(spaceAddRequest, loginUser);
pictureUpdateRequest.setSpaceId(spaceId);

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
ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
return ResultUtils.success(true);
}