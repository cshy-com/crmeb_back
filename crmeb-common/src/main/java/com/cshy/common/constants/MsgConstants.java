package com.cshy.common.constants;

public class MsgConstants {
    // 订单 基本 操作字样
    public static String RESULT_ORDER_NOTFOUND = "订单号 ${orderCode} 未找到";
    public static String RESULT_ORDER_NOTFOUND_IN_ID = "订单id ${orderId} 未找到";
    public static String RESULT_ORDER_PAYED = "订单号 ${orderCode} 已支付";

    public static String RESULT_ORDER_EDIT_PRICE_SAME = "修改价格不能和支付价格相同 原价 ${oldPrice} 修改价 ${editPrice}";
    public static String RESULT_ORDER_EDIT_PRICE_SUCCESS = "订单号 ${orderNo} 修改价格 ${price} 成功";
    public static String RESULT_ORDER_EDIT_PRICE_LOGS = "订单价格 ${orderPrice} 修改实际支付金额为 ${price} 元";

    // 订单 支付 操作字样
    public static String RESULT_ORDER_PAY_OFFLINE = "订单号 ${orderNo} 现在付款 ${price} 成功";

    // 订单核销 返回字样 Order response text info
    public static String RESULT_VERIFICATION_ORDER_NOT_FUND = "核销码 ${vCode} 的订单未找到";
    public static String RESULT_VERIFICATION_ORDER_VED = "核销码 ${vCode} 的订单已核销";
    public static String RESULT_VERIFICATION_NOTAUTH = "没有核销权限";
    public static String RESULT_VERIFICATION_USER_EXIST = "当前用户已经是核销员";


    // QRcode Response text info
    public static String RESULT_QRCODE_PRAMERROR = "生成二维码参数不合法";

    //订单操作类型 -> 消息
    public static final String ORDER_LOG_MESSAGE_REFUND_PRICE = "退款给用户{amount}元"; //退款
    public static final String ORDER_LOG_MESSAGE_EXPRESS = "已发货 快递公司：{deliveryName}, 快递单号：{deliveryCode}"; //快递
    public static final String ORDER_LOG_MESSAGE_DELIVERY = "已配送 发货人：{deliveryName}, 发货人电话：{deliveryCode}"; //送货
    public static final String ORDER_LOG_MESSAGE_REFUND_REFUSE = "{reason}"; //不退款款因
    public static final String ORDER_LOG_MESSAGE_PAY_SUCCESS = "用户付款成功"; //用户付款成功

    //用户等级升级
    public static final String USER_LEVEL_OPERATE_LOG_MARK = "尊敬的用户 【{$userName}】, 在{$date}赠送会员等级成为{$levelName}会员";
    public static final String USER_LEVEL_UP_LOG_MARK = "尊敬的用户 【{$userName}】, 在{$date}您升级为为{$levelName}会员";
}
