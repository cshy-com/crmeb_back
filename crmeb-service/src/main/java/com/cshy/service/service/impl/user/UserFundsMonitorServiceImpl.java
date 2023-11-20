package com.cshy.service.service.impl.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.constants.BrokerageRecordConstants;
import com.cshy.common.model.vo.user.UserFundsMonitor;
import com.github.pagehelper.PageInfo;
import com.cshy.common.model.request.BrokerageRecordRequest;
import com.cshy.common.model.entity.user.User;
import com.cshy.common.model.entity.user.UserBrokerageRecord;
import com.cshy.service.dao.user.UserFundsMonitorDao;
import com.cshy.service.service.user.UserBrokerageRecordService;
import com.cshy.service.service.user.UserFundsMonitorService;
import com.cshy.service.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
* UserRechargeServiceImpl 接口实现

*/
@Service
public class UserFundsMonitorServiceImpl extends ServiceImpl<UserFundsMonitorDao, UserFundsMonitor> implements UserFundsMonitorService {

    @Resource
    private UserFundsMonitorDao dao;

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    @Autowired
    private UserService userService;

    /**
     * 佣金记录
     * @param request 筛选条件
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserBrokerageRecord> getBrokerageRecord(BrokerageRecordRequest request, PageParamRequest pageParamRequest) {
        PageInfo<UserBrokerageRecord> pageInfo = userBrokerageRecordService.getAdminList(request, pageParamRequest);
        List<UserBrokerageRecord> list = pageInfo.getList();
        if (CollUtil.isEmpty(list)) {
            pageInfo.setList(list);
            return pageInfo;
        }
        List<Integer> uidList = list.stream().map(e -> e.getUid()).distinct().collect(Collectors.toList());
        HashMap<Integer, User> userMap = userService.getMapListInUid(uidList);
        list.forEach(e -> {
            if (e.getLinkType().equals(BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_WITHDRAW)
                    && e.getStatus().equals(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE)
                    && e.getType().equals(BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_SUB)) {
                e.setTitle("提现成功");
            }
            String name = "-";
            if(ObjectUtil.isNotNull(userMap.get(e.getUid()))){
                name = userMap.get(e.getUid()).getNickname();
            }
            e.setUserName(name);
        });
        pageInfo.setList(list);
        return pageInfo;
    }


}
