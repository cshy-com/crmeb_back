package com.cshy.admin.controller.store;

import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.request.*;
import com.cshy.common.model.request.order.OrderRefundComputeRequest;
import com.cshy.common.model.request.store.*;
import com.cshy.common.model.response.*;
import com.cshy.common.model.vo.ExpressSheetVo;
import com.cshy.common.model.vo.LogisticsResultVo;
import com.cshy.service.service.order.OrderService;
import com.cshy.service.service.store.StoreOrderService;
import com.cshy.service.service.store.StoreOrderVerification;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/store/order")
@Api(tags = "订单") //配合swagger使用
public class StoreOrderController {

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderVerification storeOrderVerification;

    @Autowired
    private OrderService orderService;

    /**
     * 分页显示订单表
     *
     * @param request          搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:order:list')")
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreOrderDetailResponse>> getList(@Validated StoreOrderSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(storeOrderService.getAdminList(request, pageParamRequest));
    }

    /**
     * 获取订单各状态数量
     */
    @PreAuthorize("hasAuthority('admin:order:status:num')")
    @ApiOperation(value = "获取订单各状态数量")
    @RequestMapping(value = "/status/num", method = RequestMethod.GET)
    public CommonResult<StoreOrderCountItemResponse> getOrderStatusNum(
            @RequestParam(value = "dateLimit", defaultValue = "") String dateLimit,
            @RequestParam(value = "type", defaultValue = "2") @Range(min = 0, max = 2, message = "未知的订单类型") Integer type) {
        return CommonResult.success(storeOrderService.getOrderStatusNum(dateLimit, type));
    }

    /**
     * 获取订单统计数据
     */
    @PreAuthorize("hasAuthority('admin:order:list:data')")
    @ApiOperation(value = "获取订单统计数据")
    @RequestMapping(value = "/list/data", method = RequestMethod.GET)
    public CommonResult<StoreOrderTopItemResponse> getOrderData(@RequestParam(value = "dateLimit", defaultValue = "") String dateLimit) {
        return CommonResult.success(storeOrderService.getOrderData(dateLimit));
    }


