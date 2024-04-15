package com.cshy.common.model.vo.ydd;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class YddProductDetailVo {
    @ApiModelProperty(value = "商品ID")
    private int id;

    @ApiModelProperty(value = "创建时间")
    private long createTime;

    @ApiModelProperty(value = "更新时间")
    private long updateTime;

    @ApiModelProperty(value = "应用ID")
    private int appId;

    @ApiModelProperty(value = "收藏ID")
    private Integer collectId;

    @ApiModelProperty(value = "商品编号")
    private String itemSn;

    @ApiModelProperty(value = "商品代码")
    private String itemCode;

    @ApiModelProperty(value = "商品条形码")
    private String itemBarCode;

    @ApiModelProperty(value = "商品名称")
    private String itemName;

    @ApiModelProperty(value = "一级分类ID")
    private int firstClassId;

    @ApiModelProperty(value = "二级分类ID")
    private int secondClassId;

    @ApiModelProperty(value = "三级分类ID")
    private int thirdClassId;

    @ApiModelProperty(value = "品牌ID")
    private Integer brandId;

    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    @ApiModelProperty(value = "一级分类名称")
    private String firstClassName;

    @ApiModelProperty(value = "二级分类名称")
    private String secondClassName;

    @ApiModelProperty(value = "三级分类名称")
    private String thirdClassName;

    @ApiModelProperty(value = "代理运费")
    private String proxyFreight;

    @ApiModelProperty(value = "是否已下架")
    private boolean down;

    @ApiModelProperty(value = "商品描述")
    private String itemDesc;

    @ApiModelProperty(value = "库存数量")
    private int stock;

    @ApiModelProperty(value = "商品级别")
    private String level;

    @ApiModelProperty(value = "销售数量")
    private int salesNum;

    @ApiModelProperty(value = "商品图片地址")
    private String img;

    @ApiModelProperty(value = "购物车ID")
    private Integer cartId;

    @ApiModelProperty(value = "购物车数量")
    private Integer cartNum;

    @ApiModelProperty(value = "SKUVO")
    private String skuVO;

    @ApiModelProperty(value = "SKU信息列表")
    private List<String> skuInfoList;

    @ApiModelProperty(value = "轮播图和视频列表")
    private List<BannerAndVideo> bannerAndVideoList;

    @ApiModelProperty(value = "关键词")
    private String keywords;

    @ApiModelProperty(value = "零售价格")
    private String retailPrice;

    @ApiModelProperty(value = "商品参数")
    private String itemParam;

    @ApiModelProperty(value = "商品内容")
    private String contents;

    @ApiModelProperty(value = "SKU列表")
    private List<String> skuList;

    @ApiModelProperty(value = "产地")
    private String originPlace;

    @ApiModelProperty(value = "包装尺寸")
    private String packSize;

    @ApiModelProperty(value = "材质")
    private String material;

    @ApiModelProperty(value = "净重")
    private String netWeight;

    @ApiModelProperty(value = "毛重")
    private String grossWeight;

    @ApiModelProperty(value = "箱子尺寸")
    private String boxSize;

    @ApiModelProperty(value = "箱子数量")
    private String boxNumber;

    @ApiModelProperty(value = "包装清单")
    private String packList;

    @ApiModelProperty(value = "属性")
    private String property;

    @ApiModelProperty(value = "京东链接")
    private String jdLink;

    @ApiModelProperty(value = "苏宁链接")
    private String snLink;

    @ApiModelProperty(value = "天猫链接")
    private String tmLink;

    @ApiModelProperty(value = "商品年份")
    private String itemYear;

    @ApiModelProperty(value = "商品尺寸")
    private String itemSize;

    @ApiModelProperty(value = "总重量")
    private String fullWeight;

    @ApiModelProperty(value = "包装")
    private String packing;

    @ApiModelProperty(value = "SKU价格")
    private String skuPrice;

    @ApiModelProperty(value = "上架状态")
    private Integer shelfStatus;

}

