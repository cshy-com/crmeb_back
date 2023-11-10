package com.cshy.common.model.dto.giftCard;

import com.cshy.common.model.entity.giftCard.GiftCard;
import com.cshy.common.model.entity.giftCard.GiftCardProduct;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("礼品卡-商品 - Dto")
public class GiftCardProductDto extends GiftCardProduct {
}
