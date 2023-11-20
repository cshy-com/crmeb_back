package com.cshy.service.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.entity.system.SystemStore;
import com.cshy.common.model.request.store.StoreNearRequest;
import com.cshy.common.model.request.system.SystemStoreRequest;
import com.cshy.common.model.response.StoreNearResponse;

import java.util.HashMap;
import java.util.List;

/**
 * SystemStoreService 接口

 */
public interface SystemStoreService extends IService<SystemStore> {

    /**
     * 门店自提分页列表
     * @param keywords 搜索条件
     * @param status 状态
     * @param pageParamRequest 分页参数
     * @return List
     */
    List<SystemStore> getList(String keywords, Integer status, PageParamRequest pageParamRequest);

    /**
     * 根据基本参数获取
     * @param systemStore 基本参数
     * @return 门店自提结果
     */
    SystemStore getByCondition(SystemStore systemStore);

    /**
     * 修改门店显示状态
     * @param id integer id
     * @param status 状态
     */
    Boolean updateStatus(Integer id, Boolean status);

    /**
     * 删除门店自提
     * @param id Integer
     */
    Boolean delete(Integer id);

    /**
     * 表头数量
     * @return HashMap<String, Integer>
     */
    HashMap<String, Integer> getCount();

    HashMap<Integer, SystemStore> getMapInId(List<Integer> storeIdList);

    StoreNearResponse getNearList(StoreNearRequest request, PageParamRequest pageParamRequest);

    /**
     * 新增门店自提
     * @param request SystemStoreRequest 新增参数
     */
    Boolean create(SystemStoreRequest request);

    /**
     * 修改门店自提
     * @param id integer id
     * @param request 修改参数
     */
    Boolean update(Integer id, SystemStoreRequest request);

    /**
     * 彻底删除
     * @param id 提货点编号
     */
    Boolean completeLyDelete(Integer id);

    /**
     * 提货点恢复
     * @param id 提货点编号
     */
    Boolean recovery(Integer id);

    /**
     * 门店自提详情
     * @param id Integer
     * @return SystemStore
     */
    SystemStore getInfo(Integer id);
}
