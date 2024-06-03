package com.cshy.service.impl.store;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cshy.common.constants.*;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.entity.order.StoreOrder;
import com.cshy.common.model.entity.system.SystemAdmin;
import com.cshy.common.model.entity.system.SystemStoreStaff;
import com.cshy.common.model.request.store.StoreOrderStaticsticsRequest;
import com.cshy.common.model.response.StoreOrderVerificationConfirmResponse;
import com.cshy.common.model.response.StoreStaffDetail;
import com.cshy.common.model.response.StoreStaffTopDetail;
import com.cshy.common.utils.DateUtil;
import com.cshy.common.utils.RedisUtil;
import com.cshy.common.utils.SecurityUtil;
import com.cshy.common.model.vo.LoginUserVo;
import com.cshy.common.model.vo.dateLimitUtilVo;
import com.cshy.service.dao.store.StoreOrderDao;
import com.cshy.service.delete.OrderUtils;
import com.cshy.service.service.store.StoreOrderInfoService;
import com.cshy.service.service.store.StoreOrderService;
import com.cshy.service.service.store.StoreOrderVerification;
import com.cshy.service.service.system.SystemStoreStaffService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * StoreOrderVerificationImpl 接口实现 核销订单
 
 */
@Service
public class StoreOrderVerificationImpl implements StoreOrderVerification {

    @Resource
    private StoreOrderDao dao;

    @Autowired
    private OrderUtils orderUtils;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SystemStoreStaffService systemStoreStaffService;

    /**
     * 获取订单核销数据
     */
    @Override
    public StoreStaffTopDetail getOrderVerificationData() {
        StoreStaffTopDetail storeStaffTopDetail = new StoreStaffTopDetail();
        //订单支付没有退款 数量-
        LambdaQueryWrapper<StoreOrder> lqwOrderCount = Wrappers.lambdaQuery();
        lqwOrderCount.eq(StoreOrder::getIsDel,false).eq(StoreOrder::getPaid,true).eq(StoreOrder::getRefundStatus,0);
        storeStaffTopDetail.setOrderCount(dao.selectCount(lqwOrderCount));
        //订单支付没有退款 支付总金额
        LambdaQueryWrapper<StoreOrder> lqwSumPrice = Wrappers.lambdaQuery();
        lqwSumPrice.eq(StoreOrder::getIsDel,false).eq(StoreOrder::getPaid,true).eq(StoreOrder::getRefundStatus,0);
        List<StoreOrder> storeOrdersSumPrice = dao.selectList(lqwSumPrice);
        Double sumPrice = storeOrdersSumPrice.stream().mapToDouble(e->e.getPayPrice().doubleValue()).sum();
        storeStaffTopDetail.setSumPrice(BigDecimal.valueOf(sumPrice).setScale(2,BigDecimal.ROUND_HALF_UP));
        //订单待支付 数量
        LambdaQueryWrapper<StoreOrder> lqwUnPaidCount = Wrappers.lambdaQuery();
        orderUtils.statusApiByWhere(lqwUnPaidCount,0);
        storeStaffTopDetail.setUnpaidCount(dao.selectCount(lqwUnPaidCount));
        //订单待发货 数量
        LambdaQueryWrapper<StoreOrder> lqwUnShippedCount = Wrappers.lambdaQuery();
        orderUtils.statusApiByWhere(lqwUnShippedCount,1);
        storeStaffTopDetail.setUnshippedCount(dao.selectCount(lqwUnShippedCount));
        //订单待收货 数量
        LambdaQueryWrapper<StoreOrder> lqwReceivedCount = Wrappers.lambdaQuery();
        orderUtils.statusApiByWhere(lqwReceivedCount,2);
        storeStaffTopDetail.setReceivedCount(dao.selectCount(lqwReceivedCount));
        // 订单待核销数量
        LambdaQueryWrapper<StoreOrder> verificationCount = Wrappers.lambdaQuery();
        orderUtils.statusApiByWhere(verificationCount,3);
        storeStaffTopDetail.setVerificationCount(dao.selectCount(verificationCount));
        //订单已完成 数量
        LambdaQueryWrapper<StoreOrder> lqwCompleteCount = Wrappers.lambdaQuery();
        orderUtils.statusApiByWhere(lqwCompleteCount,4);
        storeStaffTopDetail.setCompleteCount(dao.selectCount(lqwCompleteCount));
        //订单退款 数量
        LambdaQueryWrapper<StoreOrder> lqwRefundCount = Wrappers.lambdaQuery();
        orderUtils.statusApiByWhere(lqwRefundCount,-3);
        storeStaffTopDetail.setRefundCount(dao.selectCount(lqwRefundCount));

        // 获取今日，昨日，本月，订单金额
        String dayStart = DateUtil.nowDateTime(DateConstants.DATE_FORMAT_START);
        String dayEnd = DateUtil.nowDateTime(DateConstants.DATE_FORMAT_END);
        String yesterdayStart = DateUtil.addDay(dayStart,-1, DateConstants.DATE_FORMAT_START);
        String yesterdayEnd = DateUtil.addDay(dayEnd,-1, DateConstants.DATE_FORMAT_END);
        String monthStart = DateUtil.nowDateTime(DateConstants.DATE_FORMAT_MONTH_START);
        String monthEnd = DateUtil.getMonthEndDay();

        // 今日订单数量
        LambdaQueryWrapper<StoreOrder> lqwTodayCount = Wrappers.lambdaQuery();
        lqwTodayCount.eq(StoreOrder::getIsDel,false).between(StoreOrder::getPayTime,dayStart,dayEnd)
                .eq(StoreOrder::getPaid,1).eq(StoreOrder::getRefundStatus,0);
        List<StoreOrder> storeOrdersTodayCount = dao.selectList(lqwTodayCount);
        if(null == storeOrdersTodayCount) storeOrdersTodayCount = new ArrayList<>();
        storeStaffTopDetail.setTodayCount(storeOrdersTodayCount.size());

        // 今日成交额
        double todayPrice = storeOrdersTodayCount.stream().mapToDouble(e->e.getPayPrice().doubleValue()).sum();
        storeStaffTopDetail.setTodayPrice(BigDecimal.valueOf(todayPrice).setScale(2,BigDecimal.ROUND_HALF_UP));

        // 昨日订单数
        LambdaQueryWrapper<StoreOrder> lqwPro = Wrappers.lambdaQuery();
        lqwPro.eq(StoreOrder::getIsDel,false).between(StoreOrder::getCreateTime,yesterdayStart,yesterdayEnd)
                .eq(StoreOrder::getPaid, true).eq(StoreOrder::getRefundStatus,0);
        List<StoreOrder> storeOrdersPro = dao.selectList(lqwPro);
        if(null == storeOrdersPro) storeOrdersPro = new ArrayList<>();
        storeStaffTopDetail.setProCount(storeOrdersPro.size());

        //  昨日成交额
        double proPrice = storeOrdersPro.stream().mapToDouble(e->e.getPayPrice().doubleValue()).sum();
        storeStaffTopDetail.setProPrice(BigDecimal.valueOf(proPrice).setScale(2,BigDecimal.ROUND_HALF_UP));

        // 本月成交订单数量
        LambdaQueryWrapper<StoreOrder> lqwMonth = Wrappers.lambdaQuery();
        lqwMonth.eq(StoreOrder::getIsDel,false).between(StoreOrder::getPayTime,monthStart, monthEnd)
                .eq(StoreOrder::getPaid,true).eq(StoreOrder::getRefundStatus,0);
        List<StoreOrder> storeOrdersMonth = dao.selectList(lqwMonth);
        if(null == storeOrdersMonth) storeOrdersMonth = new ArrayList<>();
        storeStaffTopDetail.setMonthCount(storeOrdersMonth.size());

        // 本月成交额
        double monthTotalPrice = storeOrdersMonth.stream().mapToDouble(e -> e.getPayPrice().doubleValue()).sum();
        storeStaffTopDetail.setMonthPrice(BigDecimal.valueOf(monthTotalPrice).setScale(2,BigDecimal.ROUND_HALF_UP));

        return storeStaffTopDetail;
    }

