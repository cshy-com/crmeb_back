package com.cshy.admin.controller.coupon;

import com.cshy.common.model.Type;
import com.cshy.common.model.dto.coupon.StoreIntegralCouponDto;
import com.cshy.common.model.dto.coupon.StoreIntegralCouponListDto;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.coupon.StoreIntegralCouponQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.coupon.StoreIntegralCouponVo;
import com.cshy.service.service.store.StoreIntegralCouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.groups.Default;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/admin/integral/coupon")
@Api(tags = "v2 -- 积分券接口")
public class StoreIntegralCouponController {
    private final StoreIntegralCouponService storeIntegralCouponService;

    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public CommonResult<CommonPage<StoreIntegralCouponVo>> page(@RequestBody StoreIntegralCouponQuery query, @Validated(Type.Page.class) @RequestBody BasePage basePage) {
        CommonPage<StoreIntegralCouponVo> page = storeIntegralCouponService.page(query, basePage);
        return CommonResult.success(page);
    }

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody StoreIntegralCouponDto dto) {
        return CommonResult.success(storeIntegralCouponService.add(dto));
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiImplicitParam(name = "id", value = "活动ID")
    public CommonResult<String> delete(@RequestParam(value = "id") String id) {
        storeIntegralCouponService.delete(id);
        return CommonResult.success();
    }

    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiImplicitParam(name = "StoreIntegralCouponDto", value = "StoreIntegralCouponDto")
    public CommonResult<String> update(@RequestBody StoreIntegralCouponDto StoreIntegralCouponDto) {
        storeIntegralCouponService.update(StoreIntegralCouponDto);
        return CommonResult.success();
    }

    @ApiOperation("查一个")
    @GetMapping("/obj/{id}")
    public CommonResult<StoreIntegralCouponVo> obj(@PathVariable String id) {
        StoreIntegralCouponVo obj = storeIntegralCouponService.obj(id);
        return CommonResult.success(obj);
    }

    @ApiOperation("查列表")
    @PostMapping("/list")
    public CommonResult<List<StoreIntegralCouponVo>> list(@RequestBody StoreIntegralCouponQuery query) {
        List<StoreIntegralCouponVo> list = storeIntegralCouponService.list(query);
        return CommonResult.success(list);
    }

    @ApiOperation("批量新增")
    @PostMapping("/addList")
    public CommonResult<?> addList(@Validated({Default.class}) @RequestBody StoreIntegralCouponListDto dto) {
        storeIntegralCouponService.addList(dto);
        return CommonResult.success();
    }

    @ApiOperation(value = "批量导出",produces = 	MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PostMapping("/export")
    public void exportList(@RequestBody StoreIntegralCouponQuery query, HttpServletResponse response) throws Exception {
        storeIntegralCouponService.export(query, response);
    }
}