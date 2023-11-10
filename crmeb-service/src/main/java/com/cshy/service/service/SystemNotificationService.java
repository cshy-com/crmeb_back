package com.cshy.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.entity.system.SystemNotification;
import com.cshy.common.model.entity.wechat.TemplateMessage;
import com.cshy.common.model.request.NotificationInfoRequest;
import com.cshy.common.model.request.NotificationSearchRequest;
import com.cshy.common.model.request.NotificationUpdateRequest;
import com.cshy.common.model.response.NotificationInfoResponse;

import java.util.List;

/**
 * 短信模板表

 */
public interface SystemNotificationService extends IService<SystemNotification> {

    /**
     * 系统通知列表
     * @param request 查询对象
     * @return List
     */
    List<SystemNotification> getList(NotificationSearchRequest request);

    /**
     * 公众号模板开关
     * @param id 通知id
     * @return Boolean
     */
    Boolean wechatSwitch(Integer id);

    /**
     * 小程序订阅模板开关
     * @param id 通知id
     * @return Boolean
     */
    Boolean routineSwitch(Integer id);

    /**
     * 发送短信开关
     * @param id 通知id
     * @return Boolean
     */
    Boolean smsSwitch(Integer id);

    /**
     * 通知详情
     * @param request 详情请求参数
     * @return NotificationInfoResponse
     */
    NotificationInfoResponse getDetail(NotificationInfoRequest request);

    /**
     * 根据标识查询信息
     * @param mark 标识
     * @return SystemNotification
     */
    SystemNotification getByMark(String mark);

    /**
     * 获取微信相关列表
     * @param type routine-小程序，public-公众号
     */
    List<SystemNotification> getListByWechat(String type);

    /**
     * 修改通知
     * @param request 请求参数
     * @return Boolean
     */
    Boolean modify(NotificationUpdateRequest request);

    /**
     * 获取小程序订阅模板编号(小程序端调用)
     * @param type 场景类型(支付之前：beforePay|支付成功：afterPay|申请退款：refundApply|充值之前：beforeRecharge|创建砍价：createBargain|参与拼团：pink|取消拼团：cancelPink)
     * @return List
     */
    List<TemplateMessage> getMiniTempList(String type);
}
