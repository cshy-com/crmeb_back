package com.cshy.service.service.category;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.entity.category.Category;
import com.cshy.common.model.request.category.CategoryRequest;
import com.cshy.common.model.request.category.CategorySearchRequest;
import com.cshy.common.model.vo.category.CategoryTreeVo;

import java.util.HashMap;
import java.util.List;

/**
*   CategoryService 接口

*/
public interface CategoryService extends IService<Category> {
    
    List<Category> getList(CategorySearchRequest request, PageParamRequest pageParamRequest);

    int delete(Integer id);

    void load2Cache();

    void load2CacheByType(Integer type);

    /**
     * 获取树形结构数据
     *
     * @param type     分类
     * @param status   状态
     * @param name     名称
     * @return List
     */
    List<CategoryTreeVo> getListTree(Integer type, Integer status, String name);

    /**
     * 获取树形结构数据
     * @param type 分类
     * @param status 状态
     * @param categoryIdList 分类idList
     * @return List
     */
    List<CategoryTreeVo> getListTree(Integer type, Integer status, List<Integer> categoryIdList);

    List<Category> getByIds(List<Integer> ids);

    HashMap<Integer, String> getListInId(List<Integer> cateIdList);

    boolean update(CategoryRequest request, Integer id);

    List<Category> getChildVoListByPid(Integer pid);

    boolean updateStatus(Integer id);

    /**
     * 新增分类表
     */
    Boolean create(CategoryRequest categoryRequest);

    /**
     * 获取文章分类列表
     * @return List<Category>
     */
    List<Category> findArticleCategoryList();
}
