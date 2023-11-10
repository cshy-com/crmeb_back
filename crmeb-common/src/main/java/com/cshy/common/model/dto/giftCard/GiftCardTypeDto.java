package com.cshy.common.model.dto.giftCard;

import com.cshy.common.model.entity.giftCard.GiftCard;
import com.cshy.common.model.entity.giftCard.GiftCardType;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("礼品卡类型 - Dto")
public class GiftCardTypeDto extends GiftCardType {
}
