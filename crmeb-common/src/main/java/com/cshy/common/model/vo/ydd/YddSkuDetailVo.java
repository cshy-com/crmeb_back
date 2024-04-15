package com.cshy.common.model.vo.ydd;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class YddSkuDetailVo {
    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

    @ApiModelProperty(value = "产品ID")
    private int productId;

    @ApiModelProperty(value = "SKU键")
    private String skuKey;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "图片")
    private String img;

    @ApiModelProperty(value = "最终价格")
    private Integer finalPrice;

    @ApiModelProperty(value = "SKU成本价")
    private String skuCostPrice;

    @ApiModelProperty(value = "SKU促销价格")
    private Integer skuPromotionalPrice;

    @ApiModelProperty(value = "ERP商品代码")
    private String erpItemCode;

    @ApiModelProperty(value = "SKU价格")
    private String skuPrice;

    @ApiModelProperty(value = "SKU代码")
    private String skuCode;

    @ApiModelProperty(value = "SKU价格列表")
    private String skuPriceList;
}
