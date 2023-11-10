package com.cshy.service.service.giftCard;

import com.cshy.common.model.dto.giftCard.GiftCardOrderDto;
import com.cshy.common.model.entity.base.BaseService;
import com.cshy.common.model.entity.giftCard.GiftCardOrder;
import com.cshy.common.model.query.giftCard.GiftCardOrderQuery;
import com.cshy.common.model.vo.giftCard.GiftCardOrderVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface GiftCardOrderService extends BaseService<GiftCardOrder, GiftCardOrderDto, GiftCardOrderQuery, GiftCardOrderVo> {
    Map<String, Object> getOrderStatusNum();

    void ship(String orderId, String trackingNo, Integer type, HttpServletRequest request);

    String addOrder(GiftCardOrderDto dto, HttpServletRequest request);
}
