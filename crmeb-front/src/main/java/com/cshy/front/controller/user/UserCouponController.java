package com.cshy.front.controller.user;

import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.request.user.UserCouponReceiveRequest;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.response.StoreCouponUserResponse;
import com.cshy.service.service.store.StoreCouponUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 优惠卷控制器

 */
@Slf4j
@RestController
@RequestMapping("api/front/coupon")
@Api(tags = "营销 -- 优惠券")
public class UserCouponController {

    @Autowired
    private StoreCouponUserService storeCouponUserService;

    /**
     * 我的优惠券
     */
    @ApiOperation(value = "我的优惠券")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="type", value="类型，usable-可用，unusable-不可用", required = true),
            @ApiImplicitParam(name="page", value="页码", required = true),
            @ApiImplicitParam(name="limit", value="每页数量", required = true)
    })
    public CommonResult<CommonPage<StoreCouponUserResponse>> getList(@RequestParam(value = "type") String type,
                                                                     @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(storeCouponUserService.getMyCouponList(type, pageParamRequest));
    }

    /**
     * 领券
     * @param request UserCouponReceiveRequest 新增参数
     */
    @ApiOperation(value = "领券")
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    public CommonResult<String> receive(@RequestBody @Validated UserCouponReceiveRequest request) {
        if (storeCouponUserService.receiveCoupon(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

}



