package com.cshy.service.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.entity.user.UserToken;

/**
 * UserTokenService 接口实现

 */
public interface UserTokenService extends IService<UserToken> {

    /**
     * 获取UserToken
     * @param token 微信为openid
     * @param type 类型
     * @return UserToken
     */
    UserToken getByOpenidAndType(String token, int type);

    void bind(String openId, int type, Integer uid);

    UserToken getTokenByUserId(Integer userId, int type);

    String getOpenIdByCode(String code, Integer userId, Integer type);
}
