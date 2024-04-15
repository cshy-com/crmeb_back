package com.cshy.service.impl.wechat;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.model.entity.wechat.WechatCallback;
import com.cshy.service.dao.wechat.WechatCallbackDao;
import com.cshy.service.service.wechat.WechatCallbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 微信小程序回调Service

 */
@Service
public class WechatCallbackServiceImpl extends ServiceImpl<WechatCallbackDao, WechatCallback> implements WechatCallbackService {

    private static final Logger logger = LoggerFactory.getLogger(WechatCallbackServiceImpl.class);

    @Resource
    private WechatCallbackDao dao;

    /**
     * 微信回调
     * @param request request
     * @return String
     */
    @Override
    public String callback(String request) {
        System.out.println("微信小程序回调：" + request);
        JSONObject jsonObject = JSONObject.parseObject(request);
        System.out.println("微信小程序回调jsonObject：" + jsonObject);
        WechatCallback wechatCallback = new WechatCallback();
        wechatCallback.setToUserName(jsonObject.getString("ToUserName"));
        wechatCallback.setFromUserName(jsonObject.getString("FromUserName"));
        wechatCallback.setCreateTime(jsonObject.getLong("CreateTime"));
        wechatCallback.setMsgType(jsonObject.getString("MsgType"));
        wechatCallback.setEvent(jsonObject.getString("Event"));
        wechatCallback.setAddTime(DateUtil.date());
        wechatCallback.setContent(request);
        save(wechatCallback);
        switch (wechatCallback.getEvent()) {
        }
        return "success";
    }
}

