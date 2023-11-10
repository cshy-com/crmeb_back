package com.cshy.admin.controller.activity;

import com.cshy.common.model.Type;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.activity.ActivityQuery;
import com.cshy.common.model.dto.activity.ActivityDto;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.activity.ActivityVo;
import com.cshy.service.service.activity.ActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/admin/activity")
@Api(tags = "v2 -- 活动配置")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    //    @PreAuthorize("hasAuthority('admin:activity:list')")
    @ApiOperation(value = "分页活动列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public CommonResult<CommonPage<ActivityVo>> page(@RequestBody ActivityQuery query, @Validated(Type.Page.class) @RequestBody BasePage basePage) {
        CommonPage<ActivityVo> page = activityService.page(query, basePage);
        return CommonResult.success(page);
    }

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody ActivityDto dto) {
        return CommonResult.success(activityService.add(dto));
    }

    //    @PreAuthorize("hasAuthority('admin:article:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiImplicitParam(name = "id", value = "活动ID")
    public CommonResult<String> delete(@RequestParam(value = "id") String id) {
        activityService.delete(id);
        return CommonResult.success();
    }


    //    @PreAuthorize("hasAuthority('admin:article:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiImplicitParam(name = "activityDto", value = "activityDto")
    public CommonResult<String> update(@RequestBody ActivityDto activityDto) {
        activityService.update(activityDto);
        return CommonResult.success();
    }

//    @PreAuthorize("@ss.hasAnyRoles('manage, mallUser, mallShopUser')")
    @ApiOperation("查一个")
    @GetMapping("/obj/{id}")
    public CommonResult<ActivityVo> obj(@PathVariable String id) {
        ActivityVo obj = activityService.obj(id);
        return CommonResult.success(obj);
    }

    @ApiOperation("查列表")
    @PostMapping("/list")
    public CommonResult<List<ActivityVo>> list(@RequestBody ActivityQuery query) {
        List<ActivityVo> list = activityService.list(query);
        return CommonResult.success(list);
    }
}
