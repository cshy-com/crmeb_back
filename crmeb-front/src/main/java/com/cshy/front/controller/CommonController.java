package com.cshy.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cshy.common.model.entity.system.SystemAdmin;
import com.cshy.common.model.response.CommonResult;
import com.cshy.service.service.system.SystemAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/front/common")
@Api(tags = "v2 -- h5通用接口")
public class CommonController {
    @Autowired
    private SystemAdminService systemAdminService;


    @ApiOperation(value = "查询客服电话")
    @RequestMapping(value = "/contact/phone", method = RequestMethod.GET)
    public CommonResult<List<String>> getInfoByPickupCode() {
        List<SystemAdmin> systemAdminList = systemAdminService.list(new LambdaQueryWrapper<SystemAdmin>().eq(SystemAdmin::getIsSms, 1));
        List<String> phoneList = systemAdminList.stream().map(SystemAdmin::getPhone).collect(Collectors.toList());
        return  CommonResult.success(phoneList);
    }
}