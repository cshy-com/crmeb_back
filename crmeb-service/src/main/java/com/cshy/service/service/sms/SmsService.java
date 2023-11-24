package com.cshy.service.service.sms;


import com.cshy.common.model.entity.sms.SmsRecord;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.request.sms.SmsRecordsRequest;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * SmsService 接口

 */
public interface SmsService {

    /**
     *
     * @param phoneNumber
     * @param triggerPosition
     * @param request
     * @param params
     */
    void sendCode(String phoneNumber, Integer triggerPosition, HttpServletRequest request, String... params);

    /**
     * 阿里云短信服务建立通信
     * @param accessKeyId
     * @param accessKeySecret
     * @return
     */
    com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception;

    /**
     * 发送支付成功短信
     * @param phone 手机号
     * @param orderNo 订单编号
     * @param payPrice 支付金额
     * @param msgTempId 短信模板id
     * @return Boolean
     */
    Boolean sendPaySuccess(String phone, String orderNo, BigDecimal payPrice, Integer msgTempId);

    /**
     * 发送管理员下单短信提醒短信
     * @param phone 手机号
     * @param orderNo 订单编号
     * @param realName 管理员名称
     * @param msgTempId 短信模板id
     */
    Boolean sendCreateOrderNotice(String phone, String orderNo, String realName, Integer msgTempId);

    /**
     * 发送订单支付成功管理员提醒短信
     * @param phone 手机号
     * @param orderNo 订单编号
     * @param realName 管理员名称
     * @param msgTempId 短信模板id
     */
    Boolean sendOrderPaySuccessNotice(String phone, String orderNo, String realName, Integer msgTempId);

    /**
     * 发送用户退款管理员提醒短信
     * @param phone 手机号
     * @param orderNo 订单编号
     * @param realName 管理员名称
     * @param msgTempId 短信模板id
     */
    Boolean sendOrderRefundApplyNotice(String phone, String orderNo, String realName, Integer msgTempId);

    /**
     * 发送用户确认收货管理员提醒短信
     * @param phone 手机号
     * @param orderNo 订单编号
     * @param realName 管理员名称
     * @param msgTempId 短信模板id
     */
    Boolean sendOrderReceiptNotice(String phone, String orderNo, String realName, Integer msgTempId);

    /**
     * 发送订单改价提醒短信
     * @param phone 手机号
     * @param orderNo 订单编号
     * @param price 修改后的支付金额
     * @param msgTempId 短信模板id
     */
    Boolean sendOrderEditPriceNotice(String phone, String orderNo, BigDecimal price, Integer msgTempId);

    /**
     * 发送订单发货提醒短信
     * @param phone 手机号
     * @param nickName 用户昵称
     * @param storeName 商品名称
     * @param orderNo 订单编号
     * @param msgTempId 短信模板id
     */
    Boolean sendOrderDeliverNotice(String phone, String nickName, String storeName, String orderNo, Integer msgTempId);


    /**
     * 查询发送短信记录列表
     * @param smsRecordsRequest
     * @param pageParamRequest
     * @return
     */
    CommonPage<SmsRecord> page(SmsRecordsRequest smsRecordsRequest, PageParamRequest pageParamRequest);
}
