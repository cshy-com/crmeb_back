package com.cshy.service.service;


import com.cshy.common.enums.SMSTemplateEnum;
import com.cshy.common.model.entity.sms.SmsRecord;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.request.SmsApplyTempRequest;
import com.cshy.common.model.request.SmsModifySignRequest;
import com.cshy.common.model.request.SmsRecordsRequest;
import com.cshy.common.model.vo.MyRecord;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * SmsService 接口

 */
public interface SmsService {

    /**
     * 修改签名
     */
    Boolean modifySign(SmsModifySignRequest request);

    /**
     * 短信模板
     */
    MyRecord temps(PageParamRequest pageParamRequest);

    /**
     * 申请模板消息
     */
    Boolean applyTempMessage(SmsApplyTempRequest request);

    /**
     * 模板申请记录
     *
     * @param type (1=验证码 2=通知 3=推广)
     */
    MyRecord applys(Integer type, PageParamRequest pageParamRequest);


    com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception;

    /**
     * 发送公共验证码
     *
     * @param phoneNumber 手机号
     * @param type SMSTemplateEnum枚举
     */
    void sendCode(String phoneNumber, SMSTemplateEnum type, HttpServletRequest request, String... s);

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

    CommonPage<SmsRecord> page(SmsRecordsRequest smsRecordsRequest, PageParamRequest pageParamRequest);
}
