package com.cshy.admin.controller.sms;

import com.cshy.common.model.Type;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.entity.sms.SmsSign;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.sms.SmsSignQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.sms.SmsSignVo;
import com.cshy.service.service.sms.SmsSignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/admin/sms/sign")
@Api(tags = "v2 -- 短信签名接口")
public class SmsSignController {
    @Autowired
    private SmsSignService smsSignService;

    @ApiOperation(value = "短信签名分页查询")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public CommonResult<CommonPage<SmsSignVo>> page(@RequestBody SmsSignQuery smsTemplateQuery, @Validated(Type.Page.class) @RequestBody BasePage basePage) {
        return CommonResult.success(smsSignService.page(smsTemplateQuery, basePage));
    }

    @ApiOperation(value = "同步短信签名")
    @RequestMapping(value = "/sync", method = RequestMethod.GET)
    public CommonResult<String> sync() throws Exception {
        smsSignService.sync();
        return CommonResult.success();
    }

    @ApiOperation(value = "获取所有短信签名")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<SmsSign>> list() {
        return CommonResult.success(smsSignService.list());
    }
}
