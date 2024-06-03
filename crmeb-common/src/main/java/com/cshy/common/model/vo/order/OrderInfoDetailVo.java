package com.cshy.common.model.vo.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单详情Vo对象

 */
@Data
public class OrderInfoDetailVo {

    /** 商品id */
    private Integer productId;

    /** 商品名称 */
    private String productName;

    /** 规格属性id */
    private Integer attrValueId;

    /** 商品图片 */
    private String mainImage;

    /** 规格图片 */
    private String attrValueImage;

    /** sku */
    private String sku;

    /** 单价 */
    private BigDecimal price;

    /** 购买数量 */
    private Integer payNum;
    /** 发货数量 */
    private Integer shipNum;

    /** 重量 */
    private BigDecimal weight;

    /** 体积 */
    private BigDecimal volume;

    /** 运费模板ID */
    private Integer tempId;

    /** 获得积分 */
    private BigDecimal giveIntegral;

    /** 是否评价 */
    private Integer isReply;

    /** 是否单独分佣 */
    private Boolean isSub;

    /** 会员价 */
    private BigDecimal vipPrice;

    /** 商品类型:0-普通，1-秒杀，2-砍价，3-拼团，4-视频号 */
    private Integer productType;

    /** 是否到店自提 */
    private Boolean isPickup;

    /** 是否商家配送 */
    private Boolean isDeliver;
}
