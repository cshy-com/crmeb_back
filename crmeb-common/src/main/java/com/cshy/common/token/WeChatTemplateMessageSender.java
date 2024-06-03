package com.cshy.common.token;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.auth.WechatPay2Validator;
import com.wechat.pay.java.core.cipher.Verifier;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import com.wechat.pay.java.core.util.PemUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivateKey;

public class WeChatTemplateMessageSender {

    public static void sendTemplateMessage(String accessToken, String templateMessage) {
        try {
            // 构建请求 URL
            URL url = new URL("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken);

            // 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // 发送请求内容
            try (OutputStream os = connection.getOutputStream()) {
                os.write(templateMessage.getBytes());
                os.flush();
            }

            // 获取响应
            int responseCode = connection.getResponseCode();
            // 处理响应...
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应内容
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    System.out.println(response);
                }
            } else {
                System.out.println("Failed to get access token. HTTP response code: " + responseCode);
            }
            // 关闭连接
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getToken(String appid, String secret) {
        try {
            URL url = new URL("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + secret);

            // 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // 获取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应内容
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    JSONObject obj = JSON.parseObject(response.toString());
                    System.out.println(obj);
                    return (String) obj.get("access_token");
                }
            } else {
                System.out.println("Failed to get access token. HTTP response code: " + responseCode);
            }

            // 关闭连接
            connection.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("找不到");
    }

//    public static void main(String[] args) {
//        String accessToken = getToken("wx3ac1f72118da0ff7", "7aee0f00d792830d38bdb4e5e25fd517");
//        String templateMessage = "{\n" +
//                "    \"touser\": \"o4tZL675cSPNTbjr0oReDlFigA-I\",\n" +
//                "    \"template_id\": \"iCr51eoxpNU8K7GyeDBsqxBWpIaRA3RggpuJ9eGTSaU\",\n" +
//                "    \"url\": \"https://cshy.store/front/#/pages/order_details/index?order_id=order15051169949995643191243\",\n" +
//                "    \"data\": {\n" +
//                "        \"amount3\": {\n" +
//                "            \"value\": \"1111111\"\n" +
//                "        },\n" +
//                "        \"thing2\": {\n" +
//                "            \"value\": \"test\"\n" +
//                "        },\n" +
//                "        \"character_string14\": {\n" +
//                "            \"value\": \"test22222\"\n" +
//                "        },\n" +
//                "        \"time6\": {\n" +
//                "            \"value\": \"2024.01.15 11:55:00\"\n" +
//                "        },\n" +
//                "        \"phrase9\": {\n" +
//                    "            \"value\": \"测试\"\n" +
//                "        }\n" +
//                "    }\n" +
//                "}";
//
////        sendTemplateMessage(accessToken, templateMessage);
//
//
////        getPubTemplateKeyWordsById(accessToken, "iCr51eoxpNU8K7GyeDBsqxBWpIaRA3RggpuJ9eGTSaU");
//
//        getTemplateList(accessToken);
//    }

    private static void getTemplateList(String accessToken) {
        try {
            // 构建请求 URL
            URL url = new URL("https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=" + accessToken);

            // 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 获取响应
            int responseCode = connection.getResponseCode();
            // 处理响应...
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应内容
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    System.out.println(response);
                }
            } else {
                System.out.println("Failed to get access token. HTTP response code: " + responseCode);
            }
            // 关闭连接
            connection.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void getPubTemplateKeyWordsById(String accessToken, String tid) {
        try {
            // 构建请求 URL
            URL url = new URL("https://api.weixin.qq.com/wxaapi/newtmpl/getpubtemplatekeywords?tid="+tid+"&access_token=" + accessToken);

            // 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 获取响应
            int responseCode = connection.getResponseCode();
            // 处理响应...
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应内容
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    System.out.println(response);
                }
            } else {
                System.out.println("Failed to get access token. HTTP response code: " + responseCode);
            }
            // 关闭连接
            connection.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}