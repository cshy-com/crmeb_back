package com.cshy.common.model.query.system;

import com.cshy.common.model.Order;
import com.cshy.common.model.entity.base.BaseOrder;
import com.cshy.common.model.entity.system.SysFaq;

import java.util.ArrayList;
import java.util.List;

public class SysFaqQuery extends SysFaq implements BaseOrder {
    @Override
    public List<Order> getOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("sort", Order.ASC));
        orders.add(new Order("create_time", Order.DESC));
        return orders;
    }
}
