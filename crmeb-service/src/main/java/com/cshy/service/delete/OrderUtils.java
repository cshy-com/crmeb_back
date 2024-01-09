package com.cshy.service.delete;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cshy.common.constants.*;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.entity.order.StoreOrder;
import com.cshy.common.model.vo.order.StoreOrderInfoOldVo;
import com.cshy.service.service.store.StoreOrderInfoService;
import com.cshy.service.service.store.StoreOrderService;
import com.cshy.service.service.system.SystemConfigService;
import com.cshy.service.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 订单工具类

 */
@Service
public class OrderUtils {

    @Autowired
    private UserService userService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    /**
     * 检测支付渠道
     * @param payChannel 支付渠道
     */
    public static boolean checkPayChannel(String payChannel) {
       if (!payChannel.equals(PayConstants.PAY_CHANNEL_WE_CHAT_H5) &&
               !payChannel.equals(PayConstants.PAY_CHANNEL_WE_CHAT_PROGRAM) &&
               !payChannel.equals(PayConstants.PAY_CHANNEL_WE_CHAT_PUBLIC) &&
               !payChannel.equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS) &&
               !payChannel.equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID)) {
           return false;
       }
       return true;
    }

    /**
     * 检查支付类型
     * @param payType 支付类型标识
     * @return 是否支持
     */
    public Boolean checkPayType(String payType){
        boolean result = false;
        payType = payType.toLowerCase();
        switch (payType){
            case PayType.PAY_TYPE_WE_CHAT:
                result = "1".equals(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_PAY_WEIXIN_OPEN));
                break;
            case PayType.PAY_TYPE_INTEGRAL:
                //TODO 积分支付开关
                result = true;
                break;
//            case PayType.PAY_TYPE_YUE:
//                result = ("1".equals(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_YUE_PAY_STATUS)));
//                break;
//            case PayType.PAY_TYPE_ALI_PAY:
//                result = ("1".equals(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_ALI_PAY_STATUS)));
//                break;
        }
        return result;
    }

    /**
     * 根据订单号查询订单信息
     * @param id 订单id
     * @return 计算后的价格集合
     */
    public StoreOrder getInfoById(Integer id) {
        StoreOrder storeOrder = storeOrderService.getById(id);
        Integer userId = userService.getUserIdException();

        if(null == storeOrder || !userId.equals(storeOrder.getUid())){
            //订单号错误
            throw new CrmebException("没有找到相关订单信息!");
        }

        return storeOrder;
    }

    /**
     * 翻译支付方式给前端
     * @param payType
     * @return
     */
    public String getOrderPayTypeStr(String payType){
        String payTypeStr = null;
        switch (payType){
            case PayType.PAY_TYPE_WE_CHAT:
                payTypeStr = "微信支付";
                break;
            case PayType.PAY_TYPE_YUE:
                payTypeStr = "余额支付";
                break;
            case PayType.PAY_TYPE_OFFLINE:
                payTypeStr = "线下支付";
                break;
            case PayType.PAY_TYPE_ALI_PAY:
                payTypeStr = "支付宝支付";
                break;
            case PayType.PAY_TYPE_INTEGRAL:
                payTypeStr = "积分支付";
                break;
            default:
                payTypeStr = "其他支付方式";
                break;
        }
        return payTypeStr;
    }

    /**
     * h5 订单查询 where status 封装
     * @param queryWrapper 查询条件
     * @param status 状态
     */
    public void statusApiByWhere(LambdaQueryWrapper<StoreOrder> queryWrapper, Integer status){
        if (Objects.nonNull(status)){
            switch (status){
                case StoreOrderStatusConstants.ORDER_STATUS_H5_UNPAID: // 未支付
                    queryWrapper.eq(StoreOrder::getPaid, false);
                    queryWrapper.eq(StoreOrder::getStatus, 0);
                    queryWrapper.eq(StoreOrder::getRefundStatus, 0);
                    queryWrapper.eq(StoreOrder::getType, 0);
                    break;
                case StoreOrderStatusConstants.ORDER_STATUS_H5_NOT_SHIPPED: // 待发货
                    queryWrapper.eq(StoreOrder::getPaid, true);
                    queryWrapper.eq(StoreOrder::getStatus, 0);
                    queryWrapper.notIn(StoreOrder::getRefundStatus, 1, 2, 3, 4, 5);
//                queryWrapper.eq(StoreOrder::getShippingType, 1);
                    break;
                case StoreOrderStatusConstants.ORDER_STATUS_H5_SPIKE: // 待收货
                    queryWrapper.eq(StoreOrder::getPaid, true);
                    queryWrapper.eq(StoreOrder::getStatus, 1);
                    queryWrapper.notIn(StoreOrder::getRefundStatus, 1, 2, 3, 4, 5);
                    break;
                case StoreOrderStatusConstants.ORDER_STATUS_H5_JUDGE: //  已支付 已收货 待评价
                    queryWrapper.eq(StoreOrder::getPaid, true);
                    queryWrapper.eq(StoreOrder::getStatus, 2);
                    queryWrapper.notIn(StoreOrder::getRefundStatus, 1, 2, 3, 4, 5);
                    break;
                case StoreOrderStatusConstants.ORDER_STATUS_H5_COMPLETE: // 已完成
                    queryWrapper.eq(StoreOrder::getPaid, true);
                    queryWrapper.eq(StoreOrder::getStatus, 3);
                    queryWrapper.eq(StoreOrder::getRefundStatus, 0);
                    break;
                case StoreOrderStatusConstants.ORDER_STATUS_H5_REFUNDING: // 退款中
                    queryWrapper.eq(StoreOrder::getPaid, true);
                    queryWrapper.in(StoreOrder::getRefundStatus, 1, 3);
                    break;
                case StoreOrderStatusConstants.ORDER_STATUS_H5_REFUNDED: // 已退款
                    queryWrapper.eq(StoreOrder::getPaid, true);
                    queryWrapper.eq(StoreOrder::getRefundStatus, 2);
                    break;
                case StoreOrderStatusConstants.ORDER_STATUS_H5_REFUND: // 包含已退款和退款中 退货待发货  退货已发货 退货退款被拒绝
                    queryWrapper.eq(StoreOrder::getPaid, true);
                    queryWrapper.in(StoreOrder::getRefundStatus, 1,2,3,4,5,6);
                    break;
                case StoreOrderStatusConstants.ORDER_STATUS_H5_REFUND_PROCESSING: // 包含已退款和退款中 退货待发货  退货已发货 退货退款被拒绝
                    queryWrapper.eq(StoreOrder::getPaid, true);
                    queryWrapper.eq(StoreOrder::getRefundStatus, 4);
                    break;
            }
        }
        queryWrapper.eq(StoreOrder::getIsDel, false);
        queryWrapper.eq(StoreOrder::getIsSystemDel, false);
    }

    /**
     * 根据订单id获取订单中商品和名称和购买数量字符串
     * @param orderId   订单id
     * @return          商品名称*购买数量
     */
    public String getStoreNameAndCarNumString(int orderId){
        List<StoreOrderInfoOldVo> currentOrderInfo = storeOrderInfoService.getOrderListByOrderId(orderId);
        if(currentOrderInfo.size() > 0) {
            StringBuilder sbOrderProduct = new StringBuilder();
            for (StoreOrderInfoOldVo storeOrderInfoVo : currentOrderInfo) {
                sbOrderProduct.append(storeOrderInfoVo.getInfo().getProductName() + "*"
                        + storeOrderInfoVo.getInfo().getPayNum());
            }
            return sbOrderProduct.toString();
        }
        return null;
    }
}
