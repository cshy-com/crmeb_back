package com.cshy.admin.controller.category;

import com.cshy.common.model.Type;
import com.cshy.common.model.dto.activity.ActivityDto;
import com.cshy.common.model.dto.category.ActivityCategoryDto;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.activity.ActivityQuery;
import com.cshy.common.model.query.category.ActivityCategoryQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.activity.ActivityVo;
import com.cshy.common.model.vo.category.ActivityCategoryVo;
import com.cshy.service.service.category.ActivityCategoryService;
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
@RequestMapping("api/admin/activity/category")
@Api(tags = "v2 -- 活动名称配置")
public class ActivityCategoryController {
    @Autowired
    private ActivityCategoryService activityCategoryService;

    @ApiOperation(value = "分页")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public CommonResult<CommonPage<ActivityCategoryVo>> page(@RequestBody ActivityCategoryQuery query, @Validated(Type.Page.class) @RequestBody BasePage basePage) {
        CommonPage<ActivityCategoryVo> page = activityCategoryService.page(query, basePage);
        return CommonResult.success(page);
    }

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody ActivityCategoryDto dto) {
        return CommonResult.success(activityCategoryService.add(dto));
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiImplicitParam(name = "id", value = "活动ID")
    public CommonResult<String> delete(@RequestParam(value = "id") String id) {
        activityCategoryService.delete(id);
        return CommonResult.success();
    }

    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiImplicitParam(name = "activityDto", value = "activityDto")
    public CommonResult<String> update(@RequestBody ActivityCategoryDto activityCategoryDto) {
        activityCategoryService.update(activityCategoryDto);
        return CommonResult.success();
    }

    @ApiOperation("查一个")
    @GetMapping("/obj/{id}")
    public CommonResult<ActivityCategoryVo> obj(@PathVariable String id) {
        ActivityCategoryVo obj = activityCategoryService.obj(id);
        return CommonResult.success(obj);
    }

    @ApiOperation("查列表")
    @PostMapping("/list")
    public CommonResult<List<ActivityCategoryVo>> list(@RequestBody ActivityCategoryQuery query) {
        List<ActivityCategoryVo> list = activityCategoryService.list(query);
        return CommonResult.success(list);
    }
}
