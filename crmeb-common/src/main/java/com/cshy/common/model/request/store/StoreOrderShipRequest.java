package com.cshy.common.model.request.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class StoreOrderShipRequest {
    @ApiModelProperty(value = "订单号", required = true)
    private String orderId;

    @ApiModelProperty(value = "物流单号", required = true)
    private String trackingNo;

    @ApiModelProperty(value = "0 普通订单 1 礼品卡订单", required = true)
    private Integer type;

    @ApiModelProperty(value = "发货对象", required = true)
    private List<SingleOrderRequest> singleOrderRequestList;

}

