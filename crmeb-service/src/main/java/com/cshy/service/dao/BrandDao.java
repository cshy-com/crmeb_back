package com.cshy.service.dao;

import com.cshy.common.model.entity.base.BaseMapper;
import com.cshy.common.model.entity.system.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.io.Serializable;

@Mapper
public interface BrandDao extends BaseMapper<Brand> {

    @Insert("INSERT INTO s_brand " +
            "(id, brand_name, icon, status, create_time, update_time, is_del) " +
            "VALUES " +
            "(#{id}, #{brandName}, #{icon}, #{status}, NOW(), NOW(), #{isDel})")
    int insertWithCustomId(Brand brand);

    @Delete("DELETE FROM s_brand where id = #{id}")
    int delete(Serializable id);
}
