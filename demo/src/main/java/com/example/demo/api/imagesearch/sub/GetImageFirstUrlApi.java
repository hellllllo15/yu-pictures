package com.example.demo.api.imagesearch.sub;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GetImageFirstUrlApi {

    /**
     * 获取图片URL列表
     *
     * @param url
     * @return 图片URL数组
     */
    public static List<String> getImageUrls(String url) {
        List<String> imageUrls = new ArrayList<>();
        try {
            // 使用 Jsoup 获取 HTML 内容
            Document document = Jsoup.connect(url)
                    .timeout(10000)
                    .get();

            // 直接获取HTML字符串进行解析
            String htmlContent = document.html();
            
            // 1. 解析商品列表中的imgurl字段
            Pattern imgUrlPattern = Pattern.compile("\"imgurl\"\\s*:\\s*\"([^\"]+)\"");
            Matcher imgUrlMatcher = imgUrlPattern.matcher(htmlContent);
            while (imgUrlMatcher.find()) {
                String imgUrl = imgUrlMatcher.group(1);
                // 处理转义字符
                imgUrl = handleEscapeCharacters(imgUrl);
                if (!imageUrls.contains(imgUrl)) {
                    imageUrls.add(imgUrl);
                    log.info("找到商品图片URL: {}", imgUrl);
                }
            }
            
            // 2. 解析图片来源列表中的image_src字段
            Pattern imageSrcPattern = Pattern.compile("\"image_src\"\\s*:\\s*\"([^\"]+)\"");
            Matcher imageSrcMatcher = imageSrcPattern.matcher(htmlContent);
            while (imageSrcMatcher.find()) {
                String imageSrc = imageSrcMatcher.group(1);
                // 处理转义字符
                imageSrc = handleEscapeCharacters(imageSrc);
                if (!imageUrls.contains(imageSrc)) {
                    imageUrls.add(imageSrc);
                    log.info("找到图片来源URL: {}", imageSrc);
                }
            }
            
            // 3. 解析其他可能的图片URL字段
            Pattern imgUrlPattern2 = Pattern.compile("\"imgUrl\"\\s*:\\s*\"([^\"]+)\"");
            Matcher imgUrlMatcher2 = imgUrlPattern2.matcher(htmlContent);
            while (imgUrlMatcher2.find()) {
                String imgUrl = imgUrlMatcher2.group(1);
                imgUrl = handleEscapeCharacters(imgUrl);
                if (!imageUrls.contains(imgUrl)) {
                    imageUrls.add(imgUrl);
                    log.info("找到其他图片URL: {}", imgUrl);
                }
            }
            
            // 4. 解析imageUrl字段
            Pattern imageUrlPattern = Pattern.compile("\"imageUrl\"\\s*:\\s*\"([^\"]+)\"");
            Matcher imageUrlMatcher = imageUrlPattern.matcher(htmlContent);
            while (imageUrlMatcher.find()) {
                String imageUrl = imageUrlMatcher.group(1);
                imageUrl = handleEscapeCharacters(imageUrl);
                if (!imageUrls.contains(imageUrl)) {
                    imageUrls.add(imageUrl);
                    log.info("找到imageUrl: {}", imageUrl);
                }
            }
            
            log.info("总共找到 {} 个图片URL", imageUrls.size());
            
            // 如果没有找到任何图片URL，返回空列表而不是抛出异常
            return imageUrls;
            
        } catch (Exception e) {
            log.error("解析图片URL失败", e);
            // 返回空列表而不是抛出异常
            return new ArrayList<>();
        }
    }
    
    /**
     * 处理JSON字符串中的转义字符
     *
     * @param url 原始URL
     * @return 处理后的URL
     */
    private static String handleEscapeCharacters(String url) {
        if (url == null) {
            return null;
        }
        
        return url.replace("\\/", "/")
                 .replace("\\\"", "\"")
                 .replace("\\\\", "\\")
                 .replace("\\n", "\n")
                 .replace("\\r", "\r")
                 .replace("\\t", "\t");
    }

    public static void main(String[] args) {
        // 请求目标 URL
        String url = "https://graph.baidu.com/s?card_key=&entrance=GENERAL&extUiData[isLogoShow]=1&f=all&isLogoShow=1&session_id=5714075224956349620&sign=1268c3d483cd7ad6f0e6601756126672&tpl_from=pc";
        List<String> imageUrls = getImageUrls(url);
        System.out.println("搜索成功，找到 " + imageUrls.size() + " 个图片URL：");
        for (int i = 0; i < imageUrls.size(); i++) {
            System.out.println((i + 1) + ". " + imageUrls.get(i));
        }
    }
}
