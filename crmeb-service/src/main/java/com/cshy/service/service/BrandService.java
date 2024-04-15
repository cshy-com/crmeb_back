package com.cshy.service.service;

import com.cshy.common.model.dto.BrandDto;
import com.cshy.common.model.entity.base.BaseService;
import com.cshy.common.model.entity.system.Brand;
import com.cshy.common.model.query.BrandQuery;
import com.cshy.common.model.vo.BrandVo;

import java.io.Serializable;

public interface BrandService extends BaseService<Brand, BrandDto, BrandQuery, BrandVo> {
    void insertWithCustomId(Brand brand);

    void deleteById(Serializable id);
}
