package com.cshy.admin.controller.system;

import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.entity.system.SystemConfig;
import com.cshy.common.model.request.system.SystemConfigAdminRequest;
import com.cshy.common.model.request.system.SystemFormCheckRequest;
import com.cshy.service.service.system.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


/**
 * 配置表 前端控制器

 */
@Slf4j
@RestController
@RequestMapping("api/admin/system/config")
@Api(tags = "设置 -- Config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 查询配置表信息
     * @param formId Integer
     */
    @PreAuthorize("hasAuthority('admin:system:config:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> info(@RequestParam(value = "formId") Integer formId) {
        return CommonResult.success(systemConfigService.info(formId));
    }


    /**
     * 整体保存表单数据
     * @param systemFormCheckRequest SystemFormCheckRequest 新增参数
     */
    @PreAuthorize("hasAuthority('admin:system:config:save:form')")
    @ApiOperation(value = "整体保存表单数据")
    @RequestMapping(value = "/save/form", method = RequestMethod.POST)
    public CommonResult<String> saveFrom(@RequestBody @Validated SystemFormCheckRequest systemFormCheckRequest) {
        if (systemConfigService.saveForm(systemFormCheckRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 配置表中仅仅存储对应的配置
     * @param key 配置表中的配置字段
     * @param value 对应的值
     */
    @PreAuthorize("hasAuthority('admin:system:config:saveuniq')")
    @ApiOperation(value = "表单配置中仅仅存储")
    @RequestMapping(value = "/saveuniq", method = RequestMethod.POST)
    public CommonResult<Boolean> justSaveUniq(@RequestParam String key, @RequestParam String value) {
        return CommonResult.success(systemConfigService.updateOrSaveValueByName(key, value));
    }

    /**
     * 根据key获取表单配置数据
     * @param key 配置表的的字段
     */
    @PreAuthorize("hasAuthority('admin:system:config:getuniq')")
    @ApiOperation(value = "表单配置根据key获取")
    @RequestMapping(value = "/getuniq", method = RequestMethod.GET)
    public CommonResult<Object> justGetUniq(@RequestParam String key) {
        return CommonResult.success(systemConfigService.getValueByKey(key),"success");
    }

    /**
     * 同步缓存
     */
    @ApiOperation(value = "同步缓存")
    @RequestMapping(value = "/async", method = RequestMethod.GET)
    public CommonResult<Object> async() {
        systemConfigService.resetConfigCache();
        return CommonResult.success("success");
    }

    @ApiOperation(value = "获取实体表")
    @RequestMapping(value = "/modelNameList", method = RequestMethod.GET)
    public CommonResult<List<String>> modelNameList(@RequestParam String modelName) {
        return  CommonResult.success(systemConfigService.modelNameList(modelName));
    }

    @ApiOperation(value = "获取属性")
    @RequestMapping(value = "/class/fields", method = RequestMethod.GET)
    public CommonResult<List<String>> fields(@RequestParam String modelName) {
        Class<?> aClass = systemConfigService.queryModel(modelName);
        List<String> fieldsByClass = systemConfigService.getFieldsByClass(aClass);
        return  CommonResult.success(fieldsByClass);
    }
}



