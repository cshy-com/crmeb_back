package com.cshy.common.model.vo.giftCard;

import com.cshy.common.model.entity.giftCard.GiftCardType;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel("礼品卡类型 - Vo")
public class GiftCardTypeVo extends GiftCardType {
    List<Integer> productIdList;
}
