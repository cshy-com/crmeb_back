package com.cshy.admin.controller.system;

import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.entity.system.SysDictType;
import com.cshy.common.model.response.CommonResult;
import com.cshy.service.service.system.ISysDictTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "v2 -- 数据字典类型", tags = "v2 -- 数据字典类型")
@RestController
@RequestMapping("api/admin/system/dict/type")
public class SysDictTypeController {
    @Autowired
    private ISysDictTypeService dictTypeService;

    @GetMapping("/list")
    @ApiOperation(value = "列表查询")
    public CommonResult<List<SysDictType>> list(SysDictType dictType) {
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return CommonResult.success(list);
    }

    /**
     * 查询字典类型详细
     */
    @GetMapping(value = "/{dictId}")
    @ApiOperation(value = "查询字典类型详细")
    public CommonResult<SysDictType> getInfo(@PathVariable String dictId) {
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
