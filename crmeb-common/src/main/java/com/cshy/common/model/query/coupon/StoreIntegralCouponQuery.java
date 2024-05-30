package com.cshy.common.model.query.coupon;

import com.cshy.common.model.Order;
import com.cshy.common.model.entity.base.BaseOrder;
import com.cshy.common.model.entity.coupon.StoreIntegralCoupon;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StoreIntegralCouponQuery extends StoreIntegralCoupon implements BaseOrder {
    @ApiModelProperty(value = "idList")
    private List<String> idList;

    @Override
    public List<Order> getOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("create_time", Order.DESC));
        return orders;
    }
}
