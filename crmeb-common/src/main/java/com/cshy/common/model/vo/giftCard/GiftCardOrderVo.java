package com.cshy.common.model.vo.giftCard;

import com.cshy.common.model.entity.giftCard.GiftCardOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@ApiModel("礼品卡订单 - Vo")
public class GiftCardOrderVo extends GiftCardOrder {
    @ApiModelProperty(value = "用户地址")
    Map<String, Object> userAddress;

    @ApiModelProperty(value = "用户信息")
    Map<String, Object> user;

    @ApiModelProperty(value = "商品信息")
    Map<String, Object> storeProduct;
    @ApiModelProperty(value = "卡类型名称")
    String giftCardTypeName;

    @ApiModelProperty(value = "规格")
    Map<String, Object> attrValue;

    @ApiModelProperty(value = "礼品卡")
    Map<String, Object> giftCard;
}
