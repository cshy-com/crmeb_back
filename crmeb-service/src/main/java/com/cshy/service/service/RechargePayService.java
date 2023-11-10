package com.cshy.service.service;

import com.cshy.common.model.entity.finance.UserRecharge;

/**
 * 订单支付

 */
public interface RechargePayService {

    /**
     * 支付成功处理
     * @param userRecharge 充值订单
     */
    Boolean paySuccess(UserRecharge userRecharge);
}
