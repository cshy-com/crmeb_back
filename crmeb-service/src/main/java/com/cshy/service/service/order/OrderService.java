package com.cshy.service.service.order;

import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.request.*;
import com.cshy.common.model.request.order.OrderComputedPriceRequest;
import com.cshy.common.model.request.order.OrderRefundApplyRequest;
import com.cshy.common.model.request.order.PreOrderRequest;
import com.cshy.common.model.request.store.StoreProductReplyAddRequest;
import com.cshy.common.model.response.*;
import com.cshy.common.model.vo.MyRecord;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * H5端订单操作
 
 */
public interface OrderService {

    /**
     * 订单列表
     * @param params 查询参数
     * @param pageRequest 分页
     * @return 订单集合
     */
    CommonPage<OrderDetailResponse> list(Map<String, Object> params, PageParamRequest pageRequest);

    /**
     * 订单详情
     * @param orderId 订单id
     */
    StoreOrderDetailInfoResponse detailOrder(String orderId);

    /**
     * 订单状态数量
     * @return 订单状态数据量
     */
    OrderDataResponse orderData();

    /**
     * 查询退款理由
     * @return 退款理由集合
     */
    List<String> getRefundReason();

    /**
     * 订单删除
     * @param id 订单id
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 创建订单商品评价
     * @param request 请求参数
     * @return Boolean
     */
    Boolean reply(StoreProductReplyAddRequest request);

    /**
     * 订单收货
     * @param id 订单id
     * @return Boolean
     */
    Boolean take(Integer id);

    /**
     * 订单取消
     * @param id 订单id
     * @return Boolean
     */
    Boolean cancel(Integer id);

    /**
     * 订单退款申请
     * @param request 申请参数
     * @return Boolean
     */
    Boolean refundApply(OrderRefundApplyRequest request);

    /**
     * 订单退款申请Task使用
     * @param applyList 退款List
     * @return Boolean
     */
    Boolean refundApplyTask(List<OrderRefundApplyRequest> applyList);

    /**
     * 获取待评价商品信息
     * @param getProductReply 订单详情参数
     * @return 待评价
     */
    OrderProductReplyResponse getReplyProduct(GetProductReply getProductReply);

    /**
     * 获取申请订单退款信息
     * @param orderId 订单编号
     * @return ApplyRefundOrderInfoResponse
     */
    ApplyRefundOrderInfoResponse applyRefundOrderInfo(String orderId);

    /**
     * 订单预下单
     * @param request 预下单请求参数
     * @return PreOrderResponse
     */
    MyRecord preOrder(PreOrderRequest request);

    /**
     * 加载预下单信息
     * @param preOrderNo 预下单号
     * @return 预下单信息
     */
    PreOrderResponse loadPreOrder(String preOrderNo);

    /**
     * 计算订单价格
     * @param request 计算订单价格请求对象
     * @return ComputedOrderPriceResponse
     */
    ComputedOrderPriceResponse computedOrderPrice(OrderComputedPriceRequest request);

    /**
     * 创建订单
     * @param orderRequest 创建订单请求参数
     * @return MyRecord 订单编号
     */
    MyRecord createOrder(CreateOrderRequest orderRequest);

    /**
     * 获取支付配置
     * @return PreOrderResponse
     */
    PreOrderResponse getPayConfig();

    void refundTrackingNo(String trackingNo, String uni);

    void ship(String orderId, String trackingNo, Integer type, HttpServletRequest request);

    List<Map<String, Object>> operationList(Integer id);

    Boolean returnShip(String orderId, String trackingNo, String remark, String img);

    void refundRevoke(String orderId);
}
