package com.cshy.admin.controller;

import com.cshy.common.model.response.CommonResult;
import com.cshy.service.service.YlyPrintService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 易联云打印订单

 */
@Slf4j
@RestController
@RequestMapping("api/admin/yly")
@Api(tags = "易联云 打印订单小票") //配合swagger使用
public class YlyPrintController {


    @Autowired
    private YlyPrintService ylyPrintService;

    @PreAuthorize("hasAuthority('admin:yly:print')")
    @ApiOperation(value = "打印小票")
    @RequestMapping(value = "/print/{ordid}", method = RequestMethod.GET)
    public CommonResult<String> updateStatus(@PathVariable  String ordid) {
        ylyPrintService.YlyPrint(ordid,false);
        return CommonResult.success();
    }
}
