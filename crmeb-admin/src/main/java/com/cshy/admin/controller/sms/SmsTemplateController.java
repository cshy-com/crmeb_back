package com.cshy.admin.controller.sms;

import com.cshy.common.model.Type;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.sms.SmsTemplateQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.sms.SmsTemplateVo;
import com.cshy.service.service.SmsTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/admin/sms/template")
@Api(tags = "短信模板接口")
public class SmsTemplateController {
    @Autowired
    private SmsTemplateService smsTemplateService;

    @ApiOperation(value = "短信模板分页查询")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public CommonResult<CommonPage<SmsTemplateVo>> page(@RequestBody SmsTemplateQuery smsTemplateQuery, @Validated(Type.Page.class) @RequestBody BasePage basePage) {
        return CommonResult.success(smsTemplateService.page(smsTemplateQuery, basePage));
    }

    @ApiOperation(value = "短信模板分页查询")
    @RequestMapping(value = "/sync", method = RequestMethod.GET)
    public CommonResult<String> sync() throws Exception {
        smsTemplateService.sync();
        return CommonResult.success();
    }
}
