package com.cshy.admin.controller;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cshy.common.constants.ProductType;
import com.cshy.common.model.entity.product.StoreProduct;
import com.cshy.common.model.entity.product.StoreProductAttr;
import com.cshy.common.model.entity.product.StoreProductAttrValue;
import com.cshy.common.model.entity.product.StoreProductDescription;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.category.CategoryTreeVo;
import com.cshy.common.model.vo.oldMall.OldMallListVo;
import com.cshy.common.model.vo.ydd.YddCategoryVo;
import com.cshy.common.utils.DateUtil;
import com.cshy.common.utils.StringUtils;
import com.cshy.service.service.category.CategoryService;
import com.cshy.service.service.store.StoreProductAttrService;
import com.cshy.service.service.store.StoreProductAttrValueService;
import com.cshy.service.service.store.StoreProductDescriptionService;
import com.cshy.service.service.store.StoreProductService;
import com.cshy.service.service.system.SystemAttachmentService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Api(value = "v2 -- 原商城", tags = "v2 -- 原商城")
@RestController
@AllArgsConstructor
@RequestMapping("api/admin/old/mall")
@Slf4j
public class OldMallController {
    @Autowired
    SystemAttachmentService systemAttachmentService;

    @Autowired
    StoreProductService storeProductService;

    @Autowired
    StoreProductAttrService storeProductAttrService;

    @Autowired
    StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    StoreProductDescriptionService storeProductDescriptionService;

    private static final String prefix = "https://www.bankservice.shop/file";

