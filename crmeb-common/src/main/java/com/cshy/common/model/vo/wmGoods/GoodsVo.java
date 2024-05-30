package com.cshy.common.model.vo.wmGoods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GoodsVo {
    @ApiModelProperty(value = "pid")
    private String pid;

    @ApiModelProperty(value = "店铺ID")
    private String storeId;

    @ApiModelProperty(value = "商品信息")
    private GoodsInfo goods;

}





