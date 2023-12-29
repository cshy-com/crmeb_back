package com.cshy.front.controller;

import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.Type;
import com.cshy.common.model.dto.AdvicesDto;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.AdvicesQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.system.AdvicesVo;
import com.cshy.common.token.FrontTokenComponent;
import com.cshy.service.service.system.AdvicesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Api(value = "v2 -- 建议意见", tags = "v2 -- 建议意见")
@RestController
@AllArgsConstructor
@RequestMapping("api/front/advices")
public class AdvicesController {
    @Resource
    private final AdvicesService advicesService;

    @Autowired
    private FrontTokenComponent tokenComponent;

    @ApiOperation("新增")
    @PostMapping("/add")
    public CommonResult<?> add(@RequestBody AdvicesDto dto) {
        Integer userId = tokenComponent.getUserId();
        dto.setUserId(userId);
        dto.setReplied(0);
        advicesService.add(dto);
        return CommonResult.success();
    }

    @ApiOperation("查看建议及回复")
    @GetMapping("/details")
    public CommonResult<?> details(@RequestParam String adviceId) {
        AdvicesVo advicesVo = advicesService.obj(adviceId);
        if (!advicesVo.getUserId().equals(tokenComponent.getUserId()))
            return CommonResult.success();
        //查询回复
        AdvicesQuery advicesQuery = new AdvicesQuery();
        advicesQuery.setParentId(adviceId);
        List<AdvicesVo> advicesVoList = advicesService.list(advicesQuery);
        advicesVo.setReplyList(advicesVoList);

        return CommonResult.success(advicesVo);
    }

    @ApiOperation("根据我的所有建议")
    @RequestMapping(value = "/myList",method = RequestMethod.POST)
    public CommonResult<?> myList(@RequestBody AdvicesQuery advicesQuery, @Validated(Type.Page.class) @RequestBody BasePage basePage) {
        Integer userId = tokenComponent.getUserId();
        if (Objects.isNull(userId))
            throw new CrmebException("登录失效，请重试");
        advicesQuery.setUserId(userId);
        CommonPage<AdvicesVo> page = advicesService.page(advicesQuery, basePage);
        return CommonResult.success(page);
    }

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
}
