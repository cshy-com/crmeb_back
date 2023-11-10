package com.cshy.service.dao;

import com.cshy.common.model.entity.express.ShippingTemplatesRegion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cshy.common.model.request.ShippingTemplatesRegionRequest;

import java.util.List;

/**
 *  Mapper 接口
 
 */
public interface ShippingTemplatesRegionDao extends BaseMapper<ShippingTemplatesRegion> {

    List<ShippingTemplatesRegionRequest> getListGroup(Integer tempId);
}
