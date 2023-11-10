package com.cshy.service.dao;

import com.cshy.common.model.request.StoreNearRequest;
import com.cshy.common.model.vo.SystemStoreNearVo;
import com.cshy.common.model.entity.system.SystemStore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 门店自提 Mapper 接口

 */
public interface SystemStoreDao extends BaseMapper<SystemStore> {

    List<SystemStoreNearVo> getNearList(StoreNearRequest request);
}

