package com.cshy.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.cshy.common.model.entity.sms.SmsRecord;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.request.SmsRecordsRequest;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.response.StoreOrderDetailResponse;
import com.cshy.common.model.vo.MyRecord;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.request.SmsApplyTempRequest;
import com.cshy.common.model.request.SmsModifySignRequest;
import com.cshy.service.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 短信发送记录表 前端控制器

 */
@Slf4j
@RestController
@RequestMapping("api/admin/sms")
@Api(tags = "短信服务")
public class SmsRecordController {

    @Autowired
    private SmsService smsService;

    /**
     * 修改签名
     */
    @PreAuthorize("hasAuthority('admin:sms:modify:sign')")
    @ApiOperation(value = "修改签名")
    @RequestMapping(value = "/modify/sign", method = RequestMethod.POST)
    public CommonResult<JSONObject> modifySign(@RequestBody @Validated SmsModifySignRequest request) {
        if (smsService.modifySign(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 短信模板
     */
    @PreAuthorize("hasAuthority('admin:sms:temps')")
    @ApiOperation(value = "短信模板")
    @RequestMapping(value = "/temps", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> temps(@ModelAttribute PageParamRequest pageParamRequest) {
        MyRecord myRecord = smsService.temps(pageParamRequest);
        return CommonResult.success(myRecord);
    }

    /**
     * 申请短信模板
     */
    @PreAuthorize("hasAuthority('admin:sms:temp:apply')")
    @ApiOperation(value = "申请短信模板")
    @RequestMapping(value = "/temp/apply", method = RequestMethod.POST)
    public CommonResult<JSONObject> applyTempMessage(@RequestBody @Validated SmsApplyTempRequest request) {
        if (smsService.applyTempMessage(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 模板申请记录
     */
    @PreAuthorize("hasAuthority('admin:sms:applys')")
    @ApiOperation(value = "模板申请记录")
    @RequestMapping(value = "/applys", method = RequestMethod.POST)
    @ApiImplicitParam(name="type", value="type (1=验证码 2=通知 3=推广)")
    public CommonResult<Map<String, Object>> applys(@RequestParam(name = "type", required = false) Integer type, @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(smsService.applys(type, pageParamRequest));
    }

    /**
     * 模板申请记录
     */
    @PreAuthorize("hasAuthority('admin:sms:applys')")
    @ApiOperation(value = "短信发送记录分页")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<SmsRecord>> page(@Validated SmsRecordsRequest smsRecordsRequest, @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(smsService.page(smsRecordsRequest, pageParamRequest));
    }

}


