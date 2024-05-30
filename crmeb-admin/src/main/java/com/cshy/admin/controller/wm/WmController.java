package com.cshy.admin.controller.wm;

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
import com.cshy.common.model.entity.user.User;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.FileResultVo;
import com.cshy.common.utils.DateUtil;
import com.cshy.common.utils.StringUtils;
import com.cshy.service.service.UploadService;
import com.cshy.service.service.category.CategoryService;
import com.cshy.service.service.store.StoreProductAttrService;
import com.cshy.service.service.store.StoreProductAttrValueService;
import com.cshy.service.service.store.StoreProductDescriptionService;
import com.cshy.service.service.store.StoreProductService;
import com.cshy.service.service.system.SystemAttachmentService;
import com.cshy.service.service.user.UserService;
import com.google.common.collect.Lists;
import com.qcloud.cos.utils.IOUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.xm.Similarity;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 微盟同步
 */
@Slf4j
@AllArgsConstructor
@RestController("WmController")
@RequestMapping("api/admin/wm")
@Api(value = "v2 -- 微盟同步", tags = "v2 -- 微盟同步")
public class WmController {
    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private UserService userService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StoreProductDescriptionService storeProductDescriptionService;

    @Autowired
    private StoreProductAttrService storeProductAttrService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    private static final String pid = "4021792237678";

    private static final String storeId = "6016272592678";

