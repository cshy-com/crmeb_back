package com.cshy.service.service.impl.store;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.model.entity.product.StoreProductAttrResult;
import com.cshy.service.dao.store.StoreProductAttrResultDao;
import com.cshy.service.service.store.StoreProductAttrResultService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * StoreProductAttrResultService实现类

 */
@Service
public class StoreProductAttrResultServiceImpl extends ServiceImpl<StoreProductAttrResultDao, StoreProductAttrResult>
        implements StoreProductAttrResultService {

    @Resource
    private StoreProductAttrResultDao dao;

    /**
     * 根据商品属性值集合查询
     *
     * @param storeProductAttrResult 查询参数
     * @return 查询结果
     */
    @Override
    public List<StoreProductAttrResult> getByEntity(StoreProductAttrResult storeProductAttrResult) {
        LambdaQueryWrapper<StoreProductAttrResult> lqw = Wrappers.lambdaQuery();
        lqw.setEntity(storeProductAttrResult);
        return dao.selectList(lqw);
    }
}
