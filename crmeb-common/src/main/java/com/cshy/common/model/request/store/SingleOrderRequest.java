package com.cshy.common.model.request.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SingleOrderRequest {
    @ApiModelProperty(value = "发货数量", required = true)
    private Integer num;

    @ApiModelProperty(value = "商品Id", required = true)
    private Integer productId;

    @ApiModelProperty(value = "商品属性值Id", required = true)
    private Integer attrValueId;
}
