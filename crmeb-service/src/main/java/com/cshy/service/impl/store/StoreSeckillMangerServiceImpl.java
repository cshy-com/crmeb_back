package com.cshy.service.impl.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.request.store.StoreSeckillMangerRequest;
import com.cshy.common.model.request.store.StoreSeckillMangerSearchRequest;
import com.cshy.common.model.response.StoreSeckillManagerResponse;
import com.cshy.common.utils.StringUtils;
import com.github.pagehelper.PageHelper;
import com.cshy.common.utils.DateUtil;
import com.cshy.common.model.entity.seckill.StoreSeckillManger;
import com.cshy.service.dao.store.StoreSeckillMangerDao;
import com.cshy.service.service.store.StoreSeckillMangerService;
import com.cshy.service.service.system.SystemAttachmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * StoreSeckillMangerServiceImpl 接口实现
 */
@Service
public class StoreSeckillMangerServiceImpl extends ServiceImpl<StoreSeckillMangerDao, StoreSeckillManger>
        implements StoreSeckillMangerService {

    @Resource
    private StoreSeckillMangerDao dao;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页类参数
     * @return List<StoreSeckillManger>
     */
    @Override
    public List<StoreSeckillManagerResponse> getList(StoreSeckillMangerSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreSeckillManger 类的多条件查询
        LambdaQueryWrapper<StoreSeckillManger> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(request.getName()))
            lambdaQueryWrapper.like(StoreSeckillManger::getName, request.getName());
        if (null != request.getStatus()) lambdaQueryWrapper.eq(StoreSeckillManger::getStatus, request.getStatus());
        if (null != request.getType()) lambdaQueryWrapper.eq(StoreSeckillManger::getType, request.getType());
        if (StringUtils.isNotBlank(request.getActivityCategoryName())) lambdaQueryWrapper.eq(StoreSeckillManger::getActivityCat, request.getActivityCategoryName());

        lambdaQueryWrapper.eq(StoreSeckillManger::getIsDel, false);
        lambdaQueryWrapper.orderByAsc(StoreSeckillManger::getSort);

        // 处理数据time格式 适配前端
        List<StoreSeckillManagerResponse> responses = new ArrayList<>();
        List<StoreSeckillManger> storeSeckillMangers = dao.selectList(lambdaQueryWrapper);
        convertTime(responses, storeSeckillMangers);
        return responses;
    }

    /**
     * 删除秒杀配置 逻辑删除
     *
     * @param id 待删除id
     * @return 删除结果
     */
    @Override
    public boolean deleteLogicById(int id) {
        return dao.deleteById(id) > 0;
    }

    /**
     * 详情
     *
     * @param id 配置id
     * @return 查询到的结果
     */
    @Override
    public StoreSeckillManagerResponse detail(int id) {
        StoreSeckillManger storeSeckillManger = dao.selectById(id);
        StoreSeckillManagerResponse response = new StoreSeckillManagerResponse();
        BeanUtils.copyProperties(storeSeckillManger, response);
        response.setImage(storeSeckillManger.getImg());
        response.setSliderImages(storeSeckillManger.getSilderImgs());
        cTime(storeSeckillManger, response);
        return response;
    }

    /**
     * 获取正在秒杀的时间段
     *
     * @return 正在秒杀的时间段
     */
    @Override
    public List<StoreSeckillManger> getCurrentSeckillManager() {
        int currentHour = DateUtil.getCurrentHour();
        LambdaQueryWrapper<StoreSeckillManger> lqw = Wrappers.lambdaQuery();
        lqw.le(StoreSeckillManger::getStartTime, currentHour).gt(StoreSeckillManger::getEndTime, currentHour);
        return dao.selectList(lqw);
    }

    /**
     * 更新秒杀配置状态
     *
     * @param id     id
     * @param status 待更新状态
     * @return 结果
     */
    @Override
    public Boolean updateStatus(Integer id, Boolean status) {
        StoreSeckillManger storeSeckillManger = dao.selectById(id);
        storeSeckillManger.setStatus(status ? 1 : 0);
        return dao.updateById(storeSeckillManger) > 0;
    }

    /**
     * 更新秒杀配置
     *
     * @param id                        id
     * @param storeSeckillMangerRequest 秒杀配置
     * @return 结果
     */
    @Override
    public Boolean update(Integer id, StoreSeckillMangerRequest storeSeckillMangerRequest) {
        StoreSeckillManger seckillManger = getById(id);
        BeanUtils.copyProperties(storeSeckillMangerRequest, seckillManger);
        seckillManger.setImg(systemAttachmentService.clearPrefix(storeSeckillMangerRequest.getImage()));
        seckillManger.setSilderImgs(systemAttachmentService.clearPrefix(storeSeckillMangerRequest.getSliderImages()));
        // 对request中的time做分割后赋值给mode中的start和end属性
        setTimeRangeFromRequest(storeSeckillMangerRequest, seckillManger);
        seckillManger.setId(id);
        return updateById(seckillManger);
    }

    /////////////////////////////////////////////////// 自定义方法

    // 列表用 格式化time 对前端输出一致
    private void convertTime(List<StoreSeckillManagerResponse> responses, List<StoreSeckillManger> storeSeckillMangers) {
        storeSeckillMangers.forEach(e -> {
            StoreSeckillManagerResponse r = new StoreSeckillManagerResponse();
            BeanUtils.copyProperties(e, r);
            cTime(e, r);
            r.setSliderImages(e.getSilderImgs());
            r.setImage(e.getImg());
            responses.add(r);
        });
    }

    // 详情用 格式化time 对前端输出一致
    private void cTime(StoreSeckillManger e, StoreSeckillManagerResponse r) {
        String pStartTime = e.getStartTime().toString();
        String pEndTime = e.getEndTime().toString();
        String startTime = pStartTime.length() == 1 ? "0" + pStartTime : pStartTime;
        String endTime = pEndTime.length() == 1 ? "0" + pEndTime : pEndTime;
        r.setTime(startTime + ":00," + endTime + ":00");
    }

    /**
     * 兼容时间参数 request中String格式 mode中Integer
     *
     * @param storeSeckillMangerRequest request参数
     * @param storeSeckillManger        秒杀配置实体
     */
    private void setTimeRangeFromRequest(@Validated @RequestBody StoreSeckillMangerRequest storeSeckillMangerRequest, StoreSeckillManger storeSeckillManger) {
        if (!storeSeckillMangerRequest.getTime().contains(",")) {
            throw new CrmebException("时间参数不正确 例如:01:00,02:00");
        }
        String[] timeRage = storeSeckillMangerRequest.getTime().split(",");
        Integer startTime = Integer.parseInt(timeRage[0].split(":")[0]);
        Integer endTime = Integer.parseInt(timeRage[1].split(":")[0]);
        storeSeckillManger.setStartTime(startTime);
        storeSeckillManger.setEndTime(endTime);
    }

    /**
     * 获取移动端列表 (正在进行和马上开始的秒杀)
     *
     * @return List<StoreSeckillManagerResponse>
     */
    @Override
    public List<StoreSeckillManagerResponse> getH5List() {
        LambdaQueryWrapper<StoreSeckillManger> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(StoreSeckillManger::getIsDel, false);
        lambdaQueryWrapper.eq(StoreSeckillManger::getStatus, 1);
        // 获取当前小时
        int currentHour = DateUtil.getCurrentHour();
        lambdaQueryWrapper.gt(StoreSeckillManger::getEndTime, currentHour);
        lambdaQueryWrapper.orderByAsc(StoreSeckillManger::getStartTime);
        List<StoreSeckillManger> storeSeckillMangers = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(storeSeckillMangers)) {
            return CollUtil.newArrayList();
        }
        // 处理数据time格式 适配前端
        List<StoreSeckillManagerResponse> responses = new ArrayList<>();
        convertTime(responses, storeSeckillMangers);
        return responses;
    }

    /**
     * 获取所有秒杀配置
     *
     * @return List
     */
    @Override
    public List<StoreSeckillManagerResponse> getAllList() {
        LambdaQueryWrapper<StoreSeckillManger> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.orderByAsc(StoreSeckillManger::getSort);
        // 处理数据time格式 适配前端
        List<StoreSeckillManger> storeSeckillMangers = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(storeSeckillMangers)) {
            return CollUtil.newArrayList();
        }
        List<StoreSeckillManagerResponse> responses = new ArrayList<>();
        convertTime(responses, storeSeckillMangers);
        return responses;
    }

    /**
     * 添加秒杀配置
     *
     * @param storeSeckillMangerRequest 配置参数
     */
    @Override
    public Boolean saveManger(StoreSeckillMangerRequest storeSeckillMangerRequest) {
        StoreSeckillManger storeSeckillManger = new StoreSeckillManger();
        BeanUtils.copyProperties(storeSeckillMangerRequest, storeSeckillManger);
        // 对request中的time做分割后赋值给mode中的start和end属性
        setTimeRangeFromRequest(storeSeckillMangerRequest, storeSeckillManger);
        storeSeckillManger.setImg(systemAttachmentService.clearPrefix(storeSeckillMangerRequest.getImage()));
        storeSeckillManger.setSilderImgs(systemAttachmentService.clearPrefix(storeSeckillMangerRequest.getSliderImages()));
        return save(storeSeckillManger);
    }
}

