package com.cshy.service.service.store;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.entity.order.StoreOrder;
import com.cshy.common.model.request.store.StoreOrderRefundRequest;


/**
 * StoreOrderRefundService 接口

 */
public interface StoreOrderRefundService extends IService<StoreOrder> {

    void refund(StoreOrderRefundRequest request, StoreOrder storeOrder);
}