    /**
     * 核销月详情
     * @return 月详情
     */
    @Override
    public List<StoreStaffDetail> getOrderVerificationDetail(StoreOrderStaticsticsRequest request) {
        request.setPage((request.getPage() - 1) * request.getLimit());
        dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
        request.setStartTime(dateLimit.getStartTime());
        request.setEndTime(dateLimit.getEndTime());
        return dao.getOrderVerificationDetail(request);
    }


    /**
     * 根据核销码核销订单(相当于收货)
     *
     * @param vCode 核销码
     * @return 核销结果
     */
    @Override
    public boolean verificationOrderByCode(String vCode) {
        StoreOrderVerificationConfirmResponse existOrder = getVerificationOrderByCode(vCode);
        // 判断当前用户是否有权限核销
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        SystemAdmin currentAdmin = loginUserVo.getUser();
        SystemStoreStaff systemStoreStaff = systemStoreStaffService.getOne(new LambdaQueryWrapper<SystemStoreStaff>().eq(SystemStoreStaff::getUid, currentAdmin.getId()));
        if (Objects.nonNull(systemStoreStaff)){
            if (systemStoreStaff.getStoreId().equals(existOrder.getStoreId())){
                StoreOrder storeOrder = new StoreOrder();
                BeanUtils.copyProperties(existOrder,storeOrder);
                storeOrder.setStatus(StoreOrderStatusConstants.ORDER_STATUS_INT_BARGAIN);
                storeOrder.setClerkId(currentAdmin.getId());
                boolean saveStatus = dao.updateById(storeOrder) > 0;
                // 小程序订阅消息发送
                if(saveStatus){
                    //后续操作放入redis
                    redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER, storeOrder.getId());
                }

                return saveStatus;
            }
            throw new CrmebException("门店错误，请确认自提门店是否正确");
        }
        throw new CrmebException("没有核销权限，请联系管理员将您添加到核销员列表");
    }

    /**
     * 根据核销码查询待核销订单
     *
     * @param vCode 核销码
     * @return 待核销订单详情
     */
    @Override
    public StoreOrderVerificationConfirmResponse getVerificationOrderByCode(String vCode) {
        StoreOrderVerificationConfirmResponse response = new StoreOrderVerificationConfirmResponse();
        StoreOrder storeOrderPram = new StoreOrder().setVerifyCode(vCode).setPaid(true).setRefundStatus(0);
        StoreOrder existOrder = storeOrderService.getByEntityOne(storeOrderPram);
        if(null == existOrder) throw new CrmebException(MsgConstants.RESULT_VERIFICATION_ORDER_NOT_FUND.replace("${vCode}",vCode));
        if(existOrder.getStatus() > 0) throw new CrmebException(MsgConstants.RESULT_VERIFICATION_ORDER_VED.replace("${vCode}",vCode));
        BeanUtils.copyProperties(existOrder, response);
        response.setStoreOrderInfoVos(storeOrderInfoService.getOrderListByOrderId(existOrder.getId()));
        return response;
    }
}