    @ApiOperation(value = "同步")
    @RequestMapping(value = "/sync", method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Object> sync() {
        //获取apiToken
        String accessToken = getToken();
        //获取用户
//        List<JSONObject> userJsonList = searchCustomer(accessToken);
//        convertUser(userJsonList);
        //查询所有商品
        List<JSONObject> goodsJsonList = queryGoodsList(accessToken);
//        //查询分类
        List<Category> categoryList = categoryService.list(new LambdaQueryWrapper<Category>().eq(Category::getType, 1).like(Category::getPath, "/%/%/"));
//        //分别获取商品详情
        goodsJsonList.forEach(goods -> {
            Long goodsId = goods.getLong("goodsId");
            JSONObject goodsDetails = queryGoodsDetail(goodsId, accessToken);
            goodsDetails.put("sales", goods.getInteger("salesNum"));

            try {
                convertGoods(goodsDetails, accessToken, categoryList);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });


        return CommonResult.success();
    }

    private void convertUser(List<JSONObject> userJsonList) {
        userJsonList.forEach(userJson -> {
            User user = new User();
            JSONObject assets = userJson.getJSONObject("assests");
            JSONObject customerInfo = userJson.getJSONObject("customerInfo");
            JSONArray memberCardInfos = userJson.getJSONArray("memberCardInfos");
            String phone = customerInfo.getString("phone");
            if (StringUtils.isNotBlank(phone) && !phone.equals("15172480645")) {
                if (Objects.nonNull(customerInfo)) {
                    //真实姓名
                    user.setRealName(customerInfo.getString("name"));
                    //昵称
                    if (StringUtils.isNotBlank(customerInfo.getString("nickName")))
                        user.setNickname(customerInfo.getString("nickName"));
                    else
                        user.setNickname("微信用户" + phone);

                    //手机号
                    user.setPhone(phone);
                    user.setAccount(phone);
                    //密码
                    user.setPwd("S36hWQPdCb9QURsVFTEytA==");
                }
                if (Objects.nonNull(assets)) {
                    //余额（同步过来为积分）
                    user.setIntegral(Integer.valueOf(assets.getString("currentAmount")));
                }
                if (memberCardInfos.size() != 0) {
                    //注册时间
                    user.setCreateTime(new Date(userJson.getJSONArray("memberCardInfos").getJSONObject(0).getLong("startDate")));
                }
                userService.save(user);
                //头像处理
                try {
                    if (StringUtils.isNotBlank(customerInfo.getString("headUrl")))
                        user.setAvatar(uploadImg(customerInfo.getString("headUrl"), user.getUid(), 1));
                } catch (Exception e) {
                    //TODO 设置默认头像
//                    throw new RuntimeException(e);
                }
                userService.updateById(user);
            }
        });
    }

    private void convertGoods(JSONObject goodsDetail, String accessToken, List<Category> cateList) throws Exception {
        JSONArray skuList = goodsDetail.getJSONArray("skuList");
        if (!skuList.isEmpty()) {
            JSONObject sku = skuList.getJSONObject(0);
            JSONObject singleProduct = sku.getJSONObject("singleProduct");

            StoreProduct storeProduct = new StoreProduct();

            storeProduct.setStoreName(goodsDetail.getString("title"));

            //分类
            JSONArray categoryList = goodsDetail.getJSONArray("categoryList");
            JSONObject category = categoryList.getJSONObject(1);
            storeProduct.setCateId(String.valueOf(matchCategory(category.getString("title"), cateList)));

            storeProduct.setKeyword("");
            storeProduct.setUnitName("件");
            storeProduct.setStoreInfo(goodsDetail.getString("title"));

            //配送
            int freightTemplate = findFreightTemplateList(goodsDetail.getLong("goodsId"), accessToken);
            if (freightTemplate != 0) {
                storeProduct.setTempId(freightTemplate);
                storeProduct.setIsDeliver(true);
            } else {
                storeProduct.setTempId(null);
                storeProduct.setIsDeliver(false);
            }

            //到店自提
            Boolean isDeliver = findDeliveryTypeList(goodsDetail.getLong("goodsId"), accessToken);
            storeProduct.setIsPickup(isDeliver);

            storeProduct.setSpecType(false);
            storeProduct.setIsPostage(false);
            storeProduct.setPostage(new BigDecimal(0));
            storeProduct.setIsShow(goodsDetail.getInteger("isPutAway").equals(0));
            storeProduct.setMerId(0);
            storeProduct.setImage("");
            storeProduct.setSliderImage("");
            storeProduct.setAddTime(DateUtil.getNowTime());
            storeProduct.setStock(sku.getInteger("availableStockNum"));

            //价格设置
            storeProduct.setPrice(goodsDetail.getBigDecimal("salePrice"));
            storeProduct.setCost(goodsDetail.getBigDecimal("costPrice"));
            storeProduct.setOtPrice(goodsDetail.getBigDecimal("originalPrice"));
            storeProduct.setVipPrice(goodsDetail.getBigDecimal("salePrice"));

            log.info("保存数据");
            storeProductService.save(storeProduct);

            //处理图片
            log.info("处理图片");
            log.info("+++++++++++===========================" + goodsDetail.getJSONArray("goodsImageUrl").getString(0));
            String mainImgUrl = uploadImg(goodsDetail.getJSONArray("goodsImageUrl").getString(0), storeProduct.getId(), 0);

            //更新数据
            log.info("更新数据");
            StoreProduct newProduct = storeProductService.getById(storeProduct.getId());
            if (StringUtils.isNotBlank(mainImgUrl)){
                newProduct.setImage(mainImgUrl);
                newProduct.setSliderImage("[\"" + mainImgUrl + "\"]");
            }

            storeProductService.updateById(newProduct);

            //商品描述
            StoreProductDescription storeProductDescription = new StoreProductDescription();
            //使用主图作为描述
            storeProductDescription.setDescription("<p><img src=\"" + mainImgUrl + "\"></p>");
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
            storeProductAttrValue.setSales(goodsDetail.getInteger("sales"));
            storeProductAttrValue.setImage(newProduct.getImage());
            storeProductAttrValue.setStock(sku.getInteger("availableStockNum"));

            JSONObject b2cSku = sku.getJSONObject("b2cSku");
            storeProductAttrValue.setWeight(b2cSku.getBigDecimal("weight"));
            storeProductAttrValue.setVolume(b2cSku.getBigDecimal("volume"));

            storeProductAttrValue.setAttrValue("{\"规格\":\"默认\"}");
            storeProductAttrValue.setBrokerage(new BigDecimal(0));
            storeProductAttrValue.setBrokerageTwo(new BigDecimal(0));

            storeProductAttrValue.setPrice(newProduct.getPrice());
            storeProductAttrValue.setCost(newProduct.getCost());
            storeProductAttrValue.setOtPrice(newProduct.getOtPrice());

            storeProductAttrValueService.save(storeProductAttrValue);
            log.info("完成");
        }

    }

    private Integer matchCategory(String categoryName, List<Category> categoryList) {
        AtomicReference<Double> highestSimilarity = new AtomicReference<>((double) 0);
        AtomicReference<Integer> mostSimilarCategory = new AtomicReference<Integer>(0);

        categoryList.forEach(category -> {
            double similarity = Similarity.charBasedSimilarity(category.getName(), categoryName);
            if (similarity > highestSimilarity.get()) {
                highestSimilarity.set(similarity);
                mostSimilarCategory.set(category.getId());
            }
        });

        return mostSimilarCategory.get();
    }

    /**
     * 查询商品列表
     *
     * @param accessToken
     * @return
     */
    private static List<JSONObject> queryGoodsList(String accessToken) {
        String url = "https://dopen.weimob.com/api/1_0/ec/goods/queryGoodsList?accesstoken=" + accessToken;
        // 请求头
        HttpHeaders headers = new HttpHeaders();

        List<JSONObject> total = Lists.newArrayList();
        int totalCount = 0;
        int pageNum = 1;
        while (totalCount == 0 || total.size() < totalCount) {
            Map<String, Object> params = new HashMap<>();
            params.put("pageNum", pageNum);
            params.put("pageSize", 20);

            // 请求
            HttpEntity<String> request = new HttpEntity<>(JSONObject.toJSONString(params), headers);


            // 使用RestTemplate请求
            RestTemplate restTemplateHttps = new RestTemplate();
            ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
            JSONObject body = responseBody.getBody();
            log.info("++++++++++++++++++结果：" + body);
            if (Objects.nonNull(body) && body.containsKey("code") && body.getJSONObject("code").getInteger("errcode").equals(0)) {
                JSONObject data = body.getJSONObject("data");
                JSONArray pageList = data.getJSONArray("pageList");
                totalCount = data.getInteger("totalCount");
                if (pageList != null && !pageList.isEmpty()) {
                    for (int i = 0; i < pageList.size(); i++) {
                        total.add(pageList.getJSONObject(i));
                    }
                    pageNum++;
                } else {
                    break;
                }
            } else {
                // 错误处理
                log.error("请求出错: " + body);
//                break; // 请求失败，退出循环
            }
        }
        return total;
    }

    /**
     * 查询商品详情
     *
     * @param goodsId
     * @param accessToken
     * @return
     */
    private static JSONObject queryGoodsDetail(Long goodsId, String accessToken) {
        String url = "https://dopen.weimob.com/api/1_0/ec/goods/queryGoodsDetail?accesstoken=" + accessToken;
        // 请求头
        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> params = new HashMap<>();
        params.put("goodsId", goodsId);

        // 请求
        HttpEntity<String> request = new HttpEntity<>(JSONObject.toJSONString(params), headers);

        // 使用RestTemplate请求
        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject body = responseBody.getBody();
        log.info("++++++++++++++++++结果：" + body);
        if (Objects.nonNull(body) && body.containsKey("code") && body.getJSONObject("code").getInteger("errcode").equals(0)) {
            return body.getJSONObject("data").getJSONObject("goods");
        }
        throw new CrmebException("未查询到商品");
    }

    /**
     * 查询用户
     *
     * @param accessToken
     * @return
     */
    private static List<JSONObject> searchCustomer(String accessToken) {
        String url = "https://dopen.weimob.com/api/1_0/ec/membership/searchCustomer?accesstoken=" + accessToken;
        // 请求头
        HttpHeaders headers = new HttpHeaders();

        // 请求
        List<JSONObject> total = Lists.newArrayList();
        int totalCount = 0;
        int pageNum = 1;
        while (totalCount == 0 || total.size() < totalCount) {
            Map<String, Object> params = new HashMap<>();
            params.put("pageNum", pageNum);
            params.put("pageSize", 100);

            // 请求
            HttpEntity<String> request = new HttpEntity<>(JSONObject.toJSONString(params), headers);


            // 使用RestTemplate请求
            RestTemplate restTemplateHttps = new RestTemplate();
            ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
            JSONObject body = responseBody.getBody();
            log.info("++++++++++++++++++结果：" + body);
            if (Objects.nonNull(body) && body.containsKey("code") && body.getJSONObject("code").getInteger("errcode").equals(0)) {
                JSONObject data = body.getJSONObject("data");
                JSONArray items = data.getJSONArray("items");
                totalCount = data.getInteger("totalCount");
                if (items != null && !items.isEmpty()) {
                    for (int i = 0; i < items.size(); i++) {
                        total.add(items.getJSONObject(i));
                    }
                    pageNum++;
                } else {
                    break;
                }
            } else {
                throw new CrmebException("请求出错");
            }
        }
        return total;
    }

    /**
     * 获取运费列表
     *
     * @param goodsId
     * @param token
     * @return
     */
    private int findFreightTemplateList(Long goodsId, String token) {
        String url = "https://dopen.weimob.com/api/1_0/ec/goods/findFreightTemplateList?accesstoken=" + token;

        Map<String, Object> params = new HashMap<>();
        params.put("goodsId", goodsId);

        // 请求头
        HttpHeaders headers = new HttpHeaders();

        // 请求
        HttpEntity<String> request = new HttpEntity<>(JSONObject.toJSONString(params), headers);

        // 使用RestTemplate请求
        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject httpBody = responseBody.getBody();
        log.info("++++++++++++++++++结果：" + httpBody);
        if (Objects.nonNull(httpBody) && httpBody.containsKey("code") && httpBody.getJSONObject("code").getString("errcode").equals("0")) {
            JSONObject selectedFreightTemplate = httpBody.getJSONObject("data").getJSONObject("selectedFreightTemplate");
            if (Objects.nonNull(selectedFreightTemplate)) {
                Long templateId = selectedFreightTemplate.getLong("templateId");
                if (templateId.equals(253823778L)) {
                    return 3;
                } else if (templateId.equals(254380978L)) {
                    return 4;
                } else if (templateId.equals(254379778L)) {
                    return 5;
                } else {
                    return 0;
                }
            }
            return 0;
        }
        throw new CrmebException("查询运费模板失败");
    }

    /**
     * 获取配送列表
     *
     * @param goodsId
     * @param token
     * @return
     */
    private Boolean findDeliveryTypeList(Long goodsId, String token) {
        String url = "https://dopen.weimob.com/api/1_0/ec/goods/findDeliveryTypeList?accesstoken=" + token;

        Map<String, Object> params = new HashMap<>();
        params.put("goodsId", goodsId);

        // 请求头
        HttpHeaders headers = new HttpHeaders();

        // 请求
        HttpEntity<String> request = new HttpEntity<>(JSONObject.toJSONString(params), headers);

        // 使用RestTemplate请求
        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(url, request, JSONObject.class);
        JSONObject httpBody = responseBody.getBody();
        log.info("++++++++++++++++++结果：" + httpBody);
        if (Objects.nonNull(httpBody) && httpBody.containsKey("code") && httpBody.getJSONObject("code").getString("errcode").equals("0")) {
            JSONArray jsonArray = httpBody.getJSONObject("data").getJSONArray("deliveryTypeList");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.getString("deliveryTypeName").equals("到店自提") && jsonObject.getBooleanValue("selected")){
                    return true;
                }
            }
            return false;
        }
        throw new CrmebException("获取配送列表失败");
    }

