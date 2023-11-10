package com.cshy.service.dao;

import com.cshy.common.model.entity.express.ShippingTemplatesFree;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cshy.common.model.request.ShippingTemplatesFreeRequest;

import java.util.List;

/**
 *  Mapper 接口

 */
public interface ShippingTemplatesFreeDao extends BaseMapper<ShippingTemplatesFree> {

    List<ShippingTemplatesFreeRequest> getListGroup(Integer tempId);
}
