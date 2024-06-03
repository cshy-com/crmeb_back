package com.cshy.admin.controller;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cshy.common.constants.ProductType;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.entity.category.Category;
import com.cshy.common.model.entity.product.StoreProduct;
import com.cshy.common.model.entity.product.StoreProductAttr;
import com.cshy.common.model.entity.product.StoreProductAttrValue;
import com.cshy.common.model.entity.product.StoreProductDescription;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.FileResultVo;
import com.cshy.common.model.vo.ydd.*;
import com.cshy.common.utils.DateUtil;
import com.cshy.common.utils.StringUtils;
import com.cshy.service.service.BrandService;
import com.cshy.service.service.UploadService;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Api(value = "v2 -- 云搭档", tags = "v2 -- 云搭档")
@RestController
@AllArgsConstructor
@RequestMapping("api/admin/ydd")
@Slf4j
public class YddDataController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    UploadService uploadService;

    @Autowired
    SystemAttachmentService systemAttachmentService;

    @Autowired
    StoreProductAttrService storeProductAttrService;

    @Autowired
    StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    StoreProductDescriptionService storeProductDescriptionService;

    private static final String testAddress = "http://open.test.yqtbuy.com";

    private static final String proAddress = "https://open.yqtbuy.com";

    private final List<String> filter = Lists.newArrayList("生鲜",
            "酒类",
            "珠宝首饰",
            "钟表",
            "医疗保健",
            "家装建材",
            "美妆护肤",
            "测试分类(请勿11修改)",
            "测试分类01",
            "卖家服务 ",
            "二手商品",
            "wh1",
            "医药健康>",
            "工业商城",
            "IP",
            "医药");

    @ApiOperation(value = "同步")
    @RequestMapping(value = "/sync", method = RequestMethod.POST)
    @Async
    public CommonResult<Object> sync(@RequestParam String token) {
        synchronize(token);
        return CommonResult.success();
    }

    void synchronize(String token) {
//        //查询本地分类
        List<Category> localCategoryList = categoryService.list(new LambdaQueryWrapper<Category>().eq(Category::getType, 1).like(Category::getPath, "/%/%/"));
//
//        log.info("开始获取分类数据");
//        //获取分类数据
//        List<YddCategoryVo> categoryList = getCategoryList(token);
//
//        List<YddCategoryVo> filterList = categoryList.stream().filter(category -> !filter.contains(category.getName())).collect(Collectors.toList());
//        //保存分类数据
//        saveCategoryList(filterList);
//        log.info("获取分类数据完成");

        //获取所有商品列表
        log.info("开始获取商品数据数据");
        List<YddProductVo> yddProductVoList = Lists.newArrayList();


        JSONArray dataArray = getProductList(token);
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject itemObject = dataArray.getJSONObject(i);
            YddProductVo yddProductVo = JSONObject.parseObject(JSONObject.toJSONString(itemObject), YddProductVo.class);
            yddProductVoList.add(yddProductVo);
        }
        log.info("获取商品完成");

        buildAndSave(yddProductVoList, token, localCategoryList);
    }

    private void buildAndSave(List<YddProductVo> yddProductVoList, String token, List<Category> localCategoryList) {
        List<String> nameList = localCategoryList.stream().map(Category::getName).collect(Collectors.toList());
        yddProductVoList.forEach(yddProductVo -> {
            StoreProduct serviceOne = storeProductService.getOne(new LambdaQueryWrapper<StoreProduct>().eq(StoreProduct::getStoreName, yddProductVo.getName()));
            if (Objects.isNull(serviceOne)) {
                log.info("正在处理：" + yddProductVo.getName() + " 商品数据");
                YddProductDetailVo detail = getDetail(yddProductVo.getGoodsId(), token);

                //数据处理和保存
                //基本数据设置
                StoreProduct storeProduct = new StoreProduct();
                storeProduct.setStoreName(detail.getName());
                //分类设置
                if (!nameList.contains(detail.getI3Category()))
                    return;
                Optional<Category> first = localCategoryList.stream().filter(ca -> ca.getName().equals(detail.getI3Category())).findFirst();
                first.ifPresent(category -> storeProduct.setCateId(String.valueOf(category.getId())));

                storeProduct.setKeyword(detail.getName());
                storeProduct.setUnitName(detail.getUnit());
                storeProduct.setStoreInfo(detail.getDesc());
                storeProduct.setTempId(1);
                storeProduct.setSpecType(false);
                storeProduct.setIsPostage(true);
                storeProduct.setPostage(new BigDecimal(0));
                storeProduct.setIsShow(true);
                storeProduct.setMerId(0);
                storeProduct.setAddTime(DateUtil.getNowTime());
                if (detail.getStock() > 99999)
                    storeProduct.setStock(99999);
                else
                    storeProduct.setStock(detail.getStock());

                //价格设置
                /**
                 * 利润率35%<40%
                 * 会员价*1.3（给甲方的成本价）
                 * 会员价*1.65 （销售价）
                 *
                 * 利润率40%=<50%
                 * 会员价*1.4（给甲方的成本价）
                 * 会员价*1.8 （销售价）
                 *
                 *
                 * 利润率50%=<
                 * 会员价*1.5（给甲方的成本价）
                 * 会员价*1.95 （销售价）
                 */
                // 移除百分号
                String rateString = yddProductVo.getTotalProfitRate().replace("%", "");

                // 将字符串转换为 double 类型
                double percentage = Double.parseDouble(rateString);

                // 将百分数转换为小数
                double rate = percentage / 100.0;
                double costPrice = Double.parseDouble(detail.getCostPrice());
                long roundedPrice = Math.round(costPrice);

                BigDecimal costPriceBigDecimal;
                BigDecimal marketPriceBigDecimal;
                if (0.35 <= rate && rate < 0.4) {
                    costPriceBigDecimal = BigDecimal.valueOf(roundedPrice * 1.3);
                    marketPriceBigDecimal = BigDecimal.valueOf(roundedPrice * 1.65 + 10);
                } else if (0.40 <= rate && rate < 50) {
                    costPriceBigDecimal = BigDecimal.valueOf(roundedPrice * 1.4);
                    marketPriceBigDecimal = BigDecimal.valueOf(roundedPrice * 1.8 + 10);
                } else if (50 <= rate) {
                    costPriceBigDecimal = BigDecimal.valueOf(roundedPrice * 1.5);
                    marketPriceBigDecimal = BigDecimal.valueOf(roundedPrice * 1.95 + 10);
                }else {
                    costPriceBigDecimal = BigDecimal.valueOf(roundedPrice * 1.5);
                    marketPriceBigDecimal = BigDecimal.valueOf(roundedPrice * 2 + 10);
                }

                storeProduct.setCost(costPriceBigDecimal);
                storeProduct.setOtPrice(marketPriceBigDecimal);
                storeProduct.setPrice(marketPriceBigDecimal);
                storeProduct.setVipPrice(marketPriceBigDecimal);

                log.info("保存数据");

                //处理图片
                log.info("处理图片");
                String mainImgUrl = detail.getMainImg();
                storeProduct.setImage(mainImgUrl);
                if (CollUtil.isNotEmpty(detail.getImages())) {
                    List<String> images = detail.getImages();
                    storeProduct.setSliderImage(JSONObject.toJSONString(images));
                }

                storeProduct.setIsDeliver(true);
                storeProduct.setIsPickup(false);

                storeProduct.setBarCode(detail.getSkuSn());

                storeProductService.save(storeProduct);

                //商品描述
                StoreProductDescription storeProductDescription = new StoreProductDescription();
                List<YddProductDetailVo.DetailImage> detailImages = detail.getDetailImages();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("<p>");
                detailImages.forEach(detailImage -> {
                    stringBuilder.append("<img src=\"").append(detailImage.getUrl()).append("\"/>");
                });
                stringBuilder.append("</p>");
                storeProductDescription.setDescription(stringBuilder.toString());
                storeProductDescription.setProductId(storeProduct.getId());
                storeProductDescription.setType(ProductType.PRODUCT_TYPE_NORMAL);
                storeProductDescriptionService.save(storeProductDescription);

                //插入默认规格数据
                log.info("规格数据");
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
                storeProductAttrValue.setStock(storeProduct.getStock());
                storeProductAttrValue.setWeight(new BigDecimal(1));
                storeProductAttrValue.setVolume(new BigDecimal(1));
                storeProductAttrValue.setAttrValue("{\"规格\":\"默认\"}");
                storeProductAttrValue.setBrokerage(new BigDecimal(0));
                storeProductAttrValue.setBrokerageTwo(new BigDecimal(0));
                storeProductAttrValue.setBarCode(detail.getSkuSn());

                storeProductAttrValue.setPrice(storeProduct.getPrice());
                storeProductAttrValue.setCost(storeProduct.getCost());
                storeProductAttrValue.setOtPrice(storeProduct.getOtPrice());

                storeProductAttrValueService.save(storeProductAttrValue);
                log.info("完成");
            }
        });
    }

    private YddProductDetailVo getDetail(String goodsId, String token) {
        String url = "https://dist.yqtyun.com/api/distributor/product/getProductDetail";
        // 请求体
        String body = "{\"goods_id\":\"" + goodsId + "\"}";

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Authorization", token);

        // 请求
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // 使用RestTemplate请求
        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject httpBody = responseBody.getBody();
        if (httpBody != null && httpBody.getInteger("code") == 10000) {
            JSONObject data = httpBody.getJSONObject("data");
            YddProductDetailVo yddProductDetailVo = JSONObject.parseObject(JSONObject.toJSONString(data), YddProductDetailVo.class);
            return yddProductDetailVo;
        }
        throw new CrmebException("查询商品失败");
    }

    private void saveCategoryList(List<YddCategoryVo> categoryList) {
        //一级分类数据
        categoryList.forEach(parentVo -> {
            Category parent = new Category();

            Category cat1 = categoryService.getById(parentVo.getId());
            if (Objects.nonNull(cat1)) {
                BeanUtils.copyProperties(cat1, parent);
            } else {
                parent.setType(1);
                parent.setStatus(true);
                parent.setSort(0);
                parent.setPath("/0/");
                parent.setPid(0);
                parent.setExtra("crmebimage/public/maintain/def_category_logo.jpg");
            }
            parent.setName(parentVo.getName());

            if (Objects.nonNull(cat1))
                categoryService.saveOrUpdate(parent);
            else
                categoryService.save(parent);
            //二级分类数据
            parentVo.getChildren().forEach(secondVo -> {
                Category second = new Category();
                Category cat2 = categoryService.getById(secondVo.getId());
                if (Objects.nonNull(cat2)) {
                    BeanUtils.copyProperties(cat2, second);
                } else {
                    second.setType(1);
                    second.setStatus(true);
                    second.setSort(0);
                    second.setPath("/0/" + parent.getId());
                    second.setPid(parent.getId());
                    second.setExtra("crmebimage/public/maintain/def_category_logo.jpg");

                }
                second.setName(secondVo.getName());

                if (Objects.nonNull(cat2))
                    categoryService.saveOrUpdate(second);
                else
                    categoryService.save(second);
                //三级分类数据
                secondVo.getChildren().forEach(thirdVo -> {
                    Category third = new Category();
                    Category cat3 = categoryService.getById(thirdVo.getId());
                    if (Objects.nonNull(cat3)) {
                        BeanUtils.copyProperties(cat3, third);
                    } else {
                        third.setType(1);
                        third.setStatus(true);
                        third.setSort(0);
                        third.setPath("/0/" + parent.getId() + "/" + second.getId());
                        third.setPid(second.getId());
                        third.setExtra("crmebimage/public/maintain/def_category_logo.jpg");

                    }
                    third.setName(thirdVo.getName());

                    if (Objects.nonNull(cat3))
                        categoryService.saveOrUpdate(third);
                    else
                        categoryService.save(third);
                });
            });
        });
    }

    private List<YddBrandVo> getBrandList(String token) {
        String url = "https://cshy.org.yddstore.com/api/admin/brand/auth/page";

        String body = "{\"brandName\":\"\",\"current\":1,\"size\":100}";

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Token", token);

        // 请求
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        List<YddBrandVo> total = Lists.newArrayList();

        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject httpBody = responseBody.getBody();
        if (httpBody != null && httpBody.getInteger("code") == 0) {
            JSONArray jsonArray = httpBody.getJSONObject("data").getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject itemObject = jsonArray.getJSONObject(i);
                YddBrandVo yddBrandVo = JSONObject.parseObject(JSONObject.toJSONString(itemObject), YddBrandVo.class);
                total.add(yddBrandVo);
            }
            return total;
        }
        throw new CrmebException("获取品牌数据失败");
    }

    private static List<YddCategoryVo> getCategoryList(String token) {
        String url = "https://dist.yqtyun.com/api/distributor/product/getCategoryList";

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Authorization", token);

        String body = "{\"parent_id\":0}";
        // 请求
        HttpEntity<String> request = new HttpEntity<>(body, headers);


        // 使用RestTemplate请求
        List<YddCategoryVo> total = Lists.newArrayList();

        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject httpBody = responseBody.getBody();
        if (httpBody != null && httpBody.getInteger("code") == 10000) {
            JSONArray jsonArray = httpBody.getJSONObject("data").getJSONArray("items");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject itemObject = jsonArray.getJSONObject(i);
                YddCategoryVo yddCategoryVo = JSONObject.parseObject(JSONObject.toJSONString(itemObject), YddCategoryVo.class);
                //获取二级数据
                getSecondCategory(token, yddCategoryVo.getId(), yddCategoryVo);
                total.add(yddCategoryVo);
            }
            return total;
        }
        throw new CrmebException("获取一级分类失败");
    }

    private static void getSecondCategory(String token, Integer id, YddCategoryVo parent) {
        String url = "https://dist.yqtyun.com/api/distributor/product/getCategoryList";
        // 请求体
        String body = "{\"parent_id\":" + id + "}";

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Authorization", token);

        // 请求
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // 使用RestTemplate请求
        List<YddCategoryVo> total = Lists.newArrayList();
        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject httpBody = responseBody.getBody();
        if (httpBody != null && httpBody.getInteger("code") == 10000) {
            JSONArray jsonArray = httpBody.getJSONObject("data").getJSONArray("items");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject itemObject = jsonArray.getJSONObject(i);
                YddCategoryVo yddCategoryVo = JSONObject.parseObject(JSONObject.toJSONString(itemObject), YddCategoryVo.class);
                //获取三级数据
                getThirdCategory(token, yddCategoryVo.getId(), yddCategoryVo);
                total.add(yddCategoryVo);
            }
            parent.setChildren(total);
            return;
        }
        throw new CrmebException("获取二级分类失败");

    }

    private static void getThirdCategory(String token, Integer id, YddCategoryVo parent) {
        String url = "https://dist.yqtyun.com/api/distributor/product/getCategoryList";
        // 请求体
        String body = "{\"parent_id\":" + id + "}";

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Authorization", token);

        // 请求
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // 使用RestTemplate请求
        List<YddCategoryVo> total = Lists.newArrayList();
        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject httpBody = responseBody.getBody();
        if (httpBody != null && httpBody.getInteger("code") == 10000) {
            JSONArray jsonArray = httpBody.getJSONObject("data").getJSONArray("items");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject itemObject = jsonArray.getJSONObject(i);
                YddCategoryVo yddCategoryVo = JSONObject.parseObject(JSONObject.toJSONString(itemObject), YddCategoryVo.class);
                total.add(yddCategoryVo);
            }
            parent.setChildren(total);
            return;
        }
        throw new CrmebException("获取三级分类失败");

    }

    private static String getToken() {
        String url = testAddress + "/open/v2/login/getAccessToken";
        // 请求体
        Map<String, Object> params = new HashMap<>();
        params.put("appId", "554dd352c4185966e04bf6f3");
        params.put("secret", "rptx7q4ixyoldn2ikhbdt3t13657387v");

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");


        // 请求
        HttpEntity<String> request = new HttpEntity<>(JSONObject.toJSONString(params), headers);

        // 使用RestTemplate请求
        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject httpBody = responseBody.getBody();

        if (httpBody != null && httpBody.getInteger("code") == 10000) {
            JSONObject data = httpBody.getJSONObject("data");
            return data.getString("accessToken");
        }
        throw new CrmebException("获取token失败");
    }

    private JSONArray getProductList(String token) {
        int page = 1;
        int total = 0;
        JSONArray jsonArray = new JSONArray();
        String url = "https://dist.yqtyun.com/api/distributor/product/getProductPageListV2";
        while (total == 0 || total != jsonArray.size()) {
            try {
                // 请求体
                String body = "{\"price_type\":2,\"price_min\":\"\",\"price_max\":\"1500\",\"profit_rate_type\":1,\"total_profit_rate_min\":\"35\",\"total_profit_rate_max\":\"\",\"is_enable\":\"\",\"keyword_type\":0,\"keyword\":\"\",\"address\":\"\",\"page_size\":2000,\"total\":0,\"page\":" + page + ",\"has_stock\":\"\",\"cost_price_sort\":\"\",\"market_price_sort\":\"\",\"province_id\":\"\",\"city_id\":\"\",\"area_id\":\"\",\"dg_city_id\":[],\"type\":\"\",\"tag_type\":\"4\",\"group_id\":\"\",\"page_sort\":0,\"page_offset\":0}";

                // 请求头
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", "application/json;charset=UTF-8");
                headers.add("Authorization", token);

                // 请求
                HttpEntity<String> request = new HttpEntity<>(body, headers);

                // 使用RestTemplate请求
                RestTemplate restTemplateHttps = new RestTemplate();
                ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
                JSONObject httpBody = responseBody.getBody();
                if (httpBody != null && httpBody.getInteger("code") == 10000) {
                    if (!httpBody.getString("msg").equals("无效的access_token")){
                        jsonArray.addAll(httpBody.getJSONObject("data").getJSONArray("items"));
                        total = httpBody.getJSONObject("data").getInteger("total");
                        page += 1;
                    } else
                        throw new CrmebException("token过期");
                }
                else
                    throw new CrmebException("token过期");
            } catch (Exception e) {
                throw new CrmebException("获取List失败");
            }
        }
        return jsonArray;
    }

    private String saveImg(String urlStr, Integer pid, String model) {
        if (StringUtils.isNotBlank(urlStr)) {
            String localPath = "/root/new_mall/temp_file/";
            UUID randomUUID = UUID.randomUUID();
            String uuid = randomUUID.toString().replace("-", "");
            String suffix = urlStr.substring(urlStr.lastIndexOf("."));
            String fullPath = localPath + uuid + suffix;
            URL url = null;
            if (suffix.equals(".jpg") || suffix.equals(".png") || suffix.equals(".gif")) {
                try {
                    url = new URL(urlStr);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }

                try (InputStream inputStream = url.openStream(); FileOutputStream fileOutputStream = new FileOutputStream(fullPath);) {

                    // 下载文件到本地
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }

                    File file = new File(fullPath);
                    FileInputStream fileInputStream = new FileInputStream(file);
                    MultipartFile multipartFile = new org.springframework.mock.web.MockMultipartFile(
                            "file",
                            file.getName(),
                            suffix.equals(".jpg") ? "image/jpeg" : suffix.equals(".png") ? "image/png" : "image/gif",
                            fileInputStream);

                    FileResultVo fileResultVo = uploadService.imageUpload(multipartFile, model, pid);
                    fileInputStream.close();
                    boolean delete = file.delete();
                    return systemAttachmentService.clearPrefix(fileResultVo.getUrl());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                throw new CrmebException("上传文件失败");
            }
        }
        return null;
    }
}
