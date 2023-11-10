package com.cshy.common.model.entity.base;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {

    // 批量插入
    void insertBatchSomeColumn(@Param("list") List<T> batchList);
}
