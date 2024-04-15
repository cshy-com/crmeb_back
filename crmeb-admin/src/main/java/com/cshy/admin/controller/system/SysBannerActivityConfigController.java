package com.cshy.admin.controller.system;

import com.cshy.common.model.Type;
import com.cshy.common.model.dto.system.SysBannerActivityConfigDto;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.system.SysBannerActivityConfigQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.system.SysBannerActivityConfigVo;
import com.cshy.service.service.system.SysBannerActivityConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(value = "v2 -- 首页banner位活动配置", tags = "v2 -- 首页banner位活动配置")
@RestController
@AllArgsConstructor
@RequestMapping("api/admin/sys/banner/activity/config")
public class SysBannerActivityConfigController {
    @Resource
    private final SysBannerActivityConfigService sysBannerActivityConfigService;

    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public CommonResult<CommonPage<SysBannerActivityConfigVo>> page(@RequestBody SysBannerActivityConfigQuery query, @Validated(Type.Page.class) @RequestBody BasePage basePage) {
        CommonPage<SysBannerActivityConfigVo> page = sysBannerActivityConfigService.page(query, basePage);
        return CommonResult.success(page);
    }

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody SysBannerActivityConfigDto dto) {
        return CommonResult.success(sysBannerActivityConfigService.add(dto));
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiImplicitParam(name = "id", value = "活动ID")
    public CommonResult<String> delete(@RequestParam(value = "id") String id) {
        sysBannerActivityConfigService.delete(id);
        return CommonResult.success();
    }


    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiImplicitParam(name = "id", value = "文章ID")
    public CommonResult<String> update(@RequestBody SysBannerActivityConfigDto sysBannerActivityConfigDto) {
        sysBannerActivityConfigService.update(sysBannerActivityConfigDto);
        return CommonResult.success();
    }

    @ApiOperation("查一个")
    @GetMapping("/obj/{id}")
    public CommonResult<SysBannerActivityConfigVo> obj(@PathVariable String id) {
        SysBannerActivityConfigVo obj = sysBannerActivityConfigService.obj(id);
        return CommonResult.success(obj);
    }
}
