package com.cshy.admin.controller.system;

import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.request.system.SystemWriteOffOrderSearchRequest;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.response.SystemWriteOffOrderResponse;
import com.cshy.service.service.StoreOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 订单表 前端控制器

 */
@Slf4j
@RestController
@RequestMapping("api/admin/system/store/order")
@Api(tags = "设置 -- 提货点 -- 核销订单") //配合swagger使用
public class SystemWriteOffOrderController {

    @Autowired
    private StoreOrderService storeOrderService;

    /**
     * 分页显示订单表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:system:order:list')")
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public CommonResult<SystemWriteOffOrderResponse>  getList(
            @Validated SystemWriteOffOrderSearchRequest request,
            @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(storeOrderService.getWriteOffList(request, pageParamRequest));
    }
}



