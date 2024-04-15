package com.cshy.admin.controller.wechat;

import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.response.WeChatJsSdkConfigResponse;
import com.cshy.service.service.wechat.WechatCommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信 -- 开放平台 admin

 */
@Slf4j
@RestController("WeChatAdminController")
@RequestMapping("api/admin/wechat")
@Api(tags = "微信 -- 开放平台 admin")
public class WeChatAdminController {

    @Autowired
    private WechatCommonService wechatCommonService;

    /**
     * 获取微信公众号js配置
     */
    @PreAuthorize("hasAuthority('admin:wechat:config')")
    @ApiOperation(value = "获取微信公众号js配置")
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    @ApiImplicitParam(name = "url", value = "页面地址url")
    public CommonResult<WeChatJsSdkConfigResponse> configJs(@RequestParam(value = "url") String url) {
        return CommonResult.success(wechatCommonService.getJsSdkConfig(url));
    }

    @PreAuthorize("hasRole('admin')")
    @ApiOperation(value = "获取微信公众号accessToken")
    @RequestMapping(value = "/accessToken", method = RequestMethod.GET)
    public CommonResult<String> accessToken() {
        String publicAccessToken = wechatCommonService.getPublicAccessToken();
        return CommonResult.success(publicAccessToken);
    }
}
