package com.cshy.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.model.entity.log.StoreProductLog;
import com.cshy.service.dao.StoreProductLogDao;
import com.cshy.service.service.StoreProductLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * StoreProductLogServiceImpl 接口实现

 */
@Service
public class StoreProductLogServiceImpl extends ServiceImpl<StoreProductLogDao, StoreProductLog> implements StoreProductLogService {

    @Resource
    private StoreProductLogDao dao;

}

