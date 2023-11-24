package com.cshy.admin.controller.giftCard;

import cn.hutool.core.lang.Assert;
import com.cshy.common.exception.ExceptionCodeEnum;
import com.cshy.common.model.Type;
import com.cshy.common.model.dto.giftCard.GiftCardDto;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.giftCard.GiftCardQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.giftCard.GiftCardVo;
import com.cshy.service.service.giftCard.GiftCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/admin/giftCard")
@Api(tags = "v2 -- 礼品卡券")
public class GiftCardController {
    @Autowired
    private GiftCardService giftCardService;

    @PreAuthorize("hasRole('admin')")
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public CommonResult<CommonPage<GiftCardVo>> page(@RequestBody GiftCardQuery query, @Validated(Type.Page.class) @RequestBody BasePage basePage) {
        CommonPage<GiftCardVo> page = giftCardService.page(query, basePage);
        return  CommonResult.success(page);
    }

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody GiftCardDto dto) {
        return CommonResult.success(giftCardService.add(dto));
    }

    //    @PreAuthorize("hasAuthority('admin:article:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiImplicitParam(name = "id", value = "活动ID")
    public CommonResult<String> delete(@RequestParam(value = "id") String id) {
        giftCardService.delete(id);
        return CommonResult.success();
    }


    //    @PreAuthorize("hasAuthority('admin:article:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiImplicitParam(name = "id", value = "文章ID")
    public CommonResult<String> update(@RequestBody GiftCardDto giftCardDto) {
        giftCardService.update(giftCardDto);
        return CommonResult.success();
    }

    @ApiOperation("查一个")
    @GetMapping("/obj/{id}")
    public CommonResult<GiftCardVo> obj(@PathVariable String id) {
        GiftCardVo obj = giftCardService.obj(id);
        return CommonResult.success(obj);
    }

    @ApiOperation("批量添加")
    @PostMapping("/batchAdd")
    public CommonResult<String> batchAdd(@RequestBody GiftCardDto dto) {
        giftCardService.batchAdd(dto);
        return CommonResult.success();
    }

    @ApiOperation("查看密码")
    @GetMapping("/viewSecret")
    public CommonResult<String> viewSecret(@RequestParam String id) {
        String secret = giftCardService.viewSecret(id);
        return CommonResult.success(secret, ExceptionCodeEnum.SUCCESS.getMessage());
    }

    @ApiOperation("停用/启用")
    @GetMapping("/updateStatus")
    public CommonResult<GiftCardVo> updateStatus(@RequestParam String id, @RequestParam Integer status) {
        GiftCardVo giftCard = giftCardService.obj(id);
        giftCard.setCardStatus(status);
        GiftCardDto giftCardDto = new GiftCardDto();
        BeanUtils.copyProperties(giftCard, giftCardDto);
        giftCardService.update(giftCardDto);
        return CommonResult.success();
    }

    @ApiOperation("批量修改礼品卡类型")
    @PostMapping("/update/batch")
    public CommonResult<GiftCardVo> updateBatch(@RequestBody Map<String, Object> params) {
        Assert.isTrue(params.containsKey("serialNoList"), "序列号不能为空");
        Assert.isTrue(params.containsKey("giftCardTypeId"), "礼品卡类型id不能为空");

        return CommonResult.success(giftCardService.updateBatch(params));
    }
}