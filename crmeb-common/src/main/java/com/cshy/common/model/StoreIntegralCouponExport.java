package com.cshy.common.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class StoreIntegralCouponExport {
    /**
     * 兑换码
     */
    private String couponCode;

    /**
     * 积分面值
     */
    private Integer integral;

    /**
     * 失效日期
     */
    private String expireTime;


}
