package com.cshy.admin.controller.wechat;

import com.cshy.common.model.entity.wechat.SysTemplateMessage;
import com.cshy.common.model.response.CommonResult;
import com.cshy.service.service.system.SysTemplateMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 微信模板 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/wechat/template")
@Api(tags = "微信 -- 消息模版") //配合swagger使用
public class WechatTemplateMessageController {

    @Autowired
    private SysTemplateMessageService sysTemplateMessageService;

    /**
     * 公众号模板消息同步
     */
    @ApiOperation(value = "公众号模板消息同步")
    @RequestMapping(value = "/wechatPublic/sync", method = RequestMethod.POST)
    public CommonResult<String> wechatPublicSync() {
        if (sysTemplateMessageService.wechatPublicSync()) {
            return CommonResult.success("公众号模板消息同步成功");
        }
        return CommonResult.failed("公众号模板消息同步失败");
    }

    /**
     * 小程序订阅消息同步
     */
    @PreAuthorize("hasAuthority('admin:wechat:routine:sync')")
    @ApiOperation(value = "小程序订阅消息同步")
    @RequestMapping(value = "/routine/sync", method = RequestMethod.POST)
    public CommonResult<String> routineSync() {
        if (sysTemplateMessageService.routineSync()) {
            return CommonResult.success("小程序订阅消息同步成功");
        }
        return CommonResult.failed("小程序订阅消息同步失败");
    }

    /**
     * 查询公众号模板消息列表
     */
    @PreAuthorize("hasRole('admin')")
    @ApiOperation(value = "查询公众号模板消息列表")
    @RequestMapping(value = "/wechatPublic/list", method = RequestMethod.GET)
    public CommonResult<List<SysTemplateMessage>> wechatPublicList() {
        return CommonResult.success(sysTemplateMessageService.list());
    }
}



