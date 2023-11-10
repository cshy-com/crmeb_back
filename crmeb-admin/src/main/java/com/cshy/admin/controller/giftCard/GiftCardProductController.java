package com.cshy.admin.controller.giftCard;

import com.cshy.common.model.Type;
import com.cshy.common.model.dto.giftCard.GiftCardProductDto;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.giftCard.GiftCardProductQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.giftCard.GiftCardProductVo;
import com.cshy.service.service.giftCard.GiftCardProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/admin/giftCard/product")
@Api(tags = "v2 -- 礼品卡-商品")
public class GiftCardProductController {
    @Resource
    private GiftCardProductService giftCardProductService;

    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public CommonResult<CommonPage<GiftCardProductVo>> page(@RequestBody GiftCardProductQuery query, @Validated(Type.Page.class) @RequestBody BasePage basePage) {
        CommonPage<GiftCardProductVo> page = giftCardProductService.page(query, basePage);
        return CommonResult.success(page);
    }

    //    @PreAuthorize("hasAuthority('admin:article:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiImplicitParam(name = "id", value = "活动ID")
    public CommonResult<String> delete(@RequestParam(value = "id") String id) {
        giftCardProductService.delete(id);
        return CommonResult.success();
    }


    //    @PreAuthorize("hasAuthority('admin:article:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiImplicitParam(name = "giftCardProductDto", value = "giftCardProductDto")
    public CommonResult<String> update(@RequestBody GiftCardProductDto giftCardProductDto) {
        giftCardProductService.update(giftCardProductDto);
        return CommonResult.success();
    }

    @ApiOperation("查列表")
    @PostMapping("/list")
    public CommonResult<List<GiftCardProductVo>> list(@RequestBody GiftCardProductQuery query) {
        List<GiftCardProductVo> list = giftCardProductService.list(query);
        return CommonResult.success(list);
    }

    @ApiOperation("批量添加")
    @PostMapping("/batchAdd")
    public CommonResult<String> batchAdd(@RequestParam String giftCardTypeId, @RequestBody List<Integer> idList) {
        giftCardProductService.batchAdd(idList, giftCardTypeId);
        return CommonResult.success();
    }

}
