package com.cshy.service.impl.category;

import com.cshy.common.model.dto.category.ActivityCategoryDto;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.entity.category.ActivityCategory;
import com.cshy.common.model.query.category.ActivityCategoryQuery;
import com.cshy.common.model.vo.category.ActivityCategoryVo;
import com.cshy.service.dao.category.ActivityCategoryDao;
import com.cshy.service.service.category.ActivityCategoryService;
import org.springframework.stereotype.Service;

@Service
public class ActivityCategoryServiceImpl extends BaseServiceImpl<ActivityCategory, ActivityCategoryDto,
        ActivityCategoryQuery, ActivityCategoryVo, ActivityCategoryDao> implements ActivityCategoryService {
}
