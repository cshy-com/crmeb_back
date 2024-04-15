package com.cshy.front.controller.activity;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cshy.common.model.entity.seckill.StoreSeckill;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.category.ActivityCategoryQuery;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.request.store.StoreSeckillMangerSearchRequest;
import com.cshy.common.model.response.*;
import com.cshy.common.model.vo.category.ActivityCategoryVo;
import com.cshy.service.service.category.ActivityCategoryService;
import com.cshy.service.service.store.StoreSeckillMangerService;
import com.cshy.service.service.store.StoreSeckillService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(value = "v2 -- 活动/秒杀相关接口", tags = "v2 -- 活动/秒杀相关接口")
@RestController
@AllArgsConstructor
@RequestMapping("api/front/activity/secKill")
public class StoreSecKillController {
    @Resource
    private final ActivityCategoryService activityCategoryService;

    @Resource
    private final StoreSeckillService storeSeckillService;

    @Resource
    private final StoreSeckillMangerService storeSeckillMangerService;

    @ApiOperation("查询启用的活动分类")
    @GetMapping("/query/enabled")
    public CommonResult<List<ActivityCategoryVo>> queryEnabled() {
        ActivityCategoryQuery activityCategoryQuery = new ActivityCategoryQuery();
        activityCategoryQuery.setIsEnabled(true);
        List<ActivityCategoryVo> list = activityCategoryService.list(activityCategoryQuery);
        return CommonResult.success(list);
    }

    @ApiOperation("根据活动分类查询活动")
    @GetMapping("/query/productList")
    public CommonResult<List<Map<String, Object>>> queryByCate(@RequestParam String activityCategoryName) {
        List<Map<String, Object>> resList = Lists.newArrayList();

        StoreSeckillMangerSearchRequest request = new StoreSeckillMangerSearchRequest();
        request.setActivityCategoryName(activityCategoryName);

        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setPage(0);
        pageParamRequest.setLimit(999);

        List<StoreSeckillManagerResponse> storeSeckillMangerServiceList = storeSeckillMangerService.getList(request, pageParamRequest);

        //根据list查询商品
        storeSeckillMangerServiceList.forEach(storeSeckillManagerResponse -> {
            Map<String, Object> map = new HashMap<>();
            List<StoreSeckill> storeSeckillList = storeSeckillService.list(new LambdaQueryWrapper<StoreSeckill>()
                    .eq(StoreSeckill::getTimeId, storeSeckillManagerResponse.getId())
                    .eq(StoreSeckill::getStatus, 1)
                    .eq(StoreSeckill::getIsDel, false));
            List<Map<String, Object>> mapList = storeSeckillList.stream().map(storeSeckill -> {
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("title", storeSeckill.getTitle());
                hashMap.put("sliderImages", storeSeckill.getImages());
                hashMap.put("otPrice", storeSeckill.getOtPrice());
                hashMap.put("price", storeSeckill.getPrice());
                hashMap.put("image", storeSeckill.getImage());
                hashMap.put("id", storeSeckill.getProductId());
                return hashMap;
            }).collect(Collectors.toList());
            map.put("id", storeSeckillManagerResponse.getId());
            map.put("name", storeSeckillManagerResponse.getName());
            map.put("sliderImages", storeSeckillManagerResponse.getSliderImages());
            map.put("image", storeSeckillManagerResponse.getImage());
            map.put("productList", mapList);
            resList.add(map);
        });

        return CommonResult.success(resList);
    }

    @ApiOperation("根据活动配置id查询下面的商品")
    @GetMapping("/query/byId")
    public CommonResult<List<StoreSecKillH5Response>> queryById(@RequestParam Serializable id) {
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setLimit(9999);
        pageParamRequest.setPage(0);
        List<StoreSecKillH5Response> secKillListByTimeId = storeSeckillService.getKillListByTimeId((String) id, pageParamRequest);

        return CommonResult.success(secKillListByTimeId);
    }

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

    @ApiOperation(value = "根据商品分类id查询秒杀商品")
    @RequestMapping(value = "/query/byCateId", method = RequestMethod.GET)
    public CommonResult<List<Map<String, Object>>> queryByCateId(@RequestParam Integer cateId) {
        List<Map<String, Object>> maps = storeSeckillService.queryByCateId(cateId);
        return CommonResult.success(maps);
    }
}
