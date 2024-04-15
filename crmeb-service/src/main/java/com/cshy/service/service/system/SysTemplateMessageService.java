package com.cshy.service.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.entity.wechat.SysTemplateMessage;
import com.cshy.common.model.vo.MyRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  TemplateMessageService 接口

 */
public interface SysTemplateMessageService extends IService<SysTemplateMessage> {

    /**
     * 公众号消费队列消费
     */
    void consumePublic();

    /**
     * 小程序消费队列消费
     */
    void consumeProgram();

    /**
     * 发送公众号模板消息
     * @param templateId 模板消息编号
     * @param temMap 内容Map
     * @param openId 微信用户openId
     */
    void pushTemplateMessage(Integer templateId, HashMap<String, String> temMap, String openId);

    /**
     * 发送小程序订阅消息
     * @param templateId 模板消息编号
     * @param temMap 内容Map
     * @param openId 微信用户openId
     */
    void pushMiniTemplateMessage(Integer templateId, HashMap<String, String> temMap, String openId);

    /**
     * 修改模板状态
     * @param id 模板id
     * @param status 状态
     */
    Boolean updateStatus(Integer id, Integer status);

    /**
     * 公众号模板消息同步
     * @return Boolean
     */
    Boolean wechatPublicSync();

    /**
     * 小程序订阅消息同步
     * @return Boolean
     */
    Boolean routineSync();

    /**
     * 获取详情
     * @param id id
     */
    SysTemplateMessage info(Integer id);

    /**
     * 获取模板列表
     * @param tidList id数组
     * @return List
     */
    List<SysTemplateMessage> getByIdList(List<Integer> tidList);

    /**
     * 发送模板消息
     * @param sysTemplateMessage 消息模板
     * @param openId
     * @param params 模板参数
     */
    void send(SysTemplateMessage sysTemplateMessage, String openId, List<String> params);
}
