package com.cshy.admin.controller.system;

import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.entity.system.SysDictType;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.response.CommonResult;
import com.cshy.service.service.system.ISysDictTypeService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "v2 -- 数据字典类型", tags = "v2 -- 数据字典类型")
@RestController
@RequestMapping("api/admin/system/dict/type")
public class SysDictTypeController {
    @Autowired
    private ISysDictTypeService dictTypeService;

    @ApiOperation(value = "新增字典类型")
    @PostMapping("/add")
    public CommonResult<String> add(@Validated @RequestBody SysDictType dict)
    {
        if (!dictTypeService.checkDictTypeUnique(dict))
        {
            return CommonResult.failed("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        int i = dictTypeService.insertDictType(dict);
        return CommonResult.success();
    }

    /**
     * 修改字典类型
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改字典类型")
    public CommonResult<String> edit(@Validated @RequestBody SysDictType dict)
    {
        if (!dictTypeService.checkDictTypeUnique(dict))
        {
            return CommonResult.failed("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dictTypeService.updateDictType(dict);
        return CommonResult.success();
    }

    @ApiOperation(value = "删除字典类型")
    @DeleteMapping("/{dictIds}")
    public  CommonResult<String> remove(@PathVariable Integer[] dictIds)
    {
        dictTypeService.deleteDictTypeByIds(dictIds);
        return CommonResult.success();
    }

    @GetMapping("/list")
    @ApiOperation(value = "列表查询")
    public CommonResult<List<SysDictType>> list(SysDictType dictType) {
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return CommonResult.success(list);
    }

    @PostMapping("/page")
    @ApiOperation(value = "分页查询")
    public CommonResult<CommonPage<SysDictType>> page(@RequestBody SysDictType dictType, @ModelAttribute BasePage basePage) {
        PageInfo<SysDictType> list = dictTypeService.selectDictTypePage(dictType, basePage);
        CommonPage<SysDictType> sysDictTypeCommonPage = CommonPage.restPage(list);
        return CommonResult.success(sysDictTypeCommonPage);
    }

    /**
     * 查询字典类型详细
     */
    @GetMapping(value = "/{dictId}")
    @ApiOperation(value = "查询字典类型详细")
    public CommonResult<SysDictType> getInfo(@PathVariable Integer dictId) {
        return CommonResult.success(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 刷新字典缓存
     */
    @DeleteMapping("/refreshCache")
    @ApiOperation(value = "刷新字典缓存")
    public CommonResult<String> refreshCache() {
        dictTypeService.resetDictCache();
        return CommonResult.success();
    }

    /**
     * 获取字典选择框列表
     */
    @GetMapping("/option/select")
    @ApiOperation(value = "获取字典选择框列表")
    public CommonResult<List<SysDictType>> optionSelect() {
        List<SysDictType> dictTypes = dictTypeService.selectDictTypeAll();
        return CommonResult.success(dictTypes);
    }
}
