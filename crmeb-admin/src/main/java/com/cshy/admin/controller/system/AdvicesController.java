package com.cshy.admin.controller.system;

import com.cshy.admin.filter.TokenComponent;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.Type;
import com.cshy.common.model.dto.AdvicesDto;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.AdvicesQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.LoginUserVo;
import com.cshy.common.model.vo.system.AdvicesVo;
import com.cshy.service.service.system.AdvicesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(value = "v2 -- 建议意见", tags = "v2 -- 建议意见")
@RestController
@AllArgsConstructor
@RequestMapping("api/admin/advices")
public class AdvicesController {
    @Resource
    private final AdvicesService advicesService;

    @Autowired
    private TokenComponent tokenComponent;

    @ApiOperation("修改")
    @PutMapping("/update")
    public CommonResult<?> update(@RequestBody AdvicesDto dto) {
        advicesService.update(dto);
        return CommonResult.success();
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete/{id}")
    public CommonResult<?> delete(@PathVariable String id) {
        advicesService.delete(id);
        return CommonResult.success();
    }

    @ApiOperation("查一个")
    @GetMapping("/obj/{id}")
    public CommonResult<AdvicesVo> obj(@PathVariable String id) {
        AdvicesVo obj = advicesService.obj(id);
        return CommonResult.success(obj);
    }

    @ApiOperation("查列表")
    @PostMapping("/list")
    public CommonResult<List<AdvicesVo>> list(@RequestBody AdvicesQuery query) {
        List<AdvicesVo> list = advicesService.list(query);
        return CommonResult.success(list);
    }

    @ApiOperation("查分页")
    @PostMapping("/page")
    public CommonResult<CommonPage<AdvicesVo>> page(@RequestBody AdvicesQuery query, @Validated(Type.Page.class) @RequestBody BasePage basePage, HttpServletRequest request) {
        query.setIsDel(0);
        query.setParentId(null);
        CommonPage<AdvicesVo> page = advicesService.page(query, basePage);
        return CommonResult.success(page);
    }

    @ApiOperation("回复")
    @PostMapping("/reply")
    public CommonResult<?> reply(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        LoginUserVo loginUser = tokenComponent.getLoginUser(request);
        if (!params.containsKey("content"))
            throw new CrmebException("内容不能为空");
        if (!params.containsKey("adviceId"))
            throw new CrmebException("adviceId不能为空");
        advicesService.reply(String.valueOf(params.get("adviceId")), (String) params.get("content"), loginUser.getUser().getId(), (String) params.get("picture"));
        return CommonResult.success();
    }
}
