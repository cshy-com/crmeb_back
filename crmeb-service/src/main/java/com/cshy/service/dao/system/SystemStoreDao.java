package com.cshy.service.dao.system;

import com.cshy.common.model.request.store.StoreNearRequest;
import com.cshy.common.model.vo.system.SystemStoreNearVo;
import com.cshy.common.model.entity.system.SystemStore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 门店自提 Mapper 接口

 */
public interface SystemStoreDao extends BaseMapper<SystemStore> {

    List<SystemStoreNearVo> getNearList(StoreNearRequest request);
}

