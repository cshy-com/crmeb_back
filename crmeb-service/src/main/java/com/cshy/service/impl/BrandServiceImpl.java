package com.cshy.service.impl;

import com.cshy.common.model.dto.BrandDto;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.entity.system.Brand;
import com.cshy.common.model.query.BrandQuery;
import com.cshy.common.model.vo.BrandVo;
import com.cshy.service.dao.BrandDao;
import com.cshy.service.service.BrandService;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class BrandServiceImpl extends BaseServiceImpl<Brand, BrandDto, BrandQuery, BrandVo, BrandDao> implements BrandService {
    @Override
    public void insertWithCustomId(Brand brand) {
        baseMapper.insertWithCustomId(brand);
    }

    @Override
    public void deleteById(Serializable id) {
        baseMapper.delete(id);
    }
}
