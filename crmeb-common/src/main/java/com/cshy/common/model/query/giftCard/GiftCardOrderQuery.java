package com.cshy.common.model.query.giftCard;

import com.cshy.common.model.Order;
import com.cshy.common.model.entity.base.BaseOrder;
import com.cshy.common.model.entity.giftCard.GiftCardOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ApiModel("礼品卡券 - query")
public class GiftCardOrderQuery extends GiftCardOrder implements BaseOrder {
    @ApiModelProperty(value = "订单状态")
    private String status;

    @ApiModelProperty(value = "时间范围")
    private String dateLimit;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;




    @Override
    public List<Order> getOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("create_time", Order.DESC));
        return orders;
    }
}
