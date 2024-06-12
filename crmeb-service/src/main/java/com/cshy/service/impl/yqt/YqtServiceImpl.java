package com.cshy.service.impl.yqt;

import com.alibaba.fastjson.JSONObject;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.utils.RedisUtil;
import com.cshy.service.service.store.StoreOrderService;
import com.cshy.service.service.store.StoreProductService;
import com.cshy.service.service.yqt.YqtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class YqtServiceImpl implements YqtService {
    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private RedisUtil redisUtil;

    private static final String REDIS_KEY = "YQT_TOKEN";
    private static final String TEST_APPID = "554dd352c4185966e04bf6f3";
    private static final String TEST_SECRET = "rptx7q4ixyoldn2ikhbdt3t13657387v";
    private static final String TEST_URL = "http://open.test.yqtbuy.com";

    public String getToken(){
        String url = TEST_URL + "/open/v2/login/getAccessToken";


        Map<String, Object> params = new HashMap<>();
        params.put("appId", TEST_APPID);
        params.put("secret", TEST_SECRET);

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        // 请求
        HttpEntity<String> request = new HttpEntity<>(JSONObject.toJSONString(params), headers);

        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject httpBody = responseBody.getBody();
        if (httpBody != null && httpBody.getInteger("code") == 10000) {
            JSONObject data = httpBody.getJSONObject("data");
            String accessToken = data.getString("accessToken");
            redisUtil.set(REDIS_KEY, accessToken, data.getLongValue("expireIn"), TimeUnit.SECONDS);
            return accessToken;
        }
        throw new CrmebException("获取token失败");
    }
}
