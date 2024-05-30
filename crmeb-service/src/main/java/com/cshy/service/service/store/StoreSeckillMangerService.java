package com.cshy.service.service.store;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.entity.seckill.StoreSeckillManger;
import com.cshy.common.model.request.store.StoreSeckillMangerRequest;
import com.cshy.common.model.request.store.StoreSeckillMangerSearchRequest;
import com.cshy.common.model.response.StoreSeckillManagerResponse;

import java.util.List;

/**
 * StoreSeckillMangerService 接口

 */
public interface StoreSeckillMangerService extends IService<StoreSeckillManger> {

    /**
     * 秒杀配置列表
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return List
     */
    List<StoreSeckillManagerResponse> page(StoreSeckillMangerSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 秒杀配置列表
     * @param request 查询参数
     * @return List
     */
    List<StoreSeckillManagerResponse> list(StoreSeckillMangerSearchRequest request);

    /**
     * 删除秒杀配置 逻辑删除
     * @param id 待删除id
     * @return  删除结果
     */
    boolean deleteLogicById(int id);

    /**
     * 详情
     * @param id 配置id
     * @return  查询到的结果
     */
    StoreSeckillManagerResponse detail(int id);

    /**
     * 获取正在秒杀的时间段
     * @return 正在秒杀的时间段
     */
    List<StoreSeckillManger> getCurrentSeckillManager();

    /**
     * 更新秒杀配置状态
     * @param id id
     * @param status 待更新状态
     * @return 结果
     */
    Boolean updateStatus(Integer id, Boolean status);

    /**
     * 更新秒杀配置
     * @param id id
     * @param storeSeckillMangerRequest 秒杀配置
     * @return 结果
     */
    Boolean update(Integer id, StoreSeckillMangerRequest storeSeckillMangerRequest);

    /**
     * 获取移动端列表(正在进行和马上开始的秒杀)
     * @return List<StoreSeckillManagerResponse>
     */
    List<StoreSeckillManagerResponse> getH5List();

    /**
     * 获取所有秒杀配置
     * @return List
     */
    List<StoreSeckillManagerResponse> getAllList();

    /**
     * 添加秒杀配置
     * @param storeSeckillMangerRequest 配置参数
     */
    Boolean saveManger(StoreSeckillMangerRequest storeSeckillMangerRequest);
}
