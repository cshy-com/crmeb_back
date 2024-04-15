package com.cshy.service.impl.activitiy;

import com.cshy.common.model.dto.activity.ActivityProductDto;
import com.cshy.common.model.entity.activity.ActivityProduct;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.query.activity.ActivityProductQuery;
import com.cshy.common.model.vo.activity.ActivityProductVo;
import com.cshy.service.dao.activity.ActivityProductDao;
import com.cshy.service.service.activity.ActivityProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ActivityProductServiceImpl extends BaseServiceImpl<ActivityProduct, ActivityProductDto,
        ActivityProductQuery, ActivityProductVo, ActivityProductDao> implements ActivityProductService {
    private static final Logger logger = LoggerFactory.getLogger(ActivityProductServiceImpl.class);


}
