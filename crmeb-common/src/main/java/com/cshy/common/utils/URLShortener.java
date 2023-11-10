package com.cshy.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class URLShortener {
    private static final String BASE_URL = "http://cshy.cn/";
    private static final int SHORT_CODE_LENGTH = 7;
    private Map<String, String> urlMap; // 用于存储映射关系
    private Random random;

    public URLShortener() {
        this.urlMap = new HashMap<>();
        this.random = new Random();
    }

    // 将长URL转换为短URL
    public String shortenURL(String longURL) {
        String shortCode = generateShortCode();
        String shortURL = BASE_URL + shortCode;
        urlMap.put(shortCode, longURL);
        return shortURL;
    }

    // 生成随机的短码
    private String generateShortCode() {
        StringBuilder shortCode = new StringBuilder();
        String characters = "hdwiqobt12-0851-u70ifasjofbapfasfj029581";

        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int index = random.nextInt(characters.length());
            shortCode.append(characters.charAt(index));
        }

        return shortCode.toString();
    }

    // 根据短URL还原为长URL
    public String expandURL(String shortURL) {
        String shortCode = shortURL.replace(BASE_URL, "");
        return urlMap.get(shortCode);
    }

    public static void main(String[] args) {
        URLShortener shortener = new URLShortener();
        String longURL = "https://www.example.com/this/is/a/very/long/url";
        String shortURL = shortener.shortenURL(longURL);
        System.out.println("Short URL: " + shortURL);

        String expandedURL = shortener.expandURL(shortURL);
        System.out.println("Expanded URL: " + expandedURL);
    }
}