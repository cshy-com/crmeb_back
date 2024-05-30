package com.cshy.common.model.vo.wmGoods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class B2CGoods {
    @ApiModelProperty(value = "运费模板ID")
    private int freightTemplateId;

    @ApiModelProperty(value = "城市运费模板ID")
    private String cityFreightTemplateId;

    @ApiModelProperty(value = "配送方式ID列表")
    private List<Integer> deliveryTypeIdList;

    @ApiModelProperty(value = "B2C商品类型")
    private int b2cGoodsType;

}
