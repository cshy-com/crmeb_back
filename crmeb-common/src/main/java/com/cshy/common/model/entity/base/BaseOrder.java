package com.cshy.common.model.entity.base;


import com.cshy.common.model.Order;

import java.util.List;

/**
 * 排序接口
 */
public interface BaseOrder {

    /**
     * 获取默认排序规则
     */
    List<Order> getOrders();
}
