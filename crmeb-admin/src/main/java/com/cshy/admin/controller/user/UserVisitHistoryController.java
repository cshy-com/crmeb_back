package com.cshy.admin.controller.user;

import com.cshy.common.model.Type;
import com.cshy.common.model.dto.user.UserVisitHistoryDto;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.user.UserVisitHistoryQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.giftCard.GiftCardVo;
import com.cshy.common.model.vo.user.UserVisitHistoryVo;
import com.cshy.service.service.user.UserVisitHistoryService;
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
@RequestMapping("api/admin/user/visit/history")
@Api(tags = "v2 -- 用户浏览历史")
public class UserVisitHistoryController {
    @Autowired
    private UserVisitHistoryService userVisitHistoryService;

    //    @PreAuthorize("hasAuthority('admin:activity:list')")
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public CommonResult<CommonPage<UserVisitHistoryVo>> page(@RequestBody UserVisitHistoryQuery query, @Validated(Type.Page.class) @RequestBody BasePage basePage) {
        CommonPage<UserVisitHistoryVo> page = userVisitHistoryService.page(query, basePage);
        return CommonResult.success(page);
    }

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody UserVisitHistoryDto dto) {
        return CommonResult.success(userVisitHistoryService.add(dto));
    }

    //    @PreAuthorize("hasAuthority('admin:article:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiImplicitParam(name = "id", value = "活动ID")
    public CommonResult<String> delete(@RequestParam(value = "id") String id) {
        userVisitHistoryService.delete(id);
        return CommonResult.success();
    }


    //    @PreAuthorize("hasAuthority('admin:article:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiImplicitParam(name = "id", value = "文章ID")
    public CommonResult<String> update(@RequestBody UserVisitHistoryDto UserVisitHistoryDto) {
        userVisitHistoryService.update(UserVisitHistoryDto);
        return CommonResult.success();
    }

    @ApiOperation("查一个")
    @GetMapping("/obj/{id}")
    public CommonResult<UserVisitHistoryVo> obj(@PathVariable String id) {
        UserVisitHistoryVo obj = userVisitHistoryService.obj(id);
        return CommonResult.success(obj);
    }

    @ApiOperation("查列表")
    @PostMapping("/list")
    public CommonResult<List<UserVisitHistoryVo>> list(@RequestBody UserVisitHistoryQuery query) {
        List<UserVisitHistoryVo> list = userVisitHistoryService.list(query);
        return CommonResult.success(list);
    }

}
