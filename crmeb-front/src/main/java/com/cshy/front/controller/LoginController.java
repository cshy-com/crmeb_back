package com.cshy.front.controller;


import com.cshy.common.enums.SmsTriggerEnum;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.request.LoginMobileRequest;
import com.cshy.common.model.request.LoginRequest;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.response.LoginResponse;
import com.cshy.common.utils.StringUtils;
import com.cshy.front.service.LoginService;
import com.cshy.service.service.SmsService;
import com.cshy.service.service.giftCard.GiftCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户登陆 前端控制器
 */
@Slf4j
@RestController("FrontLoginController")
@RequestMapping("api/front")
@Api(tags = "用户 -- 登录注册")
public class LoginController {

    @Autowired
    private SmsService smsService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private GiftCardService giftCardService;

    /**
     * 手机号登录接口
     */
    @ApiOperation(value = "手机号登录接口")
    @RequestMapping(value = "/login/mobile", method = RequestMethod.POST)
    public CommonResult<LoginResponse> phoneLogin(@RequestBody @Validated LoginMobileRequest loginRequest) {
        //兑换礼品卡时候校验提货密码
        if (StringUtils.isNotBlank(loginRequest.getPickupCode()) && StringUtils.isNotBlank(loginRequest.getPickupSecret())){
            boolean checkSecret = giftCardService.checkSecret(loginRequest.getPickupCode(), loginRequest.getPickupSecret());
            if (!checkSecret)
                throw new CrmebException("提货密码错误，请重试");
        }

        return CommonResult.success(loginService.phoneLogin(loginRequest));
    }

    /**
     * 账号密码登录
     */
    @ApiOperation(value = "账号密码登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public CommonResult<LoginResponse> login(@RequestBody @Validated LoginRequest loginRequest) {
        throw new CrmebException("暂不支持账号密码登录");
//        return CommonResult.success(loginService.login(loginRequest));
    }


    /**
     * 退出登录
     */
    @ApiOperation(value = "退出")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public CommonResult<String> loginOut(HttpServletRequest request) {
        loginService.loginOut(request);
        return CommonResult.success();
    }

    /**
     * 发送短信登录验证码
     *
     * @param phone 手机号码
     * @return 发送是否成功
     */
    @ApiOperation(value = "发送短信登录验证码")
    @RequestMapping(value = "/sendCode", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号码", required = true)
    })
    public CommonResult<Object> sendCode(@RequestParam String phone, HttpServletRequest request) {
        smsService.sendCode(phone, SmsTriggerEnum.VERIFICATION_CODE.getCode(), request, null);
        return CommonResult.success("发送成功");
    }
}



