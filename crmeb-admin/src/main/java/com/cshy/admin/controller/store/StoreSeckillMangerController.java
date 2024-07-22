package com.cshy.admin.controller.store;

import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.request.store.StoreSeckillMangerRequest;
import com.cshy.common.model.request.store.StoreSeckillMangerSearchRequest;
import com.cshy.common.model.response.StoreSeckillManagerResponse;
import com.cshy.service.service.store.StoreSeckillMangerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 秒杀配置 前端控制器

 */
@Slf4j
@RestController
@RequestMapping("api/admin/store/seckill/manger")
@Api(tags = "商品 -- 秒杀 -- 配置") //配合swagger使用
public class StoreSeckillMangerController {

    @Autowired
    private StoreSeckillMangerService storeSeckillMangerService;

    /**
     * 分页显示
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('admin:seckill:manger:info')")
    public CommonResult<CommonPage<StoreSeckillManagerResponse>>  getList(
            @Validated StoreSeckillMangerSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(storeSeckillMangerService.page(request, pageParamRequest)));
    }

    /**
     * 新增
     * @param storeSeckillMangerRequest 新增参数
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('admin:seckill:manger:save')")
    public CommonResult<String> save(@RequestBody @Validated StoreSeckillMangerRequest storeSeckillMangerRequest) {
        if (storeSeckillMangerService.saveManger(storeSeckillMangerRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }


    /**
     * 删除
     * @param id Integer
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('admin:seckill:manger:delete')")
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id) {
        if (storeSeckillMangerService.deleteLogicById(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 修改
     * @param id integer id
     * @param storeSeckillMangerRequest 修改参数
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('admin:seckill:manger:update')")
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated StoreSeckillMangerRequest storeSeckillMangerRequest) {
        storeSeckillMangerService.update(id, storeSeckillMangerRequest);
        return CommonResult.success();
    }

    /**
     * 查询信息
     * @param id Integer
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('admin:seckill:manger:info')")
    public CommonResult<StoreSeckillManagerResponse> info(@RequestParam(value = "id") Integer id) {
        return CommonResult.success(storeSeckillMangerService.detail(id));
   }

    /**
     * 更新秒杀配置状态
     * @param id    配置id
     * @param status    待更新配置状态
     * @return  更新结果
     */
   @ApiOperation(value = "秒杀配置状态更新")
   @RequestMapping(value = "/update/status/{id}", method = RequestMethod.POST)
   @ApiImplicitParams({
           @ApiImplicitParam(name = "id", value = "秒杀配置id", dataType = "int", required = true),
           @ApiImplicitParam(name = "status", value = "状态", dataType = "boolean", required = true)
   })
   @PreAuthorize("hasAuthority('admin:seckill:manger:update:status')")
   public CommonResult<Object> updateStatus(@PathVariable(value = "id") Integer id, Boolean status) {
        return CommonResult.success(storeSeckillMangerService.updateStatus(id,status));
   }
}



