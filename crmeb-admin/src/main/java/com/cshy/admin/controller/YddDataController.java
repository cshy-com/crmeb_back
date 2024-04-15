package com.cshy.admin.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.log.Log;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cshy.common.constants.ProductType;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.dto.BrandDto;
import com.cshy.common.model.entity.category.Category;
import com.cshy.common.model.entity.product.StoreProduct;
import com.cshy.common.model.entity.product.StoreProductAttr;
import com.cshy.common.model.entity.product.StoreProductAttrValue;
import com.cshy.common.model.entity.product.StoreProductDescription;
import com.cshy.common.model.entity.system.Brand;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @ApiOperation(value = "同步")
    @RequestMapping(value = "/sync", method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Object> sync() {
        Integer current = 1;

        String token = getToken();

        log.info("开始获取分类数据");
        //获取分类数据
        List<YddCategoryVo> categoryList = getCategoryList(token);

        //保存分类数据
        saveCategoryList(categoryList);
        log.info("获取分类数据完成");

        //获取品牌数据
        log.info("开始获取品牌数据");
        List<YddBrandVo> brandList = getBrandList(token);

        //保存品牌数据
        brandList.forEach(brandVo -> {
            BrandDto brand = new BrandDto();

            Brand byId = brandService.getById(brandVo.getId());
            if (Objects.nonNull(byId))
                BeanUtils.copyProperties(byId, brand);
            else {
                brand.setId(String.valueOf(brandVo.getId()));
                brand.setStatus(true);
                brand.setIsDel(0);
            }
            brand.setBrandName(brandVo.getName());
            String img = saveImg(brandVo.getIcon(), 0, "product");
            brand.setIcon(img);
            if (Objects.nonNull(byId))
                brandService.updateById(brand);
            else
                brandService.insertWithCustomId(brand);
        });
        log.info("获取品牌数据完成");


        //获取所有商品列表
        log.info("开始获取商品数据数据");
        List<YddProductVo> yddProductVoList = Lists.newArrayList();

        JSONArray total = new JSONArray();
        boolean flag = true;

//        while (flag) {
            //继续请求
            JSONArray dataArray = getList(current, token);
            total.addAll(dataArray);
            if (dataArray.size() < 100) {
                flag = false;
            }
            current++;
//        }
        for (int i = 0; i < total.size(); i++) {
            JSONObject itemObject = total.getJSONObject(i);
            YddProductVo yddProductVo = JSONObject.parseObject(JSONObject.toJSONString(itemObject), YddProductVo.class);
            yddProductVoList.add(yddProductVo);
        }
        log.info("获取商品完成");
        //删除原数据
        List<String> nameList = yddProductVoList.stream().map(YddProductVo::getItemName).collect(Collectors.toList());
        storeProductService.remove(new LambdaQueryWrapper<StoreProduct>().in(StoreProduct::getStoreName, nameList));

        //根据商品id获取商品详情 和规格
        log.info("开始获取商品详情、规格数据");
        yddProductVoList.forEach(yddProductVo -> {
            StoreProduct serviceOne = storeProductService.getOne(new LambdaQueryWrapper<StoreProduct>().eq(StoreProduct::getStoreName, yddProductVo.getItemName()));
            if (Objects.isNull(serviceOne)){
                log.info("正在处理：" + yddProductVo.getItemName() + " 商品数据");
                YddProductDetailVo detail = getDetail(Integer.valueOf(yddProductVo.getId()), token);
                YddSkuDetailVo skuDetail = getSkuDetail(Integer.valueOf(yddProductVo.getId()), token);

                //数据处理和保存
                //基本数据设置
                StoreProduct storeProduct = new StoreProduct();
                storeProduct.setStoreName(detail.getItemName());
                storeProduct.setCateId(String.valueOf(detail.getThirdClassId()));
                storeProduct.setKeyword(detail.getKeywords());
                storeProduct.setUnitName("件");
                storeProduct.setStoreInfo(detail.getItemName());
                storeProduct.setTempId(2);
                storeProduct.setSpecType(false);
                storeProduct.setIsPostage(false);
                storeProduct.setPostage(new BigDecimal(10));
                storeProduct.setIsShow(false);
                storeProduct.setMerId(0);
                storeProduct.setImage("");
                storeProduct.setSliderImage("");
                storeProduct.setAddTime(DateUtil.getNowTime());
                storeProduct.setStock(9999);

                //价格设置
                storeProduct.setCost(BigDecimal.valueOf(Double.parseDouble(skuDetail.getSkuCostPrice()) / 100));
                storeProduct.setOtPrice(BigDecimal.valueOf(Double.parseDouble(skuDetail.getSkuCostPrice()) / 100 * 2));
                storeProduct.setPrice(BigDecimal.valueOf(Double.parseDouble(skuDetail.getSkuCostPrice()) / 100 * 1.8));
                storeProduct.setVipPrice(BigDecimal.valueOf(Double.parseDouble(skuDetail.getSkuCostPrice()) / 100 * 1.8));

                log.info("保存数据");
                storeProductService.save(storeProduct);

                //处理图片
                log.info("处理图片");
                String mainImgUrl = saveImg(detail.getImg(), storeProduct.getId(), "product");
                List<String> bannerAndVideoList = Lists.newArrayList();
                if (CollUtil.isNotEmpty(detail.getBannerAndVideoList())) {
                    detail.getBannerAndVideoList().forEach(bannerAndVideo -> {
                        String bannerUrl = saveImg(bannerAndVideo.getUrl(), storeProduct.getId(), "product");
                        bannerAndVideoList.add("\"" + bannerUrl + "\"");
                    });
                }

                //更新数据
                log.info("更新数据");
                StoreProduct newProduct = storeProductService.getById(storeProduct.getId());
                newProduct.setImage(mainImgUrl);
                if (CollUtil.isNotEmpty(bannerAndVideoList))
                    newProduct.setSliderImage(bannerAndVideoList.toString());

                storeProductService.updateById(newProduct);

                //商品描述
                StoreProductDescription storeProductDescription = new StoreProductDescription();
                storeProductDescription.setDescription(detail.getContents());
                storeProductDescription.setProductId(newProduct.getId());
                storeProductDescription.setType(ProductType.PRODUCT_TYPE_NORMAL);
                storeProductDescriptionService.save(storeProductDescription);

                //插入默认规格数据
                log.info("规格数据");
                StoreProductAttr storeProductAttr = new StoreProductAttr();
                storeProductAttr.setProductId(newProduct.getId());
                storeProductAttr.setAttrName("规格");
                storeProductAttr.setAttrValues("默认");
                storeProductAttr.setType(0);

                storeProductAttrService.save(storeProductAttr);

                StoreProductAttrValue storeProductAttrValue = new StoreProductAttrValue();
                storeProductAttrValue.setProductId(newProduct.getId());
                storeProductAttrValue.setSuk("默认");
                storeProductAttrValue.setSales(0);
                storeProductAttrValue.setImage(newProduct.getImage());
                storeProductAttrValue.setStock(9999);
                storeProductAttrValue.setWeight(new BigDecimal(1));
                storeProductAttrValue.setVolume(new BigDecimal(1));
                storeProductAttrValue.setAttrValue("{\"规格\":\"默认\"}");
                storeProductAttrValue.setBrokerage(new BigDecimal(0));
                storeProductAttrValue.setBrokerageTwo(new BigDecimal(0));

                storeProductAttrValue.setPrice(newProduct.getPrice());
                storeProductAttrValue.setCost(newProduct.getCost());
                storeProductAttrValue.setOtPrice(newProduct.getOtPrice());

                storeProductAttrValueService.save(storeProductAttrValue);
                log.info("完成");
            }
        });

        log.info("同步完成");
        return CommonResult.success();
    }

    private void saveCategoryList(List<YddCategoryVo> categoryList) {
        //一级分类数据
        categoryList.forEach(parentVo -> {
            Category parent = new Category();

            Category cat1 = categoryService.getById(parentVo.getId());
            if (Objects.nonNull(cat1)) {
                BeanUtils.copyProperties(cat1, parent);
            } else {
                parent.setId(parentVo.getId());
                parent.setType(1);
                parent.setStatus(true);
                parent.setSort(0);
                parent.setPath("/0/");
                parent.setPid(0);
            }
            parent.setName(parentVo.getName());

            String imgUrl = saveImg(parentVo.getIcon(), 0, "product");
            parent.setExtra(imgUrl);
            if (Objects.nonNull(cat1))
                categoryService.saveOrUpdate(parent);
            else
                categoryService.insertWithCustomId(parent);
            //二级分类数据
            parentVo.getChildren().forEach(secondVo -> {
                Category second = new Category();
                Category cat2 = categoryService.getById(secondVo.getId());
                if (Objects.nonNull(cat2)) {
                    BeanUtils.copyProperties(cat2, second);
                } else {
                    second.setId(secondVo.getId());
                    second.setType(1);
                    second.setStatus(true);
                    second.setSort(0);
                    second.setPath("/0/" + parent.getId());
                    second.setPid(parent.getId());
                }
                second.setName(secondVo.getName());

                String secondImgUrl = saveImg(secondVo.getIcon(), 0, "product");
                second.setExtra(secondImgUrl);
                if (Objects.nonNull(cat2))
                    categoryService.saveOrUpdate(second);
                else
                    categoryService.insertWithCustomId(second);
                //三级分类数据
                secondVo.getChildren().forEach(thirdVo -> {
                    Category third = new Category();
                    Category cat3 = categoryService.getById(thirdVo.getId());
                    if (Objects.nonNull(cat3)) {
                        BeanUtils.copyProperties(cat3, third);
                    } else {
                        third.setId(thirdVo.getId());
                        third.setType(1);
                        third.setStatus(true);
                        third.setSort(0);
                        third.setPath("/0/" + parent.getId() + "/" + second.getId());
                        third.setPid(second.getId());
                    }
                    third.setName(thirdVo.getName());

                    String thirdImgUrl = saveImg(thirdVo.getIcon(), 0, "product");
                    third.setExtra(thirdImgUrl);
                    if (Objects.nonNull(cat3))
                        categoryService.saveOrUpdate(third);
                    else
                        categoryService.insertWithCustomId(third);
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

    private List<YddCategoryVo> getCategoryList(String token) {
        String url = "https://cshy.org.yddstore.com/api/admin/category/auth/page";
        // 请求体
        String body = "{\"name\":\"\",\"current\":1,\"size\":20}\n";

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Token", token);

        // 请求
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // 使用RestTemplate请求
        List<YddCategoryVo> total = Lists.newArrayList();

        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject httpBody = responseBody.getBody();
        if (httpBody != null && httpBody.getInteger("code") == 0) {
            JSONArray jsonArray = httpBody.getJSONObject("data").getJSONArray("data");
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

    private void getSecondCategory(String token, Integer id, YddCategoryVo parent) {
        String url = "https://cshy.org.yddstore.com/api/admin/category/v1/auth/list";
        // 请求体
        String body = "{\"pid\":" + id + "}";

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Token", token);

        // 请求
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // 使用RestTemplate请求
        List<YddCategoryVo> total = Lists.newArrayList();
        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject httpBody = responseBody.getBody();
        if (httpBody != null && httpBody.getInteger("code") == 0) {
            JSONArray jsonArray = httpBody.getJSONArray("data");
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

    private void getThirdCategory(String token, Integer id, YddCategoryVo parent) {
        String url = "https://cshy.org.yddstore.com/api/admin/category/v1/auth/list";
        // 请求体
        String body = "{\"pid\":" + id + "}";

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Token", token);

        // 请求
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // 使用RestTemplate请求
        List<YddCategoryVo> total = Lists.newArrayList();
        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject httpBody = responseBody.getBody();
        if (httpBody != null && httpBody.getInteger("code") == 0) {
            JSONArray jsonArray = httpBody.getJSONArray("data");
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

    private YddSkuDetailVo getSkuDetail(Integer id, String token) {
        String url = "https://cshy.org.yddstore.com/api/admin/item/v1/auth/skuDetail";
        // 请求体
        String body = "{\"id\":" + id + "}";

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Token", token);

        // 请求
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // 使用RestTemplate请求
        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject httpBody = responseBody.getBody();
        if (httpBody != null && httpBody.getInteger("code") == 0) {
            JSONArray data = httpBody.getJSONArray("data");
            YddSkuDetailVo skuDetailVo = JSONObject.parseObject(data.getJSONObject(0).toJSONString(), YddSkuDetailVo.class);
            return skuDetailVo;
        }
        throw new CrmebException("获取sku详情失败");
    }

    private YddProductDetailVo getDetail(Integer id, String token) {
        String url = "https://cshy.org.yddstore.com/api/admin/item/v1/auth/detail";
        // 请求体
        String body = "{\"id\":" + id + "}";

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Token", token);

        // 请求
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // 使用RestTemplate请求
        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject httpBody = responseBody.getBody();
        if (httpBody != null && httpBody.getInteger("code") == 0) {
            JSONObject data = httpBody.getJSONObject("data");
            YddProductDetailVo yddProductDetailVo = JSONObject.parseObject(data.toJSONString(), YddProductDetailVo.class);
            return yddProductDetailVo;
        }
        throw new CrmebException("获取详情失败");
    }

    private String getToken() {
        String url = "https://cshy.org.yddstore.com/api/admin/user/login";
        // 请求体
        String body = "{\n" +
                "    \"phone\": \"15285143252\",\n" +
                "    \"password\": \"xpz15285143252\"\n" +
                "}";

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");


        // 请求
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // 使用RestTemplate请求
        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject httpBody = responseBody.getBody();

        if (httpBody != null && httpBody.getInteger("code") == 0) {
            JSONObject data = httpBody.getJSONObject("data");
            return data.getString("token");
        }
        throw new CrmebException("获取token失败");
    }

    private JSONArray getList(Integer current, String token) {
        String url = "https://cshy.org.yddstore.com/api/admin/item/v1/auth/page";
        // 请求体
        String body = "{\"brandId\":\"\",\"itemName\":\"\",\"firstClassId\":\"\",\"secondClassId\":\"\",\"thirdClassId\":\"\",\"shelfStatus\":\"\",\"current\":" + current + ",\"size\":100}";

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Token", token);

        // 请求
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // 使用RestTemplate请求
        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject httpBody = responseBody.getBody();
        if (httpBody != null && httpBody.getInteger("code") == 0) {
            return httpBody.getJSONObject("data").getJSONArray("data");
        }
        throw new CrmebException("获取List失败");
    }

    private String saveImg(String urlStr, Integer pid, String model) {
        if (StringUtils.isNotBlank(urlStr)) {
            String localPath = "C:\\Users\\admin\\Desktop\\file\\";
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
                throw new CrmebException("上传文件失败");
            }
        }
        return null;
    }
}
