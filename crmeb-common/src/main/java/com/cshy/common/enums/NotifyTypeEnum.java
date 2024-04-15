package com.cshy.common.enums;

public enum NotifyTypeEnum {
    PAY_SUCCESS(1, "订单支付成功通知"),
    DELIVER_GOODS(2, "订单发货通知"),
    BARGAINING_SUCCESS(3, "砍价成功通知"),
    GROUP_SUCCESS(4, "拼团成功通知"),
    FULFILLMENT_ORDER(5, "订单配送通知"),
    RECHARGE_SUCCESS(6,  "充值成功通知"),
    RECEIPT_GOODS(7,  " 确认收货通知"),
    ADMIN_PAY_SUCCESS(8,  " 支付成功(管理员短信)通知"),
    ADMIN_RECEIPT_GOODS(9,  "用户收货(管理员)通知"),
    ADMIN_PLACE_ORDER(10,  "用户下单(管理员)通知"),
    ADMIN_APPLY_ORDER_REFUND(11,  "用户发起退款(管理员)通知"),
    MODIFY_ORDER_PRICE(12,  "改价通知"),
    VERIFICATION_CODE(13,  "验证码"),

    ;

    private Integer code;
    private String desc;

    NotifyTypeEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}