package com.cshy.service.dao;

import com.cshy.common.model.entity.order.StoreOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cshy.common.model.request.StoreDateRangeSqlPram;
import com.cshy.common.model.request.StoreOrderStaticsticsRequest;
import com.cshy.common.model.response.OrderBrokerageData;
import com.cshy.common.model.response.StoreOrderStatisticsChartItemResponse;
import com.cshy.common.model.response.StoreStaffDetail;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单表 Mapper 接口

 */
public interface StoreOrderDao extends BaseMapper<StoreOrder> {

    /**
     * 订单总金额
     */
    BigDecimal getTotalPrice(String where);

    /**
     * 退款总金额
     */
    BigDecimal getRefundPrice(String where);

    /**
     * 获取退款总单数
     */
    Integer getRefundTotal(String where);

    /**
     * 核销详情 月数据
     * @param request 分页和日期
     * @return 月数据
     */
    List<StoreStaffDetail> getOrderVerificationDetail(StoreOrderStaticsticsRequest request);

    /**
     * 订单统计详情 price
     * @param pram 时间区间参数
     * @return 月数据
     */
    List<StoreOrderStatisticsChartItemResponse> getOrderStatisticsPriceDetail(StoreDateRangeSqlPram pram);

    /**
     * 订单统计详情 订单量
     * @param pram 时间区间参数
     * @return 月数据
     */
    List<StoreOrderStatisticsChartItemResponse> getOrderStatisticsOrderCountDetail(StoreDateRangeSqlPram pram);

    /**
     * 获取佣金相关数据
     * @param uid 用户uid
     * @param spreadId 推广人uid
     */
    OrderBrokerageData getBrokerageData(@Param("uid") Integer uid, @Param("spreadId") Integer spreadId);
}
