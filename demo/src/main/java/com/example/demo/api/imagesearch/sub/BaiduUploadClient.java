package com.example.demo.api.imagesearch.sub;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * 以 Java 方式调用百度图搜上传接口，提供与 GetImagePageUrlApi 等价的方法。
 */
public class BaiduUploadClient {

    public static void main(String[] args) throws Exception {

        String imageParam = "https://joy-yyds-1369159021.cos.ap-chengdu.myqcloud.com/public/1959441089008041985/2025-08-24_mrDERdKSumiff9Pu.webp";


        String imageUrl = args.length > 0 ? args[0] : "https://www.codefather.cn/logo.png";
        String result = getImagePageUrl("https://joy-yyds-1369159021.cos.ap-chengdu.myqcloud.com/public/1957791449871413249/2025-08-20_zCocOLq2WIaLH3WV.webp");
        System.out.println("搜索成功，结果 URL：" + result);



    }

    /**
     * 发送请求并返回解码后的结果页 URL
     */
    public static String getImagePageUrl(String imageUrl) throws Exception {
        long uptime = System.currentTimeMillis();
        String url = "https://graph.baidu.com/upload?uptime=" + uptime;

        String boundary = "----WebKitFormBoundaryUYlMf9DqNIy8jumP";
        String encodedImage = URLEncoder.encode(imageUrl, StandardCharsets.UTF_8);
        String body =
                "------WebKitFormBoundaryUYlMf9DqNIy8jumP\r\n" +
                        "Content-Disposition: form-data; name=\"image\"\r\n\r\n" +
                        encodedImage + "\r\n" +
                        "------WebKitFormBoundaryUYlMf9DqNIy8jumP\r\n" +
                        "Content-Disposition: form-data; name=\"tn\"\r\n\r\n" +
                        "pc\r\n" +
                        "------WebKitFormBoundaryUYlMf9DqNIy8jumP\r\n" +
                        "Content-Disposition: form-data; name=\"from\"\r\n\r\n" +
                        "pc\r\n" +
                        "------WebKitFormBoundaryUYlMf9DqNIy8jumP\r\n" +
                        "Content-Disposition: form-data; name=\"image_source\"\r\n\r\n" +
                        "PC_UPLOAD_URL\r\n" +
                        "------WebKitFormBoundaryUYlMf9DqNIy8jumP\r\n" +
                        "Content-Disposition: form-data; name=\"sdkParams\"\r\n\r\n" +
                        "{\\\"data\\\":\\\"8f7588d07e048cb185895de2ac09bfbe055aff1048e6c9402b96d986ae625dde0784d0d19ade10a0e3b516c7fda093a7f467d9c96549e0de5482b4fff347bd31987656ac67a840e03d8e975fed424a6e\\\",\\\"key_id\\\":\\\"23\\\",\\\"sign\\\":\\\"b8e460b3\\\"}\r\n" +
                        "------WebKitFormBoundaryUYlMf9DqNIy8jumP--\r\n";

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("content-type", "multipart/form-data; boundary=" + boundary)
                .header("acs-token", "1756095554803_1756110644790_9Lc7n7qt9E22hEHi1DMyzQSJGpyd7mW8GgRLHg08RV38Ozw9MgNAqAiRF1dEBL5iUWxsRn5JxlYDPRg7WHxXGjsFIbbHrjzK8iQvmxbvG/73gj1z2RXjzSrSBgpKjTnl5HHhkBe6IawKdlW0I7eTDVIRNYKymk7SUjqKDEKJGJl0QJV21ht8JmvzGMd5er5KyWv3kMAdkhx6s6+qNzutEtDVD/JlUBYnLmaP+paviHl9wYyB9IMQD7147csOVLBNDwVvbgqukWTfOBg8HwoRna3/FccTF1bCUv5oTngtdjvOdBG112qXQuYx6sMl6ksNwUE4JhFZy4EZTUKkUGTcmt2N6KFQmYR6fFWFW7FUWYoFCEEbt5kxf8ACFyW5DWZzw84dAvJlIer8zdxgd1wZfkrWoi3ZvrS5W/lNwS8xvSATsQgJrJduzLs8lH8PXw0x")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) {
            throw new RuntimeException("接口调用失败");
        }
        String respBody = response.body();
        if (respBody == null || !respBody.contains("\"status\":0")) {
            throw new RuntimeException("接口调用失败");
        }
        String urlKey = "\"url\":\"";
        int idx = respBody.indexOf(urlKey);
        if (idx < 0) {
            throw new RuntimeException("未返回有效结果");
        }
        int start = idx + urlKey.length();
        int end = respBody.indexOf('"', start);
        if (end < 0) {
            throw new RuntimeException("未返回有效结果");
        }
        String rawUrl = respBody.substring(start, end);
        String decoded = URLDecoder.decode(rawUrl, StandardCharsets.UTF_8);
        // 还原转义字符，使URL可以直接在浏览器中访问
        decoded = decoded.replace("\\u0026", "&");
        if (decoded == null || decoded.isEmpty()) {
            throw new RuntimeException("未返回有效结果");
        }
        return decoded;
    }
}


