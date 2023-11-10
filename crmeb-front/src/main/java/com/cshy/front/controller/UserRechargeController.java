package com.cshy.front.controller;

import com.cshy.common.constants.Constants;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.request.UserRechargeRequest;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.response.OrderPayResultResponse;
import com.cshy.common.model.response.UserRechargeBillRecordResponse;
import com.cshy.common.model.response.UserRechargeFrontResponse;
import com.cshy.common.utils.CrmebUtil;
import com.cshy.front.service.UserCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户 -- 充值
 
 */
@Slf4j
@RestController("UserRechargeController")
@RequestMapping("api/front/recharge")
@Api(tags = "用户 -- 充值")
public class UserRechargeController {
    @Autowired
    private UserCenterService userCenterService;

    /**
     * 充值额度选择
     */
    @ApiOperation(value = "充值额度选择")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public CommonResult<UserRechargeFrontResponse> getRechargeConfig() {
        return CommonResult.success(userCenterService.getRechargeConfig());
    }

    /**
     * 小程序充值
     */
    @ApiOperation(value = "小程序充值")
    @RequestMapping(value = "/routine", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> routineRecharge(HttpServletRequest httpServletRequest, @RequestBody @Validated UserRechargeRequest request) {
        request.setFromType(Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM);
        request.setClientIp(CrmebUtil.getClientIp(httpServletRequest));
        OrderPayResultResponse recharge = userCenterService.recharge(request);
        Map<String, Object> map = new HashMap<>();
        map.put("data", recharge);
        map.put("type", request.getFromType());
        return CommonResult.success(map);
    }

    /**
     * 公众号充值
     */
    @ApiOperation(value = "公众号充值")
    @RequestMapping(value = "/wechat", method = RequestMethod.POST)
    public CommonResult<OrderPayResultResponse> weChatRecharge(HttpServletRequest httpServletRequest, @RequestBody @Validated UserRechargeRequest request) {
        request.setClientIp(CrmebUtil.getClientIp(httpServletRequest));
        return CommonResult.success(userCenterService.recharge(request));
    }

    /**
     * 佣金转入余额
     */
    @ApiOperation(value = "佣金转入余额")
    @RequestMapping(value = "/transferIn", method = RequestMethod.POST)
    public CommonResult<Boolean> transferIn(@RequestParam(name = "price") BigDecimal price) {
        return CommonResult.success(userCenterService.transferIn(price));
    }

    /**
     * 用户账单记录
     */
    @ApiOperation(value = "用户账单记录")
    @RequestMapping(value = "/bill/record", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "记录类型：all-全部，expenditure-支出，income-收入", required = true)
    public CommonResult<CommonPage<UserRechargeBillRecordResponse>> billRecord(@RequestParam(name = "type") String type, @ModelAttribute PageParamRequest pageRequest) {
        return CommonResult.success(userCenterService.nowMoneyBillRecord(type, pageRequest));
    }
}



