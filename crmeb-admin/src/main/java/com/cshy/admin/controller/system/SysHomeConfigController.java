package com.cshy.admin.controller.system;

import com.cshy.common.model.Type;
import com.cshy.common.model.dto.system.SysHomeConfigDto;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.system.SysHomeConfigQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.system.SysHomeConfigVo;
import com.cshy.service.service.system.SysHomeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "v2 -- 首页功能配置", tags = "v2 -- 首页功能配置")
@RestController
@AllArgsConstructor
@RequestMapping("api/admin/sys/home/config")
public class SysHomeConfigController {
    @Resource
    private final SysHomeConfigService sysHomeConfigService;

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody SysHomeConfigDto dto) {
        return CommonResult.success(sysHomeConfigService.add(dto));
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiImplicitParam(name = "id", value = "活动ID")
    public CommonResult<String> delete(@RequestParam(value = "id") String id) {
        sysHomeConfigService.delete(id);
        return CommonResult.success();
    }


    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody List<SysHomeConfigDto> sysHomeConfigDtoList) {
        sysHomeConfigService.updateAll(sysHomeConfigDtoList);
        return CommonResult.success();
    }

    @ApiOperation("查一个")
    @GetMapping("/obj/{id}")
    public CommonResult<SysHomeConfigVo> obj(@PathVariable String id) {
        SysHomeConfigVo obj = sysHomeConfigService.obj(id);
        return CommonResult.success(obj);
    }

    @ApiOperation("查列表")
    @PostMapping("/list")
    public CommonResult<List<SysHomeConfigVo>> list(@RequestBody SysHomeConfigQuery query) {
        List<SysHomeConfigVo> list = sysHomeConfigService.list(query);
        return CommonResult.success(list);
    }
}
