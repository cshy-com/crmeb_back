package com.cshy.common.constants;

public class AliSMSTemplateStatus {
    /**
     * 阿里云短信模板列表查询
     */
    public static final String INIT = "AUDIT_STATE_INIT"; //审核中
    public static final String PASS = "AUDIT_STATE_PASS";// 审核通过。
    public static final String NOT_PASS = "AUDIT_STATE_NOT_PASS";//审核未通过，请在返回参数Reason中查看审核未通过原因。
    public static final String STATE_CANCEL = "AUDIT_STATE_CANCEL"; //取消审核。
    public static final String SATE_CANCEL = "AUDIT_SATE_CANCEL";//取消审核。
}
