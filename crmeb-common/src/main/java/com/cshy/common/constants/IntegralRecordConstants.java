package com.cshy.common.constants;

/**
 * 积分记录常量类

 */
public class IntegralRecordConstants {

    /** 积分类型—增加 */
    public static final Integer INTEGRAL_RECORD_TYPE_ADD = 1;

    /** 积分类型—扣减 */
    public static final Integer INTEGRAL_RECORD_TYPE_SUB = 2;

    /** 积分状态—创建 */
    public static final Integer INTEGRAL_RECORD_STATUS_CREATE = 1;

    /** 积分状态—冻结期 */
    public static final Integer INTEGRAL_RECORD_STATUS_FROZEN = 2;

    /** 积分状态—完成 */
    public static final Integer INTEGRAL_RECORD_STATUS_COMPLETE = 3;

    /** 积分状态—失效（订单退款, 取消） */
    public static final Integer INTEGRAL_RECORD_STATUS_INVALIDATION = 4;

    /** 积分关联类型—订单 */
    public static final String INTEGRAL_RECORD_LINK_TYPE_ORDER = "order";

    /** 积分关联类型—签到 */
    public static final String INTEGRAL_RECORD_LINK_TYPE_SIGN = "sign";

    /** 积分关联类型—系统后台 */
    public static final String INTEGRAL_RECORD_LINK_TYPE_SYSTEM = "system";
    /** 积分关联类型—领券 */
    public static final String INTEGRAL_RECORD_LINK_TYPE_COUPON = "coupon";

    /** 积分标题—用户订单付款成功 */
    public static final String BROKERAGE_RECORD_TITLE_ORDER = "用户订单付款成功";

    /** 积分标题—签到经验奖励 */
    public static final String BROKERAGE_RECORD_TITLE_SIGN = "签到积分奖励";

    /** 积分标题—后台积分操作 */
    public static final String BROKERAGE_RECORD_TITLE_SYSTEM = "后台积分操作";
    /** 积分标题—扫码领取积分 */
    public static final String BROKERAGE_RECORD_TITLE_COUPON = "扫码领取积分";

    /** 积分标题—订单退款 */
    public static final String BROKERAGE_RECORD_TITLE_REFUND = "订单退款";
    /** 积分标题—订单退款 */
    public static final String BROKERAGE_RECORD_TITLE_CANCEL = "订单取消";
}
