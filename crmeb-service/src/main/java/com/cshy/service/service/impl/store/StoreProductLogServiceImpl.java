package com.cshy.service.service.impl.store;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.model.entity.log.StoreProductLog;
import com.cshy.service.dao.store.StoreProductLogDao;
import com.cshy.service.service.store.StoreProductLogService;
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

