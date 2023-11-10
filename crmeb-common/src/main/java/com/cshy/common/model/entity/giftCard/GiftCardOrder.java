package com.cshy.common.model.entity.giftCard;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cshy.common.model.entity.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("b_gift_card_order")
@ApiModel(value="GiftCardOrder对象", description="礼品卡订单表")
public class GiftCardOrder extends BaseModel<GiftCardOrder> {
    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "用户Id")
    private Integer userId;

    @ApiModelProperty(value = "收货地址id")
    private Integer addressId;

    @ApiModelProperty(value = "商品Id")
    private Integer productId;

    @ApiModelProperty(value = "订单状态 (0 待发货 1 待收货 2 已收货 3 已完结)")
    private Integer orderStatus;

    @ApiModelProperty(value = "礼品卡类型id")
    private String giftCardTypeId;

    @ApiModelProperty(value = "礼品卡id")
    private String giftCardId;

    @ApiModelProperty(value = "物流单号")
    private String trackingNo;

    @ApiModelProperty(value = "短连接")
    private String shortenUrl;

    @ApiModelProperty(value = "商品规格属性id")
    private String attrValueId;

    @ApiModelProperty(value = "备注")
    private String remark;
}
