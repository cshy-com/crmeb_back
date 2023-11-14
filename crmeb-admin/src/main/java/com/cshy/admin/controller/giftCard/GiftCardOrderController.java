package com.cshy.admin.controller.giftCard;

import com.cshy.common.model.Type;
import com.cshy.common.model.dto.giftCard.GiftCardOrderDto;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.giftCard.GiftCardOrderQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.giftCard.GiftCardOrderVo;
import com.cshy.service.service.SmsService;
import com.cshy.service.service.giftCard.GiftCardOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/admin/giftCard/order")
@Api(tags = "v2 -- 礼品卡订单")
public class GiftCardOrderController {
    @Autowired
    private GiftCardOrderService giftCardOrderService;
    @Autowired
    private SmsService smsService;

    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public CommonResult<CommonPage<GiftCardOrderVo>> page(@RequestBody GiftCardOrderQuery query, @Validated(Type.Page.class) @RequestBody BasePage basePage) {
        CommonPage<GiftCardOrderVo> page = giftCardOrderService.page(query, basePage);
        return CommonResult.success(page);
    }

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody GiftCardOrderDto dto) {
        return CommonResult.success(giftCardOrderService.add(dto));
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiImplicitParam(name = "id", value = "活动ID")
    public CommonResult<String> delete(@RequestParam(value = "id") String id) {
        giftCardOrderService.delete(id);
        return CommonResult.success();
    }


    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiImplicitParam(name = "id", value = "文章ID")
    public CommonResult<String> update(@RequestBody GiftCardOrderDto giftCardOrderDto) {
        giftCardOrderService.update(giftCardOrderDto);
        return CommonResult.success();
    }

    @ApiOperation("查一个")
    @GetMapping("/obj/{id}")
    public CommonResult<GiftCardOrderVo> obj(@PathVariable String id) {
        GiftCardOrderVo obj = giftCardOrderService.obj(id);
        return CommonResult.success(obj);
    }

    @ApiOperation("查列表")
    @PostMapping("/list")
    public CommonResult<List<GiftCardOrderVo>> list(@RequestBody GiftCardOrderQuery query) {
        List<GiftCardOrderVo> list = giftCardOrderService.list(query);
        return CommonResult.success(list);
    }

    @ApiOperation(value = "获取订单各状态数量")
    @RequestMapping(value = "/status/num", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> getOrderStatusNum() {
        return CommonResult.success(giftCardOrderService.getOrderStatusNum());
    }

    @ApiOperation(value = "发货")
    @RequestMapping(value = "/ship", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "0 普通订单 1 礼品卡订单"),
            @ApiImplicitParam(name = "orderId", value = "订单号"),
            @ApiImplicitParam(name = "trackingNo", value = "物流单号")
    })
    public CommonResult<String> ship(@RequestParam String orderId, @RequestParam String trackingNo, @RequestParam Integer type, HttpServletRequest request) {
        giftCardOrderService.ship(orderId, trackingNo, type, request);
        return CommonResult.success();
    }
}
