package com.cshy.common.model.vo.ydd;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class YddProductVo {
    @ApiModelProperty(value = "商品ID")
    private String id;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "主图片URL")
    private String mainImg;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "商品类型")
    private String type;

    @ApiModelProperty(value = "商品ID")
    private String goodsId;

    @ApiModelProperty(value = "供应商备注")
    private String suppRemark;

    @ApiModelProperty(value = "是否启用")
    private Integer isEnable;

    @ApiModelProperty(value = "成本价格")
    private String costPrice;

    @ApiModelProperty(value = "市场价格")
    private String marketPrice;

    @ApiModelProperty(value = "销售数量")
    private Integer sellingCount;

    @ApiModelProperty(value = "总利润率")
    private String totalProfitRate;

    @ApiModelProperty(value = "一级类目ID")
    private Integer i1CategoryId;

    @ApiModelProperty(value = "二级类目ID")
    private Integer i2CategoryId;

    @ApiModelProperty(value = "三级类目ID")
    private Integer i3CategoryId;

    @ApiModelProperty(value = "供应商ID")
    private Integer supplierId;

    @ApiModelProperty(value = "SKU编号")
    private String numSku;

    @ApiModelProperty(value = "一级规格项ID")
    private Integer i1SpecItemId;

    @ApiModelProperty(value = "二级规格项ID")
    private Integer i2SpecItemId;

    @ApiModelProperty(value = "三级规格项ID")
    private Integer i3SpecItemId;

    @ApiModelProperty(value = "一级规格名称")
    private String item1Name;

    @ApiModelProperty(value = "二级规格名称")
    private String item2Name;

    @ApiModelProperty(value = "三级规格名称")
    private String item3Name;

    @ApiModelProperty(value = "按钮点数")
    private String buttonPoint;

    @ApiModelProperty(value = "审核链接")
    private String auditLink;

    @ApiModelProperty(value = "审核截图")
    private String auditScreenshot;

    @ApiModelProperty(value = "电商代码")
    private String ecommerceCode;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    @ApiModelProperty(value = "规格名称")
    private String specName;

    @ApiModelProperty(value = "类目名称")
    private String categoryName;
}
