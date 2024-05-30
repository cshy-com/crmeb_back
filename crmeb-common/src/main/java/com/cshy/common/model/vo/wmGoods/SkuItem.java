package com.cshy.common.model.vo.wmGoods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SkuItem {
    @ApiModelProperty(value = "外部SKU编码")
    private String outerSkuCode;

    @ApiModelProperty(value = "单品编码")
    private String singleProductCode;

    @ApiModelProperty(value = "销售价")
    private int salePrice;

    @ApiModelProperty(value = "成本价")
    private String costPrice;

    @ApiModelProperty(value = "原价")
    private String originalPrice;

    @ApiModelProperty(value = "B2C SKU信息")
    private B2CSku b2cSku;

    @ApiModelProperty(value = "是否禁用")
    private boolean isDisabled;

    @ApiModelProperty(value = "编辑库存数")
    private int editStockNum;

}

