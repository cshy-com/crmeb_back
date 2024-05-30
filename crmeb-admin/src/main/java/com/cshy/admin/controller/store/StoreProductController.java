package com.cshy.admin.controller.store;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.entity.product.StoreProduct;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.request.store.StoreCopyProductRequest;
import com.cshy.common.model.request.store.StoreProductAddRequest;
import com.cshy.common.model.request.store.StoreProductRequest;
import com.cshy.common.model.request.store.StoreProductSearchRequest;
import com.cshy.common.model.response.StoreProductInfoResponse;
import com.cshy.common.model.response.StoreProductResponse;
import com.cshy.common.model.response.StoreProductTabsHeader;
import com.cshy.common.utils.CrmebUtil;
import com.cshy.service.service.category.CategoryService;
import com.cshy.service.service.store.StoreCartService;
import com.cshy.service.service.store.StoreProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 商品表 前端控制器
 
 */
@Slf4j
@RestController
@RequestMapping("api/admin/store/product")
@Api(tags = "商品") //配合swagger使用
public class StoreProductController {

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreCartService storeCartService;

    /**
     * 分页显示商品表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:product:list')")
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreProductResponse>> getList(@Validated StoreProductSearchRequest request,
                                                                  @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(storeProductService.getAdminList(request, pageParamRequest)));
    }

    /**
     * 新增商品
     * @param request 新增参数
     */
    @PreAuthorize("hasAuthority('admin:product:save')")
    @ApiOperation(value = "新增商品")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated StoreProductAddRequest request) {
        if (storeProductService.save(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 删除商品表
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:product:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestBody @PathVariable Integer id, @RequestParam(value = "type", required = false, defaultValue = "recycle")String type) {
        if (storeProductService.deleteProduct(id, type)) {
            if ("recycle".equals(type)) {
                storeCartService.productStatusNotEnable(id);
            } else {
                storeCartService.productDelete(id);
            }
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 恢复已删除商品表
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:product:restore')")
    @ApiOperation(value = "恢复商品")
    @RequestMapping(value = "/restore/{id}", method = RequestMethod.GET)
    public CommonResult<String> restore(@RequestBody @PathVariable Integer id) {
        if (storeProductService.reStoreProduct(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 商品修改
     * @param storeProductRequest 商品参数
     */
    @PreAuthorize("hasAuthority('admin:product:update')")
    @ApiOperation(value = "商品修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated StoreProductAddRequest storeProductRequest) {
        if (storeProductService.update(storeProductRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }


    @ApiOperation(value = "批量更新分类")
    @RequestMapping(value = "/batchUpdate", method = RequestMethod.POST)
    public CommonResult<String> batchUpdate(@RequestBody Map<String, Object> map) {
        if (!map.containsKey("ids") || !map.containsKey("categoryId")) {
            throw new CrmebException("参数为ids数组 和 categoryId");
        }

        List<Integer> cateList = new ArrayList<>();

        Object cate =  map.get("categoryId");
        if(!(cate instanceof ArrayList<?>)){
            throw new CrmebException("参数不正确");
        }else {
            for (Object object : (List<?>) cate) {
                cateList.add(Integer.class.cast(object));
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Integer item : cateList) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(item);
        }

        List<Integer> idList = new ArrayList<>();

        Object value =  map.get("ids");
        if(!(value instanceof ArrayList<?>)){
            throw new CrmebException("参数不正确");
        }else{
            for (Object object : (List<?>) value) {
                idList.add(Integer.class.cast(object));
            }
            storeProductService.update(new LambdaUpdateWrapper<StoreProduct>().in(StoreProduct::getId, idList).set(StoreProduct::getCateId, sb.toString()));
        }

        return CommonResult.success();
    }

    /**
     * 商品详情
     * @param id 商品id
     */
    @PreAuthorize("hasAuthority('admin:product:info')")
    @ApiOperation(value = "商品详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<StoreProductInfoResponse> info(@PathVariable Integer id) {
        return CommonResult.success(storeProductService.getInfo(id));
   }

    /**
     * 商品tabs表头数据
     */
    @PreAuthorize("hasAuthority('admin:product:tabs:headers')")
   @ApiOperation(value = "商品表头数量")
   @RequestMapping(value = "/tabs/headers", method = RequestMethod.GET)
   public CommonResult<List<StoreProductTabsHeader>> getTabsHeader() {
        return CommonResult.success(storeProductService.getTabsHeader());
   }


    /**
     * 上架
     */
    @PreAuthorize("hasAuthority('admin:product:up')")
    @ApiOperation(value = "上架")
    @RequestMapping(value = "/putOnShell/{id}", method = RequestMethod.GET)
    public CommonResult<String> putOn(@PathVariable Integer id) {
        if (storeProductService.putOnShelf(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 下架
     */
    @PreAuthorize("hasAuthority('admin:product:down')")
    @ApiOperation(value = "下架")
    @RequestMapping(value = "/offShell/{id}", method = RequestMethod.GET)
    public CommonResult<String> offShell(@PathVariable Integer id) {
        if (storeProductService.offShelf(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @PreAuthorize("hasAuthority('admin:product:import:product')")
    @ApiOperation(value = "导入99Api商品")
    @RequestMapping(value = "/importProduct", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "form", value = "导入平台1=淘宝，2=京东，3=苏宁，4=拼多多, 5=天猫", dataType = "int",  required = true),
            @ApiImplicitParam(name = "url", value = "URL", dataType = "String", required = true),
    })
    public CommonResult<StoreProductRequest> importProduct(
            @RequestParam @Valid int form,
            @RequestParam @Valid String url) throws IOException, JSONException {
        StoreProductRequest productRequest = storeProductService.importProductFromUrl(url, form);
        return CommonResult.success(productRequest);
    }

    /**
     * 获取复制商品配置
     */
    @PreAuthorize("hasAuthority('admin:product:copy:config')")
    @ApiOperation(value = "获取复制商品配置")
    @RequestMapping(value = "/copy/config", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> copyConfig() {
        return CommonResult.success(storeProductService.copyConfig());
    }

    @PreAuthorize("hasAuthority('admin:product:copy:product')")
    @ApiOperation(value = "复制平台商品")
    @RequestMapping(value = "/copy/product", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> copyProduct(@RequestBody @Valid StoreCopyProductRequest request) {
        return CommonResult.success(storeProductService.copyProduct(request.getUrl()));
    }

    @PreAuthorize("hasRole('admin')")
    @ApiOperation(value = "设置商品属性")
    @RequestMapping(value = "/set/feature", method = RequestMethod.POST)
    public CommonResult<String> updateFeature(@RequestBody Map<String, Object> params) {
        List<Integer> idList = (List<Integer>) params.get("idList");
        String feature = (String) params.get("feature");
        boolean flag = (Boolean) params.get("flag");
        storeProductService.updateFeature(idList, feature, flag);
        return CommonResult.success();
    }
}



