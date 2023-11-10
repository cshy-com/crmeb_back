package com.cshy.common.model.entity.giftCard;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cshy.common.model.entity.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("b_gift_card_product")
@ApiModel(value="GiftCardProduct对象", description="礼品卡-商品表")
public class GiftCardProduct extends BaseModel<GiftCardProduct> {
    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "卡券类型id")
    private String giftCardTypeId;
}
