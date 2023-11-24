package com.cshy.front.controller;

import com.cshy.common.model.response.CommonResult;
import com.cshy.service.service.order.ShortUrlService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/front/url/shortener")
@Api(tags = "v2 -- 短连接转换接口")
public class ShortUrlController {
    @Autowired
    private ShortUrlService shortUrlService;

    @ApiOperation(value = "短连接转换成长连接")
    @RequestMapping(value = "/expand", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="url", value="长连接"),
            @ApiImplicitParam(name="location", value="0 普通订单 1 礼品卡订单"),
    })
    public CommonResult<String> expand(@RequestParam String url, @RequestParam Integer location) {
        String expandUrl = shortUrlService.expandUrl(url, location);
        return CommonResult.success(expandUrl);
    }

    @ApiOperation(value = "短连接转换成长连接")
    @RequestMapping(value = "/shorten", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="param", value="需要转换的数据"),
            @ApiImplicitParam(name="location", value="0 普通订单 1 礼品卡订单"),
    })
    public CommonResult<String> shorten(@RequestParam String param, @RequestParam Integer location) {
        String shortenURL = shortUrlService.shortenURL(param, location);
        return CommonResult.success(shortenURL);
    }
}