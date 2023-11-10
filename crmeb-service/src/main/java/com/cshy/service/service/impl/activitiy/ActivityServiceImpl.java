package com.cshy.service.service.impl.activitiy;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.query.activity.ActivityQuery;
import com.cshy.common.model.dto.activity.ActivityDto;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.request.ActivityRequest;
import com.cshy.common.model.vo.activity.ActivityVo;
import com.github.pagehelper.PageHelper;
import com.cshy.common.model.entity.activity.Activity;
import com.cshy.common.model.request.ActivitySearchRequest;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.service.dao.activity.ActivityDao;
import com.cshy.service.service.activity.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ActivityServiceImpl extends BaseServiceImpl<Activity, ActivityDto,
        ActivityQuery, ActivityVo, ActivityDao> implements ActivityService {
    private static final Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);
    @Resource
    private ActivityDao dao;
    @Override
    public List<Activity> getList(ActivitySearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Activity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(null != request.getType()){
            lambdaQueryWrapper.eq(Activity::getName, request.getName());
        }
        if(null != request.getName()){
            lambdaQueryWrapper.like(Activity::getName, request.getName());
        }
        if(null != request.getStatus()){
            lambdaQueryWrapper.like(Activity::getStatus, request.getStatus());
        }
        return dao.selectList(lambdaQueryWrapper);
    }

    @Override
    public boolean create(ActivityRequest activityRequest) {
        Activity activity = new Activity();
        BeanUtils.copyProperties(activityRequest, activity);
        return save(activity);
    }

    @Override
    public boolean deleteById(Integer id) {
        Activity activity = getById(id);
        if (ObjectUtil.isNull(activity)) {
            throw new CrmebException("文章已删除");
        }
        return removeById(id);
    }

    @Override
    public boolean updateArticle(ActivityRequest activityRequest) {
        Activity activity = new Activity();
        BeanUtils.copyProperties(activityRequest, activity);
        return updateById(activity);
    }
}