    @ApiOperation(value = "同步")
    @RequestMapping(value = "/sync", method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Object> sync() {
        List<OldMallListVo> oldMallListVoList = getList();
        System.out.println(oldMallListVoList);

        //数据处理
        handleData(oldMallListVoList);
        return CommonResult.success();
    }

    private void handleData(List<OldMallListVo> oldMallListVoList) {
        oldMallListVoList.forEach(oldMallListVo -> {
            StoreProduct storeProduct = new StoreProduct();
            StoreProduct serviceOne = storeProductService.getOne(new LambdaQueryWrapper<StoreProduct>().eq(StoreProduct::getStoreName, oldMallListVo .getName()));
            if (Objects.isNull(serviceOne)) {
                //基本数据
                storeProduct.setStoreName(oldMallListVo.getName());
                String pic = StringUtils.isNotBlank(oldMallListVo.getListPicture()) ? oldMallListVo.getListPicture() : oldMallListVo.getMainPicture();
                if (StringUtils.isNotBlank(pic))
                    storeProduct.setImage(pic.replace(prefix, ""));
                else
                    storeProduct.setImage("");

                List<String> picturePathList = oldMallListVo.getPicturePathList ();
                if (CollUtil.isNotEmpty(picturePathList)) {
                    List<String> collect = picturePathList.stream().map(s -> "\"" + s.replace(prefix, "") + "\"").collect(Collectors.toList());
                    storeProduct.setSliderImage(collect.toString());
                } else if (StringUtils.isNotBlank(pic)) {
                    storeProduct.setSliderImage(Lists.newArrayList("\"" + pic.replace(prefix, "") + "\"")   .toString());
                } else {
                    storeProduct.setSliderImage("");
                }

                storeProduct.setSpecType(false);
                storeProduct.setStoreInfo(oldMallListVo.getName());
                storeProduct.setIsPostage(false);
                storeProduct.setPostage(new BigDecimal(10));
                storeProduct.setIsShow(false);
                storeProduct.setUnitName("件");
                storeProduct.setAddTime(DateUtil.getNowTime());
                storeProduct.setStock(999);
                storeProduct.setTempId(2);
                storeProduct.setCateId("1668");
                storeProduct.setMerId(0);
                storeProduct.setKeyword(oldMallListVo.getName());

                //价格数据
                storeProduct.setPrice(BigDecimal.valueOf(0 != oldMallListVo.getRetailPrice() ? oldMallListVo.getRetailPrice() : oldMallListVo.getSupplyPrice() * 1.8));
                storeProduct.setOtPrice(BigDecimal.valueOf(oldMallListVo.getSupplyPrice() * 2));
                storeProduct.setCost(BigDecimal.valueOf(oldMallListVo.getSupplyPrice()));

                storeProductService.save(storeProduct);

                //商品描述
                StoreProductDescription storeProductDescription = new StoreProductDescription();
                String sliderImage = storeProduct.getSliderImage();

                StringBuilder stringBuilder = new StringBuilder();
                if (StringUtils.isNotBlank(sliderImage)) {
                    sliderImage = sliderImage.replaceAll("\\[", "");
                    sliderImage = sliderImage.replaceAll("]", "");
                    String[] split = StringUtils.split(sliderImage, ",");

                    stringBuilder.append("<p>");
                    Arrays.asList(split).forEach(s -> {
                        stringBuilder.append("<img src=" + s + "/> ");
                    });
                    stringBuilder.append("</p>");
                }

                storeProductDescription.setDescription(
                        (StringUtils.isNotBlank(oldMallListVo.getFirstDetails()) ? "<p>" + oldMallListVo.getFirstDetails() + "</p>" : "") +
                                (StringUtils.isNotBlank(oldMallListVo.getSecondDetails()) ? "<p>" + oldMallListVo.getSecondDetails() + "</p>" : "") +
                                (StringUtils.isNotBlank(oldMallListVo.getThirdDetails()) ? "<p>" + oldMallListVo.getThirdDetails() + "</p>" : "") +
                                (stringBuilder.toString().contains("<img") ? stringBuilder.toString() : ""));

                storeProductDescription.setProductId(storeProduct.getId());
                storeProductDescription.setType(ProductType.PRODUCT_TYPE_NORMAL);
                storeProductDescriptionService.save(storeProductDescription);


                //规格数据
                StoreProductAttr storeProductAttr = new StoreProductAttr();
                storeProductAttr.setProductId(storeProduct.getId());
                storeProductAttr.setAttrName("规格");
                storeProductAttr.setAttrValues("默认");
                storeProductAttr.setType(0);

                storeProductAttrService.save(storeProductAttr);

                StoreProductAttrValue storeProductAttrValue = new StoreProductAttrValue();
                storeProductAttrValue.setProductId(storeProduct.getId());
                storeProductAttrValue.setSuk("默认");
                storeProductAttrValue.setSales(0);
                storeProductAttrValue.setImage(storeProduct.getImage());
                storeProductAttrValue.setStock(9999);
                storeProductAttrValue.setWeight(new BigDecimal(1));
                storeProductAttrValue.setVolume(new BigDecimal(1));
                storeProductAttrValue.setAttrValue("{\"规格\":\"默认\"}");
                storeProductAttrValue.setBrokerage(new BigDecimal(0));
                storeProductAttrValue.setBrokerageTwo(new BigDecimal(0));

                storeProductAttrValue.setPrice(storeProduct.getPrice());
                storeProductAttrValue.setCost(storeProduct.getCost());
                storeProductAttrValue.setOtPrice(storeProduct.getOtPrice());

                storeProductAttrValueService.save(storeProductAttrValue);
            }
        });
    }

    private List<OldMallListVo> getList() {
        String url = "https://www.bankservice.shop/prod-api/card/commodity/list";

        // 请求体
        String body = "{\"state\": 0}";
        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        // 请求
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // 使用RestTemplate请求
        List<OldMallListVo> total = Lists.newArrayList();

        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject httpBody = responseBody.getBody();
        if (httpBody != null && httpBody.getInteger("code") == 0) {
            JSONArray jsonArray = httpBody.getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject itemObject = jsonArray.getJSONObject(i);
                OldMallListVo oldMallListVo = JSONObject.parseObject(JSONObject.toJSONString(itemObject), OldMallListVo.class);
                total.add(oldMallListVo);
            }
        }
        return total;
    }
}
