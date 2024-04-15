package com.cshy.service.impl.store;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.model.entity.product.StoreProductDescription;
import com.cshy.service.dao.store.StoreProductDescriptionDao;
import com.cshy.service.service.store.StoreProductDescriptionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * StoreProductDescriptionServiceImpl 接口实现

 */
@Service
public class StoreProductDescriptionServiceImpl extends ServiceImpl<StoreProductDescriptionDao, StoreProductDescription> implements StoreProductDescriptionService {

    @Resource
    private StoreProductDescriptionDao dao;

    /**
     * 根据商品id和type删除对应描述
     * @param productId 商品id
     * @param type      类型
     */
    @Override
    public void deleteByProductId(int productId,int type) {
        LambdaQueryWrapper<StoreProductDescription> lmq = Wrappers.lambdaQuery();
        lmq.eq(StoreProductDescription::getProductId, productId).eq(StoreProductDescription::getType,type);
        dao.delete(lmq);
    }

    /**
     * 获取详情
     * @param productId 商品id
     * @param type 商品类型
     * @return StoreProductDescription
     */
    @Override
    public StoreProductDescription getByProductIdAndType(Integer productId, Integer type) {
        LambdaQueryWrapper<StoreProductDescription> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreProductDescription::getProductId, productId);
        lqw.eq(StoreProductDescription::getType,type);
        return dao.selectOne(lqw);
    }
}

