package com.cshy.service.impl.wechat;

import com.alibaba.fastjson.JSONObject;
import com.cshy.common.constants.WeChatConstants;
import com.cshy.common.utils.RedisUtil;
import com.cshy.service.service.wechat.WechatCommonService;
import com.cshy.service.service.wechat.WechatPublicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 微信公众号Service实现类

 */
@Service
public class WechatPublicServiceImpl implements WechatPublicService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WechatCommonService wechatCommonService;

    /**
     * 获取公众号自定义菜单
     * @return Object
     */
    @Override
    public Object getCustomizeMenus() {
        Object list = redisUtil.get(WeChatConstants.REDIS_PUBLIC_MENU_KEY);
        if (list == null || "".equals(list)) {
            //如果没有， 去读取
            JSONObject tagsList = wechatCommonService.getPublicCustomMenu();
            redisUtil.set(WeChatConstants.REDIS_PUBLIC_MENU_KEY, tagsList);
            list = tagsList;
        }
        return list;
    }

    /**
     * 保存自定义菜单
     * @param data 菜单json
     * @return Boolean
     */
    @Override
    public Boolean createMenus(String data) {
        Boolean create = wechatCommonService.createPublicCustomMenu(data);
        if (!create) {
            return create;
        }
        // 清除历史缓存
        if (redisUtil.exists(WeChatConstants.REDIS_PUBLIC_MENU_KEY)) {
            redisUtil.delete(WeChatConstants.REDIS_PUBLIC_MENU_KEY);
        }
        return create;
    }

    /**
     * 删除自定义菜单
     * @return Boolean
     */
    @Override
    public Boolean deleteMenus() {
        Boolean delete = wechatCommonService.deletePublicCustomMenu();
        if (!delete) {
            return delete;
        }
        // 清除历史缓存
        if (redisUtil.exists(WeChatConstants.REDIS_PUBLIC_MENU_KEY)) {
            redisUtil.delete(WeChatConstants.REDIS_PUBLIC_MENU_KEY);
        }
        return delete;
    }
}