    /**
     * 订单删除
     */
    @PreAuthorize("hasAuthority('admin:order:delete')")
    @ApiOperation(value = "订单删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "orderNo") String orderNo) {
        if (storeOrderService.delete(orderNo)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 备注订单
     */
    @PreAuthorize("hasAuthority('admin:order:mark')")
    @ApiOperation(value = "备注")
    @RequestMapping(value = "/mark", method = RequestMethod.POST)
    public CommonResult<String> mark(@RequestParam String orderNo, @RequestParam String mark) {
        if (storeOrderService.mark(orderNo, mark)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 修改订单(改价)
     */
    @PreAuthorize("hasAuthority('admin:order:update:price')")
    @ApiOperation(value = "修改订单(改价)")
    @RequestMapping(value = "/update/price", method = RequestMethod.POST)
    public CommonResult<String> updatePrice(@RequestBody @Validated StoreOrderUpdatePriceRequest request) {
        if (storeOrderService.updatePrice(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 订单详情
     */
    @PreAuthorize("hasAuthority('admin:order:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreOrderInfoResponse> info(@RequestParam(value = "orderNo") String orderNo) {
        return CommonResult.success(storeOrderService.info(orderNo));
    }

    /**
     * 退款
     */
    @PreAuthorize("hasAuthority('admin:order:refund')")
    @ApiOperation(value = "同意退款")
    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    public CommonResult<Boolean> refund(@RequestBody @Validated StoreOrderRefundRequest request) {
        return CommonResult.success(storeOrderService.refund(request));
    }

    @ApiOperation(value = "计算应退金额")
    @RequestMapping(value = "/refund/compute", method = RequestMethod.POST)
    public CommonResult<BigDecimal> refundCompute(@RequestBody OrderRefundComputeRequest orderRefundComputeRequest) {
        return CommonResult.success(orderService.refundCompute(orderRefundComputeRequest));
    }

    /**
     * 拒绝退款
     */
    @PreAuthorize("hasAuthority('admin:order:refund:refuse')")
    @ApiOperation(value = "拒绝退款")
    @RequestMapping(value = "/refund/refuse", method = RequestMethod.GET)
    public CommonResult<Object> refundRefuse(@RequestParam String orderNo, @RequestParam String reason, @RequestParam Integer type) {
        if (storeOrderService.refundRefuse(orderNo, reason, type)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('admin:order:statistics')")
    @ApiOperation(value = "核销订单头部数据")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public CommonResult<StoreStaffTopDetail> getStatistics() {
        return CommonResult.success(storeOrderVerification.getOrderVerificationData());
    }

    @PreAuthorize("hasAuthority('admin:order:statistics:data')")
    @ApiOperation(value = "核销订单 月列表数据")
    @RequestMapping(value = "/statisticsData", method = RequestMethod.GET)
    public CommonResult<List<StoreStaffDetail>> getStaffDetail(StoreOrderStaticsticsRequest request) {
        return CommonResult.success(storeOrderVerification.getOrderVerificationDetail(request));
    }


    @PreAuthorize("hasAuthority('admin:order:write:update')")
    @ApiOperation(value = "核销码核销订单")
    @RequestMapping(value = "/writeUpdate/{vCode}", method = RequestMethod.GET)
    public CommonResult<Object> verificationOrder(@PathVariable String vCode) {
        return CommonResult.success(storeOrderVerification.verificationOrderByCode(vCode));
    }

    @PreAuthorize("hasAuthority('admin:order:write:confirm')")
    @ApiOperation(value = "核销码查询待核销订单")
    @RequestMapping(value = "/writeConfirm/{vCode}", method = RequestMethod.GET)
    public CommonResult<Object> verificationConfirmOrder(
            @PathVariable String vCode) {
        return CommonResult.success(storeOrderVerification.getVerificationOrderByCode(vCode));
    }

    @PreAuthorize("hasAuthority('admin:order:time')")
    @ApiOperation(value = "订单统计详情")
    @RequestMapping(value = "/time", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateLimit", value = "today,yesterday,lately7,lately30,month,year,/yyyy-MM-dd hh:mm:ss,yyyy-MM-dd hh:mm:ss/",
                    dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "1=price 2=order", required = true)
    })
    public CommonResult<Object> statisticsOrderTime(@RequestParam String dateLimit,
                                                    @RequestParam Integer type) {
        return CommonResult.success(storeOrderService.orderStatisticsByTime(dateLimit, type));
    }

    /**
     * 获取面单默认配置信息
     */
    @PreAuthorize("hasAuthority('admin:order:sheet:info')")
    @ApiOperation(value = "获取面单默认配置信息")
    @RequestMapping(value = "/sheet/info", method = RequestMethod.GET)
    public CommonResult<ExpressSheetVo> getDeliveryInfo() {
        return CommonResult.success(storeOrderService.getDeliveryInfo());
    }

    @ApiOperation(value = "发货")
    @RequestMapping(value = "/ship", method = RequestMethod.POST)
    public CommonResult<String> ship(@RequestBody StoreOrderShipRequest storeOrderShipRequest, HttpServletRequest request) {
        orderService.ship(storeOrderShipRequest, request);
        return CommonResult.success();
    }

    @ApiOperation(value = "同意退货退款")
    @RequestMapping(value = "/refund/return", method = RequestMethod.GET)
    public CommonResult<Boolean> refundNReturn(@Validated StoreOrderRefundRequest request) {
        return CommonResult.success(storeOrderService.refundNReturn(request));
    }

    @PreAuthorize("hasAuthority('admin:order:refund')")
    @ApiOperation(value = "退货平台收货后退款")
    @RequestMapping(value = "/refund/arrived", method = RequestMethod.GET)
    public CommonResult<Boolean> returnArrived(@RequestParam String orderId) {
        return CommonResult.success(storeOrderService.returnArrived(orderId));
    }
}



