package com.cshy.common.model.vo.wmGoods;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GoodsInfo {
    @ApiModelProperty(value = "B2C商品信息")
    private B2CGoods b2cGoods;

    @ApiModelProperty(value = "商品业务类型")
    private int goodsBizType;

    @ApiModelProperty(value = "商品分类ID")
    private int categoryId;

    @ApiModelProperty(value = "商品标题")
    private String title;

    @ApiModelProperty(value = "积分抵扣比例")
    private int pointDeductRatio;

    @ApiModelProperty(value = "积分抵扣规则")
    private PointDeductRule pointDeductRule;

    @ApiModelProperty(value = "外部商品编码")
    private String outerGoodsCode;

    @ApiModelProperty(value = "是否多规格")
    private int isMultiSku;

    @ApiModelProperty(value = "商品标签ID")
    private String goodsTagId;

    @ApiModelProperty(value = "商品描述")
    private String goodsDesc;

    @ApiModelProperty(value = "扣减库存类型")
    private int deductStockType;

    @ApiModelProperty(value = "是否上架")
    private int isPutAway;

    @ApiModelProperty(value = "上架时间")
    private long startPutAwayTime;

    @ApiModelProperty(value = "商品模板ID")
    private int goodsTemplateId;

    @ApiModelProperty(value = "是否会员折扣")
    private int isMemberShipDiscount;

    @ApiModelProperty(value = "分类名称树")
    private String categoryNameTree;

    @ApiModelProperty(value = "SKU列表")
    private List<SkuItem> skuList;

    @ApiModelProperty(value = "品牌ID")
    private String brandId;

    @ApiModelProperty(value = "发票抬头")
    private String invoiceTitle;

    @ApiModelProperty(value = "销售单位ID")
    private String sellUnitId;

    @ApiModelProperty(value = "发票规则ID")
    private String invoiceRuleId;

    @ApiModelProperty(value = "商品限购开关")
    private int goodsLimitSwitch;

    @ApiModelProperty(value = "商品限购信息")
    private GoodsLimit goodsLimitVo;

    @ApiModelProperty(value = "商品图片URL")
    private List<String> goodsImageUrl;

}