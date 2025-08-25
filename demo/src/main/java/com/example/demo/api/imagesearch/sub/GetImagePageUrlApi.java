package com.example.demo.api.imagesearch.sub;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

//  获取图片页面地址   已废弃
@Deprecated
@Slf4j
public class GetImagePageUrlApi {

    /**
     * 获取图片页面地址    已废弃
     *
     * @param imageUrl
     * @return
     */
    public static String getImagePageUrl(String imageUrl) {
        // 1. 准备请求参数（与成功请求保持一致：image 需 URL 编码，补充 sdkParams）
        Map<String, Object> formData = new HashMap<>();
        String encodedImage = URLUtil.encode(imageUrl, StandardCharsets.UTF_8);
        formData.put("image", encodedImage);
        formData.put("tn", "pc");
        formData.put("from", "pc");
        formData.put("image_source", "PC_UPLOAD_URL");
        formData.put("sdkParams", "{\"data\":\"8f7588d07e048cb185895de2ac09bfbe055aff1048e6c9402b96d986ae625dde0784d0d19ade10a0e3b516c7fda093a7f467d9c96549e0de5482b4fff347bd31987656ac67a840e03d8e975fed424a6e\",\"key_id\":\"23\",\"sign\":\"b8e460b3\"}");
        // 获取当前时间戳
        long uptime = System.currentTimeMillis();
        // 请求地址
        String url = "https://graph.baidu.com/upload?uptime=" + uptime;

        try {
            // 2. 发送 POST 请求到百度接口（补充必要头：acs-token，硬编码）
            HttpRequest httpReq = HttpRequest.post(url)
                    .form(formData)
                    .timeout(5000)

                    .header("acs-token", "1756095554803_1756110644790_9Lc7n7qt9E22hEHi1DMyzQSJGpyd7mW8GgRLHg08RV38Ozw9MgNAqAiRF1dEBL5iUWxsRn5JxlYDPRg7WHxXGjsFIbbHrjzK8iQvmxbvG/73gj1z2RXjzSrSBgpKjTnl5HHhkBe6IawKdlW0I7eTDVIRNYKymk7SUjqKDEKJGJl0QJV21ht8JmvzGMd5er5KyWv3kMAdkhx6s6+qNzutEtDVD/JlUBYnLmaP+paviHl9wYyB9IMQD7147csOVLBNDwVvbgqukWTfOBg8HwoRna3/FccTF1bCUv5oTngtdjvOdBG112qXQuYx6sMl6ksNwUE4JhFZy4EZTUKkUGTcmt2N6KFQmYR6fFWFW7FUWYoFCEEbt5kxf8ACFyW5DWZzw84dAvJlIer8zdxgd1wZfkrWoi3ZvrS5W/lNwS8xvSATsQgJrJduzLs8lH8PXw0x");
            HttpResponse response = httpReq.execute();
            // 判断响应状态
            if (HttpStatus.HTTP_OK != response.getStatus()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            // 解析响应
            String responseBody = response.body();
            Map<String, Object> result = JSONUtil.toBean(responseBody, Map.class);

            // 3. 处理响应结果
            if (result == null || !Integer.valueOf(0).equals(result.get("status"))) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            String rawUrl = (String) data.get("url");
            // 对 URL 进行解码
            String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);
            // 还原转义字符，使URL可以直接在浏览器中访问
            searchResultUrl = searchResultUrl.replace("\\u0026", "&");
            // 如果 URL 为空
            if (searchResultUrl == null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "未返回有效结果");
            }
            return searchResultUrl;
        } catch (Exception e) {
            log.error("搜索失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜索失败");
        }
    }

    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://www.codefather.cn/logo.png";
        String result = getImagePageUrl(imageUrl);
        System.out.println("搜索成功，结果 URL：" + result);
    }
}
