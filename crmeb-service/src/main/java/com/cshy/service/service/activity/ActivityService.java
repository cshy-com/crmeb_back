package com.cshy.service.service.activity;

import com.cshy.common.model.query.activity.ActivityQuery;
import com.cshy.common.model.dto.activity.ActivityDto;
import com.cshy.common.model.entity.activity.Activity;
import com.cshy.common.model.entity.base.BaseService;
import com.cshy.common.model.request.ActivityRequest;
import com.cshy.common.model.request.ActivitySearchRequest;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.vo.activity.ActivityVo;

import java.util.List;

public interface ActivityService extends BaseService<Activity, ActivityDto, ActivityQuery, ActivityVo> {
    List<Activity> getList(ActivitySearchRequest request, PageParamRequest pageParamRequest);

    boolean create(ActivityRequest activityRequest);

    boolean deleteById(Integer id);

    boolean updateArticle(ActivityRequest activityRequest);
}
