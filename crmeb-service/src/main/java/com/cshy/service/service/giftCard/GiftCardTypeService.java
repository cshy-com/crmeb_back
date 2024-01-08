package com.cshy.service.service.giftCard;

import com.cshy.common.model.dto.giftCard.GiftCardTypeDto;
import com.cshy.common.model.entity.base.BaseService;
import com.cshy.common.model.entity.giftCard.GiftCardType;
import com.cshy.common.model.query.giftCard.GiftCardTypeQuery;
import com.cshy.common.model.vo.giftCard.GiftCardTypeVo;

public interface GiftCardTypeService extends BaseService<GiftCardType, GiftCardTypeDto, GiftCardTypeQuery, GiftCardTypeVo> {
    GiftCardType getById(String id, Boolean isDel);
}
