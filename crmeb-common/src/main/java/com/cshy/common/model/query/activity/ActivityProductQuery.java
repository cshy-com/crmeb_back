package com.cshy.common.model.query.activity;

import com.cshy.common.model.Order;
import com.cshy.common.model.entity.activity.ActivityProduct;
import com.cshy.common.model.entity.base.BaseOrder;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApiModel("活动商品 - query")
public class ActivityProductQuery extends ActivityProduct implements BaseOrder {
    @Override
    public List<Order> getOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("create_time", Order.DESC));
        return orders;
    }
}
