package com.cshy.service.dao.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cshy.common.model.entity.system.SystemMenu;

import java.util.List;

public interface SystemMenuDao extends BaseMapper<SystemMenu> {

    /**
     * 通过角色数据获取权限
     * @param userId 用户id
     * @return List
     */
    List<SystemMenu> findPermissionByUserId(Integer userId);

    /**
     * 获取用户路由
     * @param userId 用户id
     * @return List
     */
    List<SystemMenu> getMenusByUserId(Integer userId);
}
