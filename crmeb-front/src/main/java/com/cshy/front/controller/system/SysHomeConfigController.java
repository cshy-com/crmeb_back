package com.cshy.front.controller.system;

import com.cshy.common.model.query.system.SysHomeConfigQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.system.SysHomeConfigVo;
import com.cshy.service.service.system.SysHomeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "v2 -- 首页功能配置", tags = "v2 -- 首页功能配置")
@RestController
@AllArgsConstructor
@RequestMapping("api/front/sys/home/config")
public class SysHomeConfigController {
    @Resource
    private final SysHomeConfigService sysHomeConfigService;

    @ApiOperation("查列表")
    @PostMapping("/list")
    public CommonResult<List<SysHomeConfigVo>> list(@RequestBody SysHomeConfigQuery query) {
        List<SysHomeConfigVo> list = sysHomeConfigService.list(query);
        return CommonResult.success(list);
    }
}
