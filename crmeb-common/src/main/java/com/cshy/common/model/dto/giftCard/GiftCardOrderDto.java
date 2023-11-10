package com.cshy.common.model.dto.giftCard;

import com.cshy.common.model.entity.giftCard.GiftCardOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("礼品卡订单 - Dto")
public class GiftCardOrderDto extends GiftCardOrder {
    @ApiModelProperty(value = "提货编码")
    private String pickupCode;

    @ApiModelProperty(value = "商品名称")
    private String productName;
}
