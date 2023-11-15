package com.cshy.admin.controller.sms;

import com.cshy.common.model.Type;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.entity.sms.SmsTemplate;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.sms.SmsTemplateQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.sms.SmsTemplateVo;
import com.cshy.service.service.SmsTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/admin/sms/template")
@Api(tags = "v2 -- 短信模板接口")
public class SmsTemplateController {
    @Autowired
    private SmsTemplateService smsTemplateService;

    @ApiOperation(value = "短信模板分页查询")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public CommonResult<CommonPage<SmsTemplateVo>> page(@RequestBody SmsTemplateQuery smsTemplateQuery, @Validated(Type.Page.class) @RequestBody BasePage basePage) {
        return CommonResult.success(smsTemplateService.page(smsTemplateQuery, basePage));
    }

    @ApiOperation(value = "同步短信模板")
    @RequestMapping(value = "/sync", method = RequestMethod.GET)
    public CommonResult<String> sync() throws Exception {
        smsTemplateService.sync();
        return CommonResult.success();
    }

    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="手机号"),
            @ApiImplicitParam(name="signId", value="签名id"),
            @ApiImplicitParam(name="triggerPosition", value="触发位置 0 发送验证码 1 下单成功通知客户 2 下单成功通知员工 3 发货后通知用户 4 退货到达通知 5 退款申请提交通知 6 退款申请通过通知")
    })
    public CommonResult<String> update(@RequestParam String id, @RequestParam Integer triggerPosition, @RequestParam String signId) {
        smsTemplateService.update(id, triggerPosition, signId);
        return CommonResult.success();
    }

    @ApiOperation(value = "查看详情")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public CommonResult<SmsTemplate> get(@PathVariable String id) {
        return CommonResult.success(smsTemplateService.getById(id));
    }
}
