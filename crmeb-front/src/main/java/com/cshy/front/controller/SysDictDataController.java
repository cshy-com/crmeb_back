package com.cshy.front.controller;

import com.cshy.common.model.entity.system.SysDictData;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.utils.StringUtils;
import com.cshy.service.service.system.ISysDictDataService;
import com.cshy.service.service.system.ISysDictTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典信息
 */
@RestController
@Api(value = "v2 -- 数据字典数据", tags = "v2 -- 数据字典类型")
@RequestMapping("api/front/system/dict/data")
public class SysDictDataController
{
    @Autowired
    private ISysDictDataService dictDataService;

    @Autowired
    private ISysDictTypeService dictTypeService;

    @GetMapping("/list")
    @ApiOperation(value = "列表查询")
    public CommonResult<List<SysDictData>> list(SysDictData dictData)
    {
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return CommonResult.success(list);
    }

    /**
     * 查询字典数据详细
     */
    @GetMapping(value = "/{dictCode}")
    @ApiOperation(value = "查询字典数据详细")
    public CommonResult<SysDictData> getInfo(@PathVariable Integer dictCode)
    {
        return CommonResult.success(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    @ApiOperation(value = "根据字典类型查询字典数据信息")
    public CommonResult<List<SysDictData>> dictType(@PathVariable String dictType)
    {
        List<SysDictData> data = dictTypeService.selectDictDataByType(dictType);
        if (StringUtils.isNull(data))
        {
            data = new ArrayList<SysDictData>();
        }
        return CommonResult.success(data);
    }
}
