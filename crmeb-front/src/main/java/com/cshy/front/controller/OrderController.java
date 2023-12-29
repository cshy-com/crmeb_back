package com.cshy.front.controller;

import cn.hutool.core.lang.Assert;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.request.*;
import com.cshy.common.model.request.order.OrderComputedPriceRequest;
import com.cshy.common.model.request.order.OrderRefundApplyRequest;
import com.cshy.common.model.request.order.PreOrderRequest;
import com.cshy.common.model.request.store.StoreProductReplyAddRequest;
import com.cshy.common.model.response.*;
import com.cshy.service.service.order.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * H5端订单操作
 */
@Slf4j
@RestController("OrderFrontController")
@RequestMapping("api/front/order")
@Api(tags = "订单")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 预下单
     */
    @ApiOperation(value = "预下单")
    @RequestMapping(value = "/pre/order", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> preOrder(@RequestBody @Validated PreOrderRequest request) {
        return CommonResult.success(orderService.preOrder(request));
    }

    /**
     * 加载预下单
     */
    @ApiOperation(value = "加载预下单")
    @RequestMapping(value = "load/pre/{preOrderNo}", method = RequestMethod.GET)
    public CommonResult<PreOrderResponse> loadPreOrder(@PathVariable String preOrderNo) {
        return CommonResult.success(orderService.loadPreOrder(preOrderNo));
    }

    /**
     * 根据参数计算订单价格
     */
    @ApiOperation(value = "计算订单价格")
    @RequestMapping(value = "/computed/price", method = RequestMethod.POST)
    public CommonResult<ComputedOrderPriceResponse> computedPrice(@Validated @RequestBody OrderComputedPriceRequest request) {
        return CommonResult.success(orderService.computedOrderPrice(request));
    }

    /**
     * 创建订单
     */
    @ApiOperation(value = "创建订单")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> createOrder(@Validated @RequestBody CreateOrderRequest orderRequest) {
        return CommonResult.success(orderService.createOrder(orderRequest));
    }

    /**
     * 订单列表
     *
     * @param params    type : 评价等级|0=未支付,1=待发货,2=待收货,3=待评价,4=已完成,-3=售后/退款\n" +
     *                     "condition: 查询条件 orderId/商品名称\n startDate、endDate: 查询时间区间
     * @return 订单列表
     */
    @ApiOperation(value = "订单列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "type : 评价等级|0=未支付,1=待发货,2=待收货,3=待评价,4=已完成,-3=售后/退款 -4=待处理\n" +
                    "condition: 查询条件 orderId/商品名称\n startDate、endDate: 查询时间区间", required = true)
    })
    public CommonResult<CommonPage<OrderDetailResponse>> orderList(@RequestBody Map<String, Object> params) {
        PageParamRequest pageRequest = new PageParamRequest();
        pageRequest.setLimit((Integer) params.get("limit"));
        pageRequest.setPage((Integer) params.get("page"));

        return CommonResult.success(orderService.list(params, pageRequest));
    }

    /**
     * 订单详情
     *
     * @param orderId 订单编号
     * @return 订单详情
     */
    @ApiOperation(value = "订单详情")
    @RequestMapping(value = "/detail/{orderId}", method = RequestMethod.GET)
    public CommonResult<StoreOrderDetailInfoResponse> orderDetail(@PathVariable String orderId) {
        return CommonResult.success(orderService.detailOrder(orderId));
    }

    /**
     * 订单头部信息
     *
     * @return 查询集合数量
     */
    @ApiOperation(value = "订单头部数量")
    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public CommonResult<OrderDataResponse> orderData() {
        return CommonResult.success(orderService.orderData());
    }

    /**
     * 删除已完成订单
     *
     * @param id String 订单号
     * @return 删除结果
     */
    @ApiOperation(value = "删除订单")
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    public CommonResult<Boolean> delete(@RequestParam Integer id) {
        if (orderService.delete(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 订单评价
     *
     * @param request StoreProductReplyAddRequest 评论参数
     */
    @ApiOperation(value = "评价订单")
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public CommonResult<Boolean> comment(@RequestBody @Validated StoreProductReplyAddRequest request) {
        if (orderService.reply(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 订单收货
     *
     * @param id Integer 订单id
     */
    @ApiOperation(value = "订单收货")
    @RequestMapping(value = "/take", method = RequestMethod.POST)
    public CommonResult<Boolean> take(@RequestParam(value = "id") Integer id) {
        if (orderService.take(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 订单取消
     *
     * @param id Integer 订单id
     */
    @ApiOperation(value = "订单取消")
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public CommonResult<Boolean> cancel(@RequestParam(value = "id") Integer id) {
        if (orderService.cancel(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 获取申请订单退款信息
     *
     * @param orderId 订单编号
     */
    @ApiOperation(value = "获取申请订单退款信息")
    @RequestMapping(value = "/apply/refund/{orderId}", method = RequestMethod.GET)

    public CommonResult<ApplyRefundOrderInfoResponse> refundApplyOrder(@PathVariable String orderId) {
        return CommonResult.success(orderService.applyRefundOrderInfo(orderId));
    }

    /**
     * 订单退款申请
     *
     * @param request OrderRefundApplyRequest 订单id
     */
    @ApiOperation(value = "订单退款申请")
    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    public CommonResult<Boolean> refundApply(@RequestBody @Validated OrderRefundApplyRequest request) {
        if (orderService.refundApply(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "退货填写物流单号")
    @RequestMapping(value = "/refund/trackingNo", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "trackingNo", value = "物流单号"),
            @ApiImplicitParam(name = "uni", value = "订单号")
    })
    public CommonResult<Boolean> refundTrackingNo(@RequestParam String trackingNo, @RequestParam String uni) {
        orderService.refundTrackingNo(trackingNo, uni);
        return CommonResult.success();
    }

    /**
     * 查询订单退款理由
     *
     * @return 退款理由
     */
    @ApiOperation(value = "订单退款理由（商家提供）")
    @RequestMapping(value = "/refund/reason", method = RequestMethod.GET)
    public CommonResult<List<String>> refundReason() {
        return CommonResult.success(orderService.getRefundReason());
    }

    /**
     * 根据订单号查询物流信息
     *
     * @param orderId 订单号
     * @return 物流信息
     */
    @ApiOperation(value = "物流信息查询")
    @RequestMapping(value = "/express/{orderId}", method = RequestMethod.GET)
    public CommonResult<Object> getExpressInfo(@PathVariable String orderId) {
        return CommonResult.success(orderService.expressOrder(orderId));
    }

    @ApiOperation(value = "待评价商品信息查询")
    @RequestMapping(value = "/product", method = RequestMethod.POST)
    public CommonResult<OrderProductReplyResponse> getOrderProductForReply(@Validated @RequestBody GetProductReply request) {
        return CommonResult.success(orderService.getReplyProduct(request));
    }

    /**
     * 获取支付配置
     */
    @ApiOperation(value = "获取支付配置")
    @RequestMapping(value = "get/pay/config", method = RequestMethod.GET)
    public CommonResult<PreOrderResponse> getPayConfig() {
        return CommonResult.success(orderService.getPayConfig());
    }

    @ApiOperation(value = "查询订单操作记录")
    @RequestMapping(value = "/operation/list", method = RequestMethod.GET)
    public CommonResult<List<Map<String, Object>>> refundList(@RequestParam Integer id) {
        return CommonResult.success(orderService.operationList(id));
    }


    @ApiOperation(value = "退货发货")
    @RequestMapping(value = "/refund/ship", method = RequestMethod.POST)
    public CommonResult<Boolean> returnShip(@RequestBody Map<String, Object> params) {
        Assert.notNull(params.get("orderId"), "订单编号不能为空");
        Assert.notNull(params.get("trackingNo"), "快递单号不能为空");

        return CommonResult.success(orderService.returnShip((String) params.get("orderId"), (String) params.get("trackingNo"), (String) params.get("remark"), (String) params.get("img")));
    }

    @ApiOperation(value = "撤销售后")
    @RequestMapping(value = "/refund/revoke", method = RequestMethod.GET)
    public CommonResult<String> refundRevoke(@RequestParam String orderId) {
        orderService.refundRevoke(orderId);
        return CommonResult.success();
    }

}
