package com.cshy.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cshy.common.model.entity.user.UserBill;
import com.cshy.common.model.response.UserBillResponse;

import java.util.List;
import java.util.Map;

/**
 * 用户账单表 Mapper 接口

 */
public interface UserBillDao extends BaseMapper<UserBill> {

    List<UserBillResponse> fundMonitoring(Map<String, Object> map);
}
