package com.cshy.admin.controller.sms;

import com.cshy.common.model.entity.sms.SmsRecord;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.request.sms.SmsRecordsRequest;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.service.service.sms.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 短信发送记录表 前端控制器

 */
@Slf4j
@RestController
@RequestMapping("api/admin/sms")
@Api(tags = "v2 -- 短信服务")
public class SmsRecordController {

    @Autowired
    private SmsService smsService;

    /**
     * 短信发送记录分页
     */
    @ApiOperation(value = "短信发送记录分页")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<SmsRecord>> page(@Validated SmsRecordsRequest smsRecordsRequest, @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(smsService.page(smsRecordsRequest, pageParamRequest));
    }

}


