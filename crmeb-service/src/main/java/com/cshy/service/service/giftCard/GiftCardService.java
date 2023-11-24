package com.cshy.service.service.giftCard;

import com.cshy.common.model.dto.giftCard.GiftCardDto;
import com.cshy.common.model.entity.base.BaseService;
import com.cshy.common.model.entity.giftCard.GiftCard;
import com.cshy.common.model.query.giftCard.GiftCardQuery;
import com.cshy.common.model.vo.giftCard.GiftCardVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface GiftCardService extends BaseService<GiftCard, GiftCardDto, GiftCardQuery, GiftCardVo> {
    void batchAdd(GiftCardDto dto);

    String viewSecret(String id);

    void export(GiftCardQuery query, HttpServletResponse response) throws IOException;

    Map<String, Object> getInfoByPickupCode(String pickupCode);

    boolean checkSecret(String code, String secret);

    void syncStatus();

    GiftCard getById(String id, Boolean isDel);

    String updateBatch(Map<String, Object> params);
}
