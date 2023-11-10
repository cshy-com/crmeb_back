package com.cshy.service.service.giftCard;

import com.cshy.common.model.dto.giftCard.GiftCardProductDto;
import com.cshy.common.model.entity.base.BaseService;
import com.cshy.common.model.entity.giftCard.GiftCardProduct;
import com.cshy.common.model.query.giftCard.GiftCardProductQuery;
import com.cshy.common.model.vo.giftCard.GiftCardProductVo;

import java.util.List;

public interface GiftCardProductService extends BaseService<GiftCardProduct, GiftCardProductDto, GiftCardProductQuery, GiftCardProductVo> {
    void batchAdd(List<Integer> dtoList, String giftCardTypeId);

    void batchDeleteByIds(List<String> idList);
}
