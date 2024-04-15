package com.cshy.service.dao.category;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cshy.common.model.entity.category.Category;
import org.apache.ibatis.annotations.Insert;

/**
 * 分类表 Mapper 接口
 
 */
public interface CategoryDao extends BaseMapper<Category> {
    @Insert("INSERT INTO sys_category (id, pid, path, name, type, url, extra, status, sort, create_time, update_time) " +
            "VALUES (#{id}, #{pid}, #{path}, #{name}, #{type}, #{url}, #{extra}, #{status}, #{sort}, NOW(), NOW())")
    int insertWithCustomId(Category category);
}
