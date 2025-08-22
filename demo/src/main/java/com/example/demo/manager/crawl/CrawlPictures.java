package com.example.demo.manager.crawl;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.URLDecoder;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CrawlPictures{
	// 本地开关：置为 true 启用可用性过滤；置为 false 直接返回解析结果
	private static final boolean a = false;

	public static List<String> crawl(String searchText){
		return crawl(searchText, 0);
	}

	public static List<String> crawl(String searchText, int offset){
		List<String> result = new ArrayList<>();
		String keyword = (searchText == null || searchText.isBlank()) ? "猫咪" : searchText;
		int safeOffset = Math.max(0, offset);
		int first = safeOffset * 20 + 1; // first = 偏移量 * 20 + 1

		// 基于关键词与偏移量构造抓取地址
		String encodedQ = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
		String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&first=%d&count=20&relp=20&apc=0&datsrc=I&mmasync=1", encodedQ, first);

		try {
			Document document = Jsoup.connect(fetchUrl)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36")
					.referrer("https://www.bing.com/")
					.timeout(10000)
					.get();

			Set<String> urls = new LinkedHashSet<>();
			ObjectMapper mapper = new ObjectMapper();

			// 仅解析 a.iusc 的 m 字段中的 murl（原图地址）
			Elements aList = document.select("a.iusc[m]");
			for (Element aNode : aList) {
				String mAttr = aNode.attr("m");
				if (mAttr == null || mAttr.isEmpty()) continue;
				try {
					JsonNode node = mapper.readTree(mAttr);
					String murl = node.path("murl").asText("");
					if (!murl.isEmpty()) {
						try { murl = URLDecoder.decode(murl, StandardCharsets.UTF_8); } catch (Exception ignore) {}
						urls.add(murl);
					}
				} catch (Exception ignore) { /* 跳过无法解析的 */ }
			}

			// 返回前 20 条
			List<String> allUrls = new ArrayList<>(urls);
			if (allUrls.size() > 100) {
				allUrls = new ArrayList<>(allUrls.subList(0, 100));
			}
			return a ? filterAccessibleImages(allUrls, 100) : allUrls;
		} catch (IOException e) {
			return result; // 失败时返回空数组
		}
	}

	// 过滤：仅返回可在浏览器直接访问的图片链接
	private static List<String> filterAccessibleImages(List<String> candidateUrls, int limit) {
		List<String> filtered = new ArrayList<>();
		for (String u : candidateUrls) {
			if (!isLikelyImageUrl(u)) continue;
			if (filtered.size() >= limit) break;
			if (isImageUrlAvailable(u)) {
				filtered.add(u);
			}
		}
		return filtered;
	}

	// 基础校验：只接受 http/https 且非 data: 链接
	private static boolean isLikelyImageUrl(String url) {
		if (url == null) return false;
		String s = url.trim().toLowerCase();
		if (!(s.startsWith("http://") || s.startsWith("https://"))) return false;
		if (s.startsWith("data:")) return false;
		return true;
	}

	// 联网校验：优先 HEAD，不支持则 GET Range 0-0；检查状态码 + Content-Type
	private static boolean isImageUrlAvailable(String urlStr) {
		try {
			// HEAD
			HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
			conn.setInstanceFollowRedirects(true);
			conn.setRequestMethod("HEAD");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36");
			conn.setRequestProperty("Referer", "https://www.bing.com/");
			int code = conn.getResponseCode();
			String ct = conn.getContentType();
			if ((code == 200 || code == 206) && ct != null && ct.toLowerCase().startsWith("image/")) {
				conn.disconnect();
				return true;
			}
			conn.disconnect();

			// GET Range 0-0 作为兜底
			conn = (HttpURLConnection) new URL(urlStr).openConnection();
			conn.setInstanceFollowRedirects(true);
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setRequestProperty("Range", "bytes=0-0");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36");
			conn.setRequestProperty("Referer", "https://www.bing.com/");
			int code2 = conn.getResponseCode();
			String ct2 = conn.getContentType();
			conn.disconnect();
			return (code2 == 200 || code2 == 206) && ct2 != null && ct2.toLowerCase().startsWith("image/");
		} catch (Exception e) {
			return false;
		}
	}
}
