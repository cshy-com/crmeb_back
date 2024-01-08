package com.cshy.front.controller.user;

import com.cshy.common.model.Type;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.user.UserVisitHistoryQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.user.UserVisitHistoryVo;
import com.cshy.service.service.user.UserService;
import com.cshy.service.service.user.UserVisitHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/front/user/visit/history")
@Api(tags = "v2 -- 用户浏览历史")
public class UserVisitHistoryController {
    private UserVisitHistoryService userVisitHistoryService;

    private UserService userService;

    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public CommonResult<CommonPage<UserVisitHistoryVo>> page(@RequestBody UserVisitHistoryQuery query, @Validated(Type.Page.class) @RequestBody BasePage basePage) {
        query.setUserId(userService.getUserId());
        CommonPage<UserVisitHistoryVo> page = userVisitHistoryService.page(query, basePage);
        return CommonResult.success(page);
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiImplicitParam(name = "ids", value = "浏览历史ID")
    public CommonResult<String> delete(@RequestBody List<String> ids) {
        ids.forEach(id -> {
            userVisitHistoryService.delete(id);
        });
        return CommonResult.success();
    }
}
