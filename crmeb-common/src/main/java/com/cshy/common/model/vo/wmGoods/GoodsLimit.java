package com.cshy.common.model.vo.wmGoods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GoodsLimit {
    @ApiModelProperty(value = "商品限购数量")
    private String goodsLimitNum;

    @ApiModelProperty(value = "商品限购周期")
    private String goodsLimitCycle;

}