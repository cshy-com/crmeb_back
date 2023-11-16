package com.cshy.service.service.impl.giftCard;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cshy.common.model.dto.giftCard.GiftCardProductDto;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.entity.giftCard.GiftCardProduct;
import com.cshy.common.model.query.giftCard.GiftCardProductQuery;
import com.cshy.common.model.vo.giftCard.GiftCardProductVo;
import com.cshy.service.dao.giftCard.GiftCardProductDao;
import com.cshy.service.service.giftCard.GiftCardProductService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class GiftCardProductServiceImpl extends BaseServiceImpl<GiftCardProduct, GiftCardProductDto,
        GiftCardProductQuery, GiftCardProductVo, GiftCardProductDao> implements GiftCardProductService {
    private static final Logger logger = LoggerFactory.getLogger(GiftCardProductServiceImpl.class);

    @Resource
    private GiftCardProductDao giftCardProductDao;


    @Override
    public void batchAdd(List<Integer> idList, String giftCardTypeId) {
        //查询原关联商品id
        List<GiftCardProduct> list = this.list(new LambdaQueryWrapper<GiftCardProduct>().eq(GiftCardProduct::getGiftCardTypeId, giftCardTypeId));
        List<Integer> originalIdList = list.stream().map(GiftCardProduct::getProductId).collect(Collectors.toList());

        //查找需要删除的id
        List<Integer> delList = Lists.newArrayList();
        this.findIdList(originalIdList, idList, delList);

        //查找需要添加的id
        List<Integer> updateList = Lists.newArrayList();
        this.findIdList(idList, originalIdList, updateList);

        //删除数据
        if (CollUtil.isNotEmpty(delList))
            this.remove(new LambdaQueryWrapper<GiftCardProduct>().eq(GiftCardProduct::getGiftCardTypeId, giftCardTypeId).in(GiftCardProduct::getProductId, delList));

        updateList.forEach(id -> {
            GiftCardProductDto giftCardProductDto = new GiftCardProductDto();
            giftCardProductDto.setProductId(id);
            giftCardProductDto.setGiftCardTypeId(giftCardTypeId);
            this.add(giftCardProductDto);
        });
    }

    @Override
    public void batchDeleteByIds(List<String> idList) {
        this.giftCardProductDao.batchDeleteByIds(idList);
    }

    @Override
    public void restore(Integer id) {
        this.giftCardProductDao.restore(id);
    }

    private void findIdList(List<Integer> oldList, List<Integer> newList, List<Integer> addList) {
        oldList.forEach(id1 -> {
            AtomicBoolean isAdd = new AtomicBoolean(true);
            newList.forEach(id2 -> {
                if (id1.equals(id2)) {
                    isAdd.set(false);
                }
            });
            if (isAdd.get()) {
                addList.add(id1);
            }
        });
    }

}
