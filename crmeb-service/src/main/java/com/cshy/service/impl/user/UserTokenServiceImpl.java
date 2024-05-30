package com.cshy.service.impl.user;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.model.entity.user.UserToken;
import com.cshy.service.dao.user.UserTokenDao;
import com.cshy.service.service.user.UserTokenService;
import com.cshy.service.service.wechat.WechatCommonService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * UserTokenServiceImpl 接口实现
 */
@Service
public class UserTokenServiceImpl extends ServiceImpl<UserTokenDao, UserToken> implements UserTokenService {

    @Resource
    private UserTokenDao dao;

    @Resource
    private WechatCommonService wechatCommonService;

    /**
     * 检测token是否存在
     *
     * @param token String openId
     * @param type  int 类型
     * @return UserToken
     */
    @Override
    public UserToken getByOpenidAndType(String token, int type) {
        LambdaQueryWrapper<UserToken> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserToken::getType, type).eq(UserToken::getToken, token);
        return dao.selectOne(lambdaQueryWrapper);
    }

    @Override
    public void bind(String token, int type, Integer userId) {
        UserToken userToken = new UserToken();
        userToken.setToken(token);
        userToken.setType(type);
        userToken.setUid(userId);
        save(userToken);
    }

    @Override
    public UserToken getTokenByUserId(Integer userId, int type) {
        LambdaQueryWrapper<UserToken> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserToken::getUid, userId).eq(UserToken::getType, type);
        return dao.selectOne(lambdaQueryWrapper);
    }

    @Override
    public String getOpenIdByCode(String code, Integer userId, Integer type) {
        String openid;
        //不存在则获取
        JSONObject openIdByCode = wechatCommonService.getOpenIdByCode(code, type);
        openid = (String) openIdByCode.get("openid");
        UserToken userToken = new UserToken();
        if (type == 1)
            userToken.setType(1);
        else
            userToken.setType(2);
        userToken.setToken(openid);
        userToken.setUid(userId);
        this.save(userToken);
        return openid;
    }
}

