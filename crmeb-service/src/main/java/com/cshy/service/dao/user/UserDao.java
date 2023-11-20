package com.cshy.service.dao.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cshy.common.model.entity.user.User;
import com.cshy.common.model.response.UserSpreadPeopleItemResponse;

import java.util.List;
import java.util.Map;

/**
 * 用户表 Mapper 接口

 */
public interface UserDao extends BaseMapper<User> {

    List<UserSpreadPeopleItemResponse> getSpreadPeopleList(Map<String, Object> map);

    List<User> findAdminList(Map<String, Object> map);
}
