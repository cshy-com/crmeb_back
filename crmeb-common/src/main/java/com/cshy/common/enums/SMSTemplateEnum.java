package com.cshy.common.enums;

public enum SMSTemplateEnum {
    VERIFICATION_CODE("SMS_462690015", "code", "验证码"),//验证码
    ORDER_SUCCESSFUL_2_EMPLOYEE("SMS_463604731", "name", "下单成功通知员工"),//下单成功通知员工
    ORDER_SUCCESSFUL_2_CUSTOMER("SMS_463634933", "name", "下单成功通知客户"),//下单成功通知客户
    ORDER_SUCCESSFUL_2_CUSTOMER_Multi_PARAM("SMS_463654735", "name,employeeMobile", "下单成功通知客户"),//下单成功通知客户
    ORDER_SHIPPING("SMS_463664910", "name", "发货后通知用户"),//发货后通知用户
    ORDER_SHIPPING_Multi_PARAM("SMS_463589796", "name,employeeMobile", "发货后通知用户"),//发货后通知用户

    ;

    private String code;
    private String param;

    private String name;

    private SMSTemplateEnum(String code, String param, String name){
        this.code = code;
        this.param = param;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getParam() {
        return param;
    }

    public String getName() {
        return name;
    }
}