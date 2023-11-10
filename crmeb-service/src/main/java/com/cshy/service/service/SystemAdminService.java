package com.cshy.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.entity.system.SystemAdmin;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.request.SystemAdminAddRequest;
import com.cshy.common.model.request.SystemAdminRequest;
import com.cshy.common.model.request.SystemAdminUpdateRequest;
import com.cshy.common.model.response.SystemAdminResponse;

import java.util.HashMap;
import java.util.List;

/**
 * SystemAdminService 接口

 */
public interface SystemAdminService extends IService<SystemAdmin> {

    /**
     * 后台管理员列表
     * @param request 请求参数
     * @param pageParamRequest 分页参数
     * @return List
     */
    List<SystemAdminResponse> getList(SystemAdminRequest request, PageParamRequest pageParamRequest);

    /**
     * 新增管理员
     */
    Boolean saveAdmin(SystemAdminAddRequest systemAdminAddRequest);

    /**
     * 更新管理员
     */
    Boolean updateAdmin(SystemAdminUpdateRequest systemAdminRequest);

    /**
     * 修改后台管理员状态
     * @param id 管理员id
     * @param status 状态
     * @return Boolean
     */
    Boolean updateStatus(Integer id, Boolean status);

    /**
     * 根据idList获取Map
     * @param adminIdList id数组
     * @return HashMap
     */
    HashMap<Integer, SystemAdmin> getMapInId(List<Integer> adminIdList);

    /**
     * 修改后台管理员是否接收状态
     * @param id 管理员id
     * @return Boolean
     */
    Boolean updateIsSms(Integer id);

    /**
     * 获取可以接收短信的管理员
     */
    List<SystemAdmin> findIsSmsList();

    /**
     * 管理员详情
     * @param id 管理员id
     * @return SystemAdmin
     */
    SystemAdmin getDetail(Integer id);

    /**
     * 通过用户名获取用户
     * @param username 用户名
     * @return SystemAdmin
     */
    SystemAdmin selectUserByUserName(String username);
}
