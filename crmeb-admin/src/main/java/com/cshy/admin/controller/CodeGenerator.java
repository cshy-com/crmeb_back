package com.cshy.admin.controller;

import com.cshy.common.constants.Constants;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.utils.DateUtil;
import com.cshy.common.utils.genutils.GenCodePageListUtils;
import com.cshy.common.utils.genutils.GenCodePageQueryUtils;
import com.cshy.service.service.impl.CrmebGeneratorCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * 前后端代码生成器 controller
 
 */
@Controller
@RequestMapping("api/codegen")
@Api(tags = "代码生成")
public class CodeGenerator {

    @Autowired
    private CrmebGeneratorCodeService crmebGeneratorCodeService;

    @ResponseBody
    @ApiOperation(value="代码生成-新列表")
    @GetMapping("/list")
    public CommonResult<Object> listNew(@RequestParam Map<String, Object> params){
        GenCodePageListUtils pageUtil = crmebGeneratorCodeService.queryList(new GenCodePageQueryUtils(params));
        return CommonResult.success(pageUtil);
    }

    /**
     * 生成代码 API
     */
    @GetMapping("/code")
    public void code(@RequestParam String tables, HttpServletResponse response) throws IOException {
        byte[] data = crmebGeneratorCodeService.generatorCode(tables.split(","));

        String contentLength = "Content-Length";
        String contentType = "application/octet-stream; charset=UTF-8;";
        String contentDisposition = "Content-Disposition";
        String attachment = "attachment; filename=\"CRMEB-Java-Code-"+ DateUtil.dateToStr(new Date(), Constants.DATE_FORMAT_HHMM) +".zip\"";

        response.reset();
        response.addHeader(contentLength, data.length +"");
        response.setContentType(contentType);
        response.setHeader(contentDisposition, attachment);

        IOUtils.write(data, response.getOutputStream());
    }
}
