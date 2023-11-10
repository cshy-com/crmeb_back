package com.cshy.common.model.query.giftCard;

import com.alibaba.excel.annotation.ExcelProperty;
import com.cshy.common.model.Order;
import com.cshy.common.model.entity.base.BaseOrder;
import com.cshy.common.model.entity.giftCard.GiftCard;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApiModel("礼品卡券 - query")
public class GiftCardQuery extends GiftCard implements BaseOrder {
    @ApiModelProperty(value = "idList")
    private List<String> idList;

    @Override
    public List<Order> getOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("create_time", Order.DESC));
        return orders;
    }
}
