package com.cshy.front.controller.system;

import com.cshy.common.model.Type;
import com.cshy.common.model.dto.system.SysFaqDto;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.system.SysFaqQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.system.SysFaqVo;
import com.cshy.service.service.system.SysFaqService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "v2 -- 常见问题", tags = "v2 -- 常见问题")
@RestController
@AllArgsConstructor
@RequestMapping("api/front/faq")
public class SysFaqController {
    @Autowired
    private SysFaqService sysFaqService;

    @ApiOperation("修改")
    @PutMapping("/update")
    public CommonResult<?> update(@RequestBody SysFaqDto dto) {
        sysFaqService.update(dto);
        return CommonResult.success();
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete/{id}")
    public CommonResult<?> delete(@PathVariable String id) {
        sysFaqService.delete(id);
        return CommonResult.success();
    }

    @ApiOperation("查一个")
    @GetMapping("/obj/{id}")
    public CommonResult<SysFaqVo> obj(@PathVariable String id) {
        SysFaqVo obj = sysFaqService.obj(id);
        return CommonResult.success(obj);
    }

    @ApiOperation("查列表")
    @PostMapping("/list")
    public CommonResult<List<SysFaqVo>> list(@RequestBody SysFaqQuery query) {
        List<SysFaqVo> list = sysFaqService.list(query);
        return CommonResult.success(list);
    }

    @ApiOperation("查分页")
    @PostMapping("/page")
    public CommonResult<CommonPage<SysFaqVo>> page(@RequestBody SysFaqQuery query, @Validated(Type.Page.class) @RequestBody BasePage basePage) {
        CommonPage<SysFaqVo> page = sysFaqService.page(query, basePage);
        return CommonResult.success(page);
    }

    @ApiOperation("查询分类")
    @PostMapping("/category/page")
    public CommonResult<CommonPage<String>> categoryPage(@RequestBody BasePage basePage) {
        CommonPage<String> page = sysFaqService.categoryPage(basePage);
        return CommonResult.success(page);
    }
}
