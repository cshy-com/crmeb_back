package com.cshy.common.model.vo.ydd;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class YddProductVo {
    @ApiModelProperty(value = "品牌id")
    private String brandId;
    @ApiModelProperty(value = "品牌名称")
    private String brandName;
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "图片")
    private String img;
    @ApiModelProperty(value = "商品编码")
    private String itemCode;
    @ApiModelProperty(value = "商品名称")
    private String itemName;
    @ApiModelProperty(value = "商品型号")
    private String itemSn;
    @ApiModelProperty(value = "采购价")
    private String retailPrice;
    @ApiModelProperty(value = "上架状态")
    private String shelfStatus;
    @ApiModelProperty(value = "库存")
    private String stock;
    @ApiModelProperty(value = "云搭档商品池")
    private String yddItemPool;
}
