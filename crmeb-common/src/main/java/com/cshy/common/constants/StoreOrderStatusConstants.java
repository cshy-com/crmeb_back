package com.cshy.common.constants;

public class StoreOrderStatusConstants {
    //订单状态
    public static final String ORDER_STATUS_ALL = "all"; //所有
    public static final String ORDER_STATUS_UNPAID = "unPaid"; //未支付
    public static final String ORDER_STATUS_CANCEL = "cancel"; //已取消
    public static final String ORDER_STATUS_NOT_SHIPPED = "notShipped"; //未发货
    public static final String ORDER_STATUS_SPIKE = "spike"; //待收货
    public static final String ORDER_STATUS_BARGAIN = "bargain"; //已收货待评价
    public static final String ORDER_STATUS_COMPLETE = "complete"; //交易完成
    public static final String ORDER_STATUS_TOBE_WRITTEN_OFF = "toBeWrittenOff"; //待核销
    public static final String ORDER_STATUS_APPLY_REFUNDING = "applyRefund"; //申请退款
    public static final String ORDER_STATUS_APPLY_REFUNDING_RETURN = "refundNReturn"; //申请退货退款
    public static final String ORDER_STATUS_APPLY_WAIT_FOR_SHIP = "waitForShip"; //退货待发货
    public static final String ORDER_STATUS_APPLY_SHIPPING = "shipping"; //退货已发货
    public static final String ORDER_STATUS_REFUNDING = "refunding"; //退款中
    public static final String ORDER_STATUS_REFUNDED = "refunded"; //已退款
    public static final String ORDER_STATUS_DELETED = "deleted"; //已删除
    public static final String ORDER_STATUS_CACHE_CREATE_ORDER = "cache_key_create_order";

    public static final String ORDER_STATUS_STR_UNPAID = "未支付"; //未支付
    public static final String ORDER_STATUS_STR_CANCEL = "已取消"; //已取消
    public static final String ORDER_STATUS_STR_NOT_SHIPPING = "未发货"; //未发货
    public static final String ORDER_STATUS_STR_SHIPPING = "已发货"; //未发货
    public static final String ORDER_STATUS_STR_SPIKE = "待收货"; //待收货
    public static final String ORDER_STATUS_STR_BARGAIN = "待评价"; //已收货待评价
    public static final String ORDER_STATUS_STR_TAKE = "用户已收货"; //用户已收货
    public static final String ORDER_STATUS_STR_COMPLETE = "交易完成"; //交易完成
    public static final String ORDER_STATUS_STR_TOBE_WRITTEN_OFF = "待核销"; //待核销
    public static final String ORDER_STATUS_STR_APPLY_REFUNDING = "申请退款"; //申请退款
    public static final String ORDER_STATUS_STR_APPLY_REFUNDING_RETURN = "申请退货退款"; //申请退款退货退款
    public static final String ORDER_STATUS_STR_REFUNDING = "退款中"; //退款中
    public static final String ORDER_STATUS_STR_REFUNDED = "已退款"; //已退款
    public static final String ORDER_STATUS_STR_DELETED = "已删除"; //已删除
    public static final String ORDER_STATUS_STR_APPLY_WAIT_FOR_SHIP = "退货待发货"; //退货待发货
    public static final String ORDER_STATUS_STR_APPLY_SHIPPING = "退货已发货"; //退货已发货
    public static final String ORDER_STATUS_STR_AGREE_RETURN = "同意退货退款"; //同意退货退款
    public static final String ORDER_STATUS_STR_AGREE_REFUND = "同意退款"; //同意退款

    public static final String ORDER_STATUS_STR_REFUND_REVOKE = "撤销售后"; //撤销售后

    // H5 端订单状态
    public static final int ORDER_STATUS_H5_UNPAID = 0; // 未支付
    public static final int ORDER_STATUS_H5_NOT_SHIPPED = 1; // 待发货
    public static final int ORDER_STATUS_H5_SPIKE = 2; // 待收货
    public static final int ORDER_STATUS_H5_JUDGE = 3; // 待评价
    public static final int ORDER_STATUS_H5_COMPLETE = 4; // 已完成
    public static final int ORDER_STATUS_H5_VERIFICATION = 5; // 待核销
    public static final int ORDER_STATUS_H5_REFUNDING = -1; // 退款中
    public static final int ORDER_STATUS_H5_REFUNDED = -2; // 已退款
    public static final int ORDER_STATUS_H5_REFUND = -3; // 退款
    public static final int ORDER_STATUS_H5_REFUND_PROCESSING = -4; // 待处理

    public static final int ORDER_STATUS_INT_PAID = 0; //已支付
    public static final int ORDER_STATUS_INT_SPIKE = 1; //待收货
    public static final int ORDER_STATUS_INT_BARGAIN = 2; //已收货，待评价
    public static final int ORDER_STATUS_INT_COMPLETE = 3; //已完成

    //订单操作类型
    public static final String ORDER_LOG_REFUND_PRICE = "refund_price"; //退款
    public static final String ORDER_LOG_CANCEL = "cancel"; //退款
    public static final String ORDER_LOG_EXPRESS = "express"; //快递
    public static final String ORDER_LOG_PICKUP = "pickup"; //快递
    public static final String ORDER_LOG_REFUND_REFUSE = "refund_refuse"; //不退款
    public static final String ORDER_LOG_REFUND_APPLY = "apply_refund"; //申请退款
    public static final String ORDER_LOG_PAY_SUCCESS = "pay_success"; //支付成功
    public static final String ORDER_LOG_DELIVERY_VI = "delivery_fictitious"; //虚拟发货
    public static final String ORDER_LOG_EDIT = "order_edit"; //编辑订单
    public static final String ORDER_LOG_RETURN_GOODS = "return_goods"; //退货中
    public static final String ORDER_LOG_AGREE_RETURN = "agree_return"; //同意退货退款
    public static final String ORDER_LOG_AGREE_REFUND = "agree_refund"; //同意退款
    public static final String ORDER_LOG_REFUND_REVOKE = "refund_revoke"; //同意退款
}
