package com.cshy.admin.controller.shipping;

import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.request.shipping.ShippingTemplatesRequest;
import com.cshy.common.model.request.shipping.ShippingTemplatesSearchRequest;
import com.cshy.service.service.ShippingTemplatesService;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import com.cshy.common.model.entity.express.ShippingTemplates;

/**
 * 物流-模板控制器

 */
@Slf4j
@RestController
@RequestMapping("api/admin/express/shipping/templates")
@Api(tags = "设置 -- 物流 -- 模板")
public class ShippingTemplatesController {

    @Autowired
    private ShippingTemplatesService shippingTemplatesService;

    /**
     * 分页显示
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:shipping:templates:list')")
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ShippingTemplates>>  getList(@Validated ShippingTemplatesSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<ShippingTemplates> shippingTemplatesCommonPage = CommonPage.restPage(shippingTemplatesService.getList(request, pageParamRequest));
        return CommonResult.success(shippingTemplatesCommonPage);
    }

    /**
     * 新增
     * @param request 新增参数
     */
    @PreAuthorize("hasAuthority('admin:shipping:templates:save')")
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated ShippingTemplatesRequest request){
        if (shippingTemplatesService.create(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed("新增运费模板失败");
    }

    /**
     * 删除
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:shipping:templates:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="模板ID", required = true)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(shippingTemplatesService.remove(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改
     * @param id integer id
     * @param request ShippingTemplatesRequest 修改参数
     */
    @PreAuthorize("hasAuthority('admin:shipping:templates:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated ShippingTemplatesRequest request){
        if (shippingTemplatesService.update(id, request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 查询信息
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:shipping:templates:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="模板ID", required = true)
    public CommonResult<ShippingTemplates> info(@RequestParam(value = "id") Integer id){
        return CommonResult.success(shippingTemplatesService.getInfo(id));
   }
}



