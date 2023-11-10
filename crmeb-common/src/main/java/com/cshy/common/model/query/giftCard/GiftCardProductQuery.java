package com.cshy.common.model.query.giftCard;

import com.cshy.common.model.Order;
import com.cshy.common.model.entity.base.BaseOrder;
import com.cshy.common.model.entity.giftCard.GiftCardProduct;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApiModel("礼品卡-商品 - query")
public class GiftCardProductQuery extends GiftCardProduct implements BaseOrder {
    @Override
    public List<Order> getOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("create_time", Order.DESC));
        return orders;
    }
}
