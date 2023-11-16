package com.cshy.common.enums;

public enum SmsTriggerEnum {
    VERIFICATION_CODE(0, "发送验证码"),
    ORDER_PLACED_TO_CUSTOMER(1, "下单成功通知客户"),
    ORDER_PLACED_TO_EMPLOYEE(2, "下单成功通知员工"),
    ITEMS_SHIPPED(3, "发货后通知用户"),
    RETURN_ITEMS_SHIPPED(4, "退货到达通知"),
    RETURN_REQUEST_SUBMITTED(5,  "退款申请提交通知"),
    RETURN_REQUEST_APPROVED(6,  "退款申请通过通知"),

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