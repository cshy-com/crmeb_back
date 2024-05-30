package com.cshy.front.controller.coupon;

import com.cshy.common.model.response.CommonResult;
import com.cshy.common.token.FrontTokenComponent;
import com.cshy.common.utils.CrmebUtil;
import com.cshy.service.service.store.StoreIntegralCouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/front/integral/coupon")
@Api(tags = "v2 -- 积分券接口")
public class StoreIntegralCouponController {
    @Autowired
    private StoreIntegralCouponService storeIntegralCouponService;

    @Autowired
    private FrontTokenComponent tokenComponent;

    @ApiOperation("积分入账")
    @GetMapping("/add")
    public CommonResult<?> add(@RequestParam String code) {
        Integer userId = tokenComponent.getUserId();

        storeIntegralCouponService.addIntegral(code, userId);

        return CommonResult.success();
    }

    @ApiOperation("是否被使用")
    @GetMapping("/isUsed")
    public CommonResult<Boolean> isUsed(@RequestParam String code) {

        Boolean used = storeIntegralCouponService.isUsed(code);

        return CommonResult.success(used);
    }

}
