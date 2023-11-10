package com.cshy.service.service.impl.giftCard;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cshy.common.model.dto.giftCard.GiftCardTypeDto;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.entity.giftCard.GiftCardProduct;
import com.cshy.common.model.entity.giftCard.GiftCardType;
import com.cshy.common.model.query.giftCard.GiftCardTypeQuery;
import com.cshy.common.model.vo.giftCard.GiftCardTypeVo;
import com.cshy.service.dao.giftCard.GiftCardTypeDao;
import com.cshy.service.service.giftCard.GiftCardProductService;
import com.cshy.service.service.giftCard.GiftCardTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GiftCardTypeServiceImpl extends BaseServiceImpl<GiftCardType, GiftCardTypeDto,
        GiftCardTypeQuery, GiftCardTypeVo, GiftCardTypeDao> implements GiftCardTypeService {
    private static final Logger logger = LoggerFactory.getLogger(GiftCardTypeServiceImpl.class);

    @Resource
    private GiftCardProductService giftCardProductService;

    @Override
    protected void onBeforeAdd(GiftCardTypeDto dto) {
        if (Objects.isNull(dto.getStatus()))
            dto.setStatus(0);
        super.onBeforeAdd(dto);
    }

    @Override
    protected void onAfterPage(Page<GiftCardTypeVo> page) {
        page.getRecords().forEach(giftCardTypeVo -> {
            List<GiftCardProduct> giftCardProductList = giftCardProductService.list(new LambdaQueryWrapper<GiftCardProduct>().eq(GiftCardProduct::getGiftCardTypeId, giftCardTypeVo.getId()));
            List<Integer> idList = giftCardProductList.stream().map(GiftCardProduct::getProductId).collect(Collectors.toList());
            giftCardTypeVo.setProductIdList(idList);
        });
        super.onAfterPage(page);
    }
}
