package com.cshy.service.service.store;


import com.cshy.common.model.request.store.StoreOrderStaticsticsRequest;
import com.cshy.common.model.response.StoreOrderVerificationConfirmResponse;
import com.cshy.common.model.response.StoreStaffDetail;
import com.cshy.common.model.response.StoreStaffTopDetail;

import java.util.List;

/**
 * 订单核销业务

 */
public interface StoreOrderVerification {
    /**
     * 获取订单核销数据
     */
    StoreStaffTopDetail getOrderVerificationData();

    /**
     * 核销月详情
     * @return 月详情
     */
    List<StoreStaffDetail> getOrderVerificationDetail(StoreOrderStaticsticsRequest request);

    /**
     * 根据核销码核销订单
     * @param vCode 核销码
     * @return 核销结果
     */
    boolean verificationOrderByCode(String vCode);

    /**
     * 根据核销码查询待核销订单
     * @param vCode 核销码
     * @return 待核销订单详情
     */
    StoreOrderVerificationConfirmResponse getVerificationOrderByCode(String vCode);
}
