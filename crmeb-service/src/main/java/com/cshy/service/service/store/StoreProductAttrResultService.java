package com.cshy.service.service.store;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.entity.product.StoreProductAttrResult;

import java.util.List;

/**
 * StoreProductAttrResultService 接口

 */
public interface StoreProductAttrResultService extends IService<StoreProductAttrResult> {

    /**
     * 根据商品属性值集合查询
     * @param storeProductAttrResult 查询参数
     * @return  查询结果
     */
    List<StoreProductAttrResult> getByEntity(StoreProductAttrResult storeProductAttrResult);
}
