package com.cshy.front.controller;

import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.response.*;
import com.cshy.common.model.response.*;
import com.cshy.service.service.StoreSeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SecKillController

 */
@Slf4j
@RestController
@RequestMapping("api/front/seckill")
@Api(tags = "秒杀商品")
public class SecKillController {

    @Autowired
    StoreSeckillService storeSeckillService;

    /**
     * 秒杀首页数据
     * @return 可秒杀配置
     */
    @ApiOperation(value = "秒杀首页数据")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public CommonResult<SeckillIndexResponse> index() {
        return CommonResult.success(storeSeckillService.getIndexInfo());
    }

    /**
     * 秒杀Index
     * @return 可秒杀配置
     */
    @ApiOperation(value = "秒杀Header")
    @RequestMapping(value = "/header", method = RequestMethod.GET)
    public CommonResult<List<SecKillResponse>> header() {
        return CommonResult.success(storeSeckillService.getForH5Index());
    }

    /**
     * 根据时间段查询秒杀信息
     * @return 查询时间内的秒杀商品列表
     */
    @ApiOperation(value = "秒杀列表")
    @RequestMapping(value = "/list/{timeId}", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreSecKillH5Response>> list(@PathVariable("timeId") String timeId, @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(storeSeckillService.getKillListByTimeId(timeId, pageParamRequest)));
    }


    @ApiOperation(value = "详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<StoreSeckillDetailResponse> info(@PathVariable(value = "id") Integer id) {
        StoreSeckillDetailResponse storeSeckill = storeSeckillService.getDetailH5(id);
        return CommonResult.success(storeSeckill);
    }
}
