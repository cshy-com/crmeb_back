package com.cshy.admin.controller.giftCard;

import com.cshy.common.model.Type;
import com.cshy.common.model.dto.giftCard.GiftCardDto;
import com.cshy.common.model.dto.giftCard.GiftCardTypeDto;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.entity.giftCard.GiftCardType;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.giftCard.GiftCardTypeQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.giftCard.GiftCardTypeVo;
import com.cshy.common.model.vo.giftCard.GiftCardVo;
import com.cshy.service.service.giftCard.GiftCardTypeService;
import com.cshy.service.service.system.SystemAttachmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/admin/giftCard/type")
@Api(tags = "v2 -- 礼品卡类型")
public class GiftCardTypeController {
    @Autowired
    private GiftCardTypeService giftCardTypeService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    //    @PreAuthorize("hasAuthority('admin:activity:list')")
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public CommonResult<CommonPage<GiftCardTypeVo>> page(@RequestBody GiftCardTypeQuery query, @Validated(Type.Page.class) @RequestBody BasePage basePage) {
        CommonPage<GiftCardTypeVo> page = giftCardTypeService.page(query, basePage);
        return CommonResult.success(page);
    }

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody GiftCardTypeDto dto) {
        dto.setBanner(systemAttachmentService.clearPrefix(dto.getBanner()));
        return CommonResult.success(giftCardTypeService.add(dto));
    }

    //    @PreAuthorize("hasAuthority('admin:article:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiImplicitParam(name = "id", value = "活动ID")
    public CommonResult<String> delete(@RequestParam(value = "id") String id) {
        giftCardTypeService.delete(id);
        return CommonResult.success();
    }


    //    @PreAuthorize("hasAuthority('admin:article:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiImplicitParam(name = "id", value = "文章ID")
    public CommonResult<String> update(@RequestBody GiftCardTypeDto giftCardTypeDto) {
        giftCardTypeDto.setBanner(systemAttachmentService.clearPrefix(giftCardTypeDto.getBanner()));
        giftCardTypeService.update(giftCardTypeDto);
        return CommonResult.success();
    }

    @ApiOperation("查一个")
    @GetMapping("/obj/{id}")
    public CommonResult<GiftCardTypeVo> obj(@PathVariable String id) {
        GiftCardTypeVo obj = giftCardTypeService.obj(id);
        return CommonResult.success(obj);
    }

    @ApiOperation("查列表")
    @PostMapping("/list")
    public CommonResult<List<GiftCardTypeVo>> list(@RequestBody GiftCardTypeQuery query) {
        List<GiftCardTypeVo> list = giftCardTypeService.list(query);
        return CommonResult.success(list);
    }

    @ApiOperation("停用/启用")
    @GetMapping("/updateStatus")
    public CommonResult<GiftCardVo> updateStatus(@RequestParam String id, @RequestParam Integer status) {
        GiftCardTypeVo giftCardTypeVo = giftCardTypeService.obj(id);
        giftCardTypeVo.setStatus(status);
        GiftCardTypeDto giftCardDto = new GiftCardTypeDto();
        BeanUtils.copyProperties(giftCardTypeVo, giftCardDto);
        giftCardTypeService.update(giftCardDto);
        return CommonResult.success();
    }
}
