package com.cshy.admin.controller;

import com.cshy.common.model.query.giftCard.GiftCardQuery;
import com.cshy.common.model.request.store.StoreBargainSearchRequest;
import com.cshy.common.model.request.store.StoreCombinationSearchRequest;
import com.cshy.common.model.request.store.StoreOrderSearchRequest;
import com.cshy.common.model.request.store.StoreProductSearchRequest;
import com.cshy.common.model.response.CommonResult;
import com.cshy.service.service.ExcelService;
import com.cshy.service.service.giftCard.GiftCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;


/**
 * Excel导出 前端控制器

 */
@Slf4j
@RestController
@RequestMapping("api/export")
@Api(tags = "导出 -- Excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private GiftCardService giftCardService;

    /**
     * 商品导出
     * @param request 搜索条件
     */
    @PreAuthorize("hasAuthority('admin:export:excel:product')")
    @ApiOperation(value = "产品")
    @RequestMapping(value = "/product", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> export(@Validated StoreProductSearchRequest request) {
        String fileName = excelService.exportProduct(request);
        HashMap<String, String> map = new HashMap<>();
        map.put("fileName", fileName);
        return CommonResult.success(map);
    }

    /**
     * 砍价商品导出
     * @param request 搜索条件
     */
    @PreAuthorize("hasAuthority('admin:export:excel:bargain')")
    @ApiOperation(value = "砍价商品导出")
    @RequestMapping(value = "/bargain/product", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> exportBargainProduct(@Validated StoreBargainSearchRequest request) {
        String fileName = excelService.exportBargainProduct(request);
        HashMap<String, String> map = new HashMap<>();
        map.put("fileName", fileName);
        return CommonResult.success(map);
    }

    /**
     * 拼团商品导出
     * @param request 搜索条件
     */
    @PreAuthorize("hasAuthority('admin:export:excel:combiantion')")
    @ApiOperation(value = "拼团商品导出")
    @RequestMapping(value = "/combiantion/product", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> exportCombinationProduct(@Validated StoreCombinationSearchRequest request) {
        String fileName = excelService.exportCombinationProduct(request);
        HashMap<String, String> map = new HashMap<>();
        map.put("fileName", fileName);
        return CommonResult.success(map);
    }

    /**
     * 订单导出
     * @param request 搜索条件
     */
    @PreAuthorize("hasAuthority('admin:export:excel:order')")
    @ApiOperation(value = "订单导出")
    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> exportOrder(@Validated StoreOrderSearchRequest request){
        String fileName = excelService.exportOrder(request);
        HashMap<String, String> map = new HashMap<>();
        map.put("fileName", fileName);
        return CommonResult.success(map);
    }

    @ApiOperation("批量导出")
    @PostMapping("/giftCard")
    public void export(@RequestBody GiftCardQuery query, HttpServletResponse response) throws IOException {
        giftCardService.export(query, response);
    }

}



