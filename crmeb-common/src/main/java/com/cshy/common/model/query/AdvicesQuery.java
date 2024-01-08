package com.cshy.common.model.query;

import com.cshy.common.model.Order;
import com.cshy.common.model.entity.system.Advices;
import com.cshy.common.model.entity.base.BaseOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AdvicesQuery extends Advices implements BaseOrder {

    @ApiModelProperty("搜索条件")
    private String condition;

    @Override
    public List<Order> getOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("create_time", Order.DESC));
        return orders;
    }
}