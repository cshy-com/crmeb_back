package com.cshy.common.enums;

public enum SmsTriggerEnum {
    VERIFICATION_CODE(1, "发送验证码"),
    ORDER_PLACED_TO_CUSTOMER(2, "下单成功通知客户"),
    ORDER_PLACED_TO_EMPLOYEE(3, "下单成功通知员工"),
    ITEMS_SHIPPED(4, "发货后通知用户"),
    RETURN_ITEMS_SHIPPED(5, "退货到达通知"),
    RETURN_REQUEST_SUBMITTED(6,  "退款申请提交通知"),
    RETURN_REQUEST_APPROVED(7,  "退款申请通过通知"),

    ;

    private Integer code;
    private String desc;

    SmsTriggerEnum(Integer code, String desc){
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