package com.cshy.service.impl.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.constants.MsgConstants;
import com.cshy.common.constants.RedisKey;
import com.cshy.common.constants.StoreOrderStatusConstants;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.constants.Constants;
import com.cshy.common.model.request.store.StoreOrderStatusSearchRequest;
import com.github.pagehelper.PageHelper;
import com.cshy.common.utils.DateUtil;
import com.cshy.common.model.entity.order.StoreOrder;
import com.cshy.common.model.entity.order.StoreOrderStatus;
import com.cshy.service.dao.store.StoreOrderStatusDao;
import com.cshy.service.service.store.StoreOrderService;
import com.cshy.service.service.store.StoreOrderStatusService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * StoreOrderStatusServiceImpl 接口实现

 */
@Service
public class StoreOrderStatusServiceImpl extends ServiceImpl<StoreOrderStatusDao, StoreOrderStatus> implements StoreOrderStatusService {

    @Resource
    private StoreOrderStatusDao dao;

    @Autowired
    private StoreOrderService storeOrderService;

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @return List<StoreOrderStatus>
    */
    @Override
    public List<StoreOrderStatus> getList(StoreOrderStatusSearchRequest request, PageParamRequest pageParamRequest) {
        StoreOrder storeOrder = storeOrderService.getByOrderId(request.getOrderNo());
        if (ObjectUtil.isNull(storeOrder)) {
            return CollUtil.newArrayList();
        }
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreOrderStatus> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreOrderStatus::getOid, storeOrder.getId());
        lqw.orderByDesc(StoreOrderStatus::getCreateTime);
        List<StoreOrderStatus> storeOrderStatuses = dao.selectList(lqw);
        storeOrderStatuses.forEach(storeOrderStatus -> {
            String type;
            switch (storeOrderStatus.getChangeType()){
                case StoreOrderStatusConstants.ORDER_LOG_REFUND_REFUSE:
                    if (storeOrder.getRefundType().equals(0))
                        type = "退款被拒绝";
                    else
                        type = "退货退款被拒绝";
                    break;
                case StoreOrderStatusConstants.ORDER_LOG_PAY_SUCCESS:
                    type = "支付成功";
                    break;
                case StoreOrderStatusConstants.ORDER_LOG_EXPRESS:
                    type = "已发货";
                    break;
                case StoreOrderStatusConstants.ORDER_LOG_EDIT:
                    type = "编辑订单";
                    break;
                case StoreOrderStatusConstants.ORDER_LOG_REFUND_PRICE:
                    type = "退款";
                    break;
                case StoreOrderStatusConstants.ORDER_LOG_CANCEL:
                    type = "取消订单";
                    break;
                case StoreOrderStatusConstants.ORDER_STATUS_CACHE_CREATE_ORDER:
                    type = "订单生成";
                    break;
                case StoreOrderStatusConstants.ORDER_LOG_RETURN_GOODS:
                    type = "退货中";
                    break;
                case StoreOrderStatusConstants.ORDER_LOG_REFUND_APPLY:
                    if (storeOrder.getRefundType().equals(0))
                        type = "申请退款";
                    else
                        type = "申请退货退款";
                    storeOrderStatus.setChangeMessage(storeOrderStatus.getChangeMessage() + "，原因：" + storeOrder.getRefundReasonWapExplain());
                    break;
                case StoreOrderStatusConstants.ORDER_LOG_AGREE_RETURN:
                    type = "平台同意退货退款";
                    break;
                case StoreOrderStatusConstants.ORDER_LOG_AGREE_REFUND:
                    type = "平台同意退款";
                    break;
                case StoreOrderStatusConstants.ORDER_LOG_REFUND_REVOKE:
                    type = "用户撤销售后";
                    break;
                default:
                    type = "系统操作";
            }
            storeOrderStatus.setChangeType(type);
        });
        return storeOrderStatuses;
    }

    /**
     * 保存订单退款记录
     * @param orderId 订单号
     * @param amount  金额
     * @param message  备注
     * @return {@link Boolean}
     */
    @Override
    public Boolean saveRefund(Integer orderId, BigDecimal amount, String message) {
        //此处更新订单状态
        String changeMessage = MsgConstants.ORDER_LOG_MESSAGE_REFUND_PRICE.replace("{amount}", amount.toString());
        if(StringUtils.isNotBlank(message)){
            changeMessage += message;
        }
        StoreOrderStatus storeOrderStatus = new StoreOrderStatus();
        storeOrderStatus.setOid(orderId);
        storeOrderStatus.setChangeType(StoreOrderStatusConstants.ORDER_LOG_REFUND_PRICE);
        storeOrderStatus.setChangeMessage(changeMessage);
        storeOrderStatus.setIsSysUser(1);
        return save(storeOrderStatus);
    }

    @Override
    public Boolean saveCancel(Integer orderId, BigDecimal amount, String message) {
        //此处更新订单状态
        String changeMessage = MsgConstants.ORDER_LOG_MESSAGE_REFUND_PRICE.replace("{amount}", amount.toString());
        if(StringUtils.isNotBlank(message)){
            changeMessage += message;
        }
        StoreOrderStatus storeOrderStatus = new StoreOrderStatus();
        storeOrderStatus.setOid(orderId);
        storeOrderStatus.setChangeType(StoreOrderStatusConstants.ORDER_LOG_CANCEL);
        storeOrderStatus.setChangeMessage(changeMessage);
        storeOrderStatus.setIsSysUser(1);
        return save(storeOrderStatus);
    }

    /**
     * 创建记录日志
     * @param orderId Integer 订单号
     * @param type String 类型
     * @param message String 消息
     * @return Boolean
     */
    @Override
    public Boolean createLog(Integer orderId, String type, String message, Integer isSysUser) {
        StoreOrderStatus storeOrderStatus = new StoreOrderStatus();
        storeOrderStatus.setOid(orderId);
        storeOrderStatus.setChangeType(type);
        storeOrderStatus.setChangeMessage(message);
        storeOrderStatus.setCreateTime(DateUtil.nowDateTime());
        storeOrderStatus.setIsSysUser(isSysUser);
        return save(storeOrderStatus);
    }

    /**
     * 根据实体获取
     * @param storeOrderStatus 订单状态参数
     * @return 查询结果
     */
    @Override
    public List<StoreOrderStatus> getByEntity(StoreOrderStatus storeOrderStatus) {
        LambdaQueryWrapper<StoreOrderStatus> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.setEntity(storeOrderStatus);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据订单id获取最后一条记录
     * @param orderId 订单id
     * @return StoreOrderStatus
     */
    @Override
    public StoreOrderStatus getLastByOrderId(Integer orderId) {
        QueryWrapper<StoreOrderStatus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("oid", orderId);
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last(" limit 1");
        return dao.selectOne(queryWrapper);
    }

    /**
     * 通过日期获取订单退款数量
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    @Override
    public Integer getRefundOrderNumByDate(String date) {
        QueryWrapper<StoreOrderStatus> wrapper = new QueryWrapper<>();
        wrapper.select("oid");
        wrapper.eq("change_type", "refund_price");
        wrapper.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(wrapper);
    }

    /**
     * 通过日期获取订单退款金额
     * @param date 日期，yyyy-MM-dd格式
     * @return BigDecimal
     */
    @Override
    public BigDecimal getRefundOrderAmountByDate(String date) {
        return dao.getRefundOrderAmountByDate(date);
    }

}

