package com.cshy.front.controller.user;

import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.request.user.UserCollectAllRequest;
import com.cshy.common.model.request.user.UserCollectRequest;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.response.UserRelationResponse;
import com.cshy.service.service.store.StoreProductRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 商品点赞和收藏表 前端控制器

 */
@Slf4j
@RestController
@RequestMapping("api/front/collect")
@Api(tags = "用户 -- 点赞/收藏")
public class UserCollectController {

    @Autowired
    private StoreProductRelationService storeProductRelationService;

    /**
     * 我的收藏列表
     */
    @ApiOperation(value = "我的收藏列表")
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserRelationResponse>> getList(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(storeProductRelationService.getUserList(pageParamRequest)));
    }

    /**
     * 添加收藏产品
     * @param request StoreProductRelationRequest 新增参数
     */
    @ApiOperation(value = "添加收藏产品")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated UserCollectRequest request) {
        if (storeProductRelationService.add(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 添加收藏产品
     * @param request UserCollectAllRequest 新增参数
     */
    @ApiOperation(value = "批量收藏")
    @RequestMapping(value = "/all", method = RequestMethod.POST)
    public CommonResult<String> all(@RequestBody @Validated UserCollectAllRequest request) {
        if (storeProductRelationService.all(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 取消收藏产品
     */
    @ApiOperation(value = "取消收藏产品")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CommonResult<String> delete(@RequestBody String requestJson) {
        if (storeProductRelationService.delete(requestJson)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 取消收藏产品(通过商品)
     */
    @ApiOperation(value = "取消收藏产品(通过商品)")
    @RequestMapping(value = "/cancel/{proId}", method = RequestMethod.POST)
    public CommonResult<String> cancel(@PathVariable Integer proId) {
        if (storeProductRelationService.deleteByProIdAndUid(proId)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }
}



