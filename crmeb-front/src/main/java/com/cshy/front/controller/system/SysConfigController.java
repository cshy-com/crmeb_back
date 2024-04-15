package com.cshy.front.controller.system;

import com.cshy.common.model.response.CommonResult;
import com.cshy.service.service.system.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/front/system/config")
@Api(tags = "设置 -- Config")
public class SysConfigController {
    @Autowired
    private SystemConfigService systemConfigService;

    @ApiOperation(value = "表单配置根据key获取")
    @RequestMapping(value = "/getuniq", method = RequestMethod.GET)
    public CommonResult<Object> justGetUniq(@RequestParam String key) {
        return CommonResult.success(systemConfigService.getValueByKey(key),"success");
    }
}
