package com.cshy.common.model.entity.base;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.page.CommonPage;

import java.util.List;

/**
 * service 基类
 */
public interface BaseService<T extends BaseModel<T>, D extends T, Q extends T, V extends T> extends IService<T> {

    /**
     * 新增
     *
     * @param dto 实体入参
     * @return
     */
    String add(D dto);

    /**
     * 修改
     *
     * @param dto 实体入参
     */
    void update(D dto);

    /**
     * 删除
     *
     * @param id 对象 id
     */
    void delete(String id);

    /**
     * 查询单个对象
     *
     * @param id 对象 id
     * @return 查询对象
     */
    V obj(String id);

    /**
     * 查询单个对象
     *
     * @param t 对象
     * @return 查询对象
     */
    V obj(T t);

    /**
     * 列表查询
     *
     * @param query 查询对象
     * @return 结果列表
     */
    List<V> list(Q query);

    /**
     * 列表查询
     *
     * @param query    查询对象
     * @param basePage 分页对象
     * @return 结果列表
     */
    CommonPage<V> page(Q query, BasePage basePage);
}
