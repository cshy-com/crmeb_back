package com.cshy.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.vo.UserFundsMonitor;
import com.github.pagehelper.PageInfo;
import com.cshy.common.model.request.BrokerageRecordRequest;
import com.cshy.common.model.entity.user.UserBrokerageRecord;

/**
*  UserRechargeService 接口

*/
public interface UserFundsMonitorService extends IService<UserFundsMonitor> {

    /**
     * 佣金记录
     * @param request 筛选条件
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserBrokerageRecord> getBrokerageRecord(BrokerageRecordRequest request, PageParamRequest pageParamRequest);
}
