package com.cshy.common.model.query.user;

import com.cshy.common.model.Order;
import com.cshy.common.model.entity.base.BaseOrder;
import com.cshy.common.model.entity.user.UserVisitHistory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApiModel("用户浏览历史 - query")
public class UserVisitHistoryQuery extends UserVisitHistory implements BaseOrder {
    @ApiModelProperty(value = "用户id")
    private String date;
    @Override
    public List<Order> getOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("create_time", Order.DESC));
        return orders;
    }
}