    /**
     * 获取token
     *
     * @return
     */
    private static String getToken() {
        String tokenUrl = "https://dopen.weimob.com/fuwu/b/oauth2/token?grant_type=client_credentials&client_id=137DA97FDBB06BC08A797391F2862288&client_secret=15E4A4C7BC2045590B008D64878D8F5A&shop_id=4021792237678&shop_type=public_account_id";
        // 请求头
        HttpHeaders headers = new HttpHeaders();

        // 请求
        HttpEntity<String> request = new HttpEntity<>("", headers);

        // 使用RestTemplate请求
        RestTemplate restTemplateHttps = new RestTemplate();
        ResponseEntity<JSONObject> responseBody = restTemplateHttps.postForEntity(tokenUrl, request, JSONObject.class);
        JSONObject firstHttpBody = responseBody.getBody();
        log.info("++++++++++++++++++结果：" + firstHttpBody);
        if (firstHttpBody.containsKey("access_token")) {
            String accessToken = firstHttpBody.get("access_token").toString();
            return accessToken;
        }
        throw new CrmebException("获取token失败");
    }

    /**
     * 图片上传
     *
     * @param imageUrl
     * @param id
     * @param type     0 为商品 1为用户
     * @return
     * @throws IOException
     */
    private String uploadImg(String imageUrl, Integer id, Integer type) {
        try {
            // 获取文件名
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

            // 打开连接
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            // 获取输入流
            InputStream inputStream = connection.getInputStream();

            // 将输入流转换为字节数组
            byte[] bytes = IOUtils.toByteArray(inputStream);

            // 关闭输入流
            inputStream.close();

            // 获取内容类型
            String contentType = connection.getContentType();

            // 创建 MultipartFile
            MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, contentType, bytes);
            String model;
            if (type == 0)
                model = "content";
            else
                model = "maintain";
            if (StringUtils.isNotBlank(multipartFile.getContentType())){
                FileResultVo fileResultVo = uploadService.imageUpload(multipartFile, model, id);
                log.info("==========================fileurl:" +fileResultVo.getUrl());
                return systemAttachmentService.clearPrefix(fileResultVo.getUrl());
            }
        } catch (Exception e) {
            throw new CrmebException(e.getMessage());
        }
       return null;
    }

}
