package com.cshy.common.model.request.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RefundOrderInfoRequest {
    @ApiModelProperty(value = "订单信息id")
    private Integer orderInfoId;

    @ApiModelProperty(value = "退款数量")
    private Integer refundNum;
}
