package com.cshy.admin.controller.wm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cshy.common.constants.PayType;
import com.cshy.common.constants.ProductType;
import com.cshy.common.constants.StoreOrderStatusConstants;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.entity.category.Category;
import com.cshy.common.model.entity.order.StoreOrder;
import com.cshy.common.model.entity.order.StoreOrderInfo;
import com.cshy.common.model.entity.product.StoreProduct;
import com.cshy.common.model.entity.product.StoreProductAttr;
import com.cshy.common.model.entity.product.StoreProductAttrValue;
import com.cshy.common.model.entity.product.StoreProductDescription;
import com.cshy.common.model.entity.system.SystemStore;
import com.cshy.common.model.entity.user.User;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.FileResultVo;
import com.cshy.common.utils.CommonUtil;
import com.cshy.common.utils.DateUtil;
import com.cshy.common.utils.StringUtils;
import com.cshy.service.service.UploadService;
import com.cshy.service.service.category.CategoryService;
import com.cshy.service.service.store.*;
import com.cshy.service.service.system.SystemAttachmentService;
import com.cshy.service.service.system.SystemStoreService;
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


import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
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
    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private SystemStoreService systemStoreService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

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
//        List<JSONObject> goodsJsonList = queryGoodsList(accessToken);
////        //查询分类
//        List<Category> categoryList = categoryService.list(new LambdaQueryWrapper<Category>().eq(Category::getType, 1).like(Category::getPath, "/%/%/"));
////        //分别获取商品详情
//        goodsJsonList.forEach(goods -> {
//            Long goodsId = goods.getLong("goodsId");
//            JSONObject goodsDetails = queryGoodsDetail(goodsId, accessToken);
//            goodsDetails.put("sales", goods.getInteger("salesNum"));
//
//            try {
//                convertGoods(goodsDetails, accessToken, categoryList);
//                System.out.println(goodsDetails);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
        List<JSONObject> jsonObjectList = queryOrderList(accessToken);
        for (int i = 0; i < jsonObjectList.size(); i++) {
            convertOrder(jsonObjectList.get(i));
        }

        return CommonResult.success();
    }

    private void convertOrder(JSONObject orderJson) {
        JSONArray itemList = orderJson.getJSONArray("itemList");
        JSONObject deliveryDetail = orderJson.getJSONObject("deliveryDetail");
        JSONObject logisticsDeliveryDetail = deliveryDetail.getJSONObject("logisticsDeliveryDetail");
        JSONObject selfPickupDetail = deliveryDetail.getJSONObject("selfPickupDetail");
        JSONArray logisticsOrderList = null;
        if (Objects.nonNull(logisticsDeliveryDetail))
            logisticsOrderList = logisticsDeliveryDetail.getJSONArray("logisticsOrderList");

        StoreOrder storeOrder = new StoreOrder();
        storeOrder.setOrderId(orderJson.get("orderNo").toString());
        //查找用户
        User wid = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getTagId, orderJson.getLong("wid").toString()));
        if (Objects.nonNull(wid)){

            storeOrder.setUid(wid.getUid());
            if (StringUtils.isNotBlank(orderJson.getString("receiverName")))
                storeOrder.setRealName(orderJson.getString("receiverName"));
            else
                storeOrder.setRealName("");
            storeOrder.setUserMobile(orderJson.getString("receiverMobile"));
            storeOrder.setAddress(orderJson.getString("receiverAddress"));
            storeOrder.setFreightPrice(orderJson.getBigDecimal("deliveryAmount"));
            int totalNum = 0;
            for (int i = 0; i < itemList.size(); i++) {
                JSONObject jsonObject = itemList.getJSONObject(i);
                totalNum += jsonObject.getInteger("skuNum");
            }
            storeOrder.setTotalNum(totalNum);
            storeOrder.setTotalPrice(storeOrder.getFreightPrice().add(orderJson.getBigDecimal("goodsAmount")));
            storeOrder.setTotalPostage(orderJson.getBigDecimal("deliveryAmount"));

            storeOrder.setPayPrice(orderJson.getBigDecimal("paymentAmount"));
            storeOrder.setPayPostage(orderJson.getBigDecimal("deliveryPaymentAmount"));
            storeOrder.setDeductionPrice(storeOrder.getTotalPrice().subtract(storeOrder.getPayPrice()));
            storeOrder.setCouponId(0);
            storeOrder.setCouponPrice(BigDecimal.ZERO);
            storeOrder.setPaid(Boolean.TRUE);
            if (Objects.nonNull(orderJson.getLong("paymentTime")))
                storeOrder.setPayTime(new Date(orderJson.getLong("paymentTime")));
            storeOrder.setPayType(storeOrder.getPayPrice().compareTo(BigDecimal.ZERO) != 0 ? PayType.PAY_TYPE_WE_CHAT : PayType.PAY_TYPE_INTEGRAL);
            storeOrder.setCreateTime(new Date(orderJson.getLong("createTime")));
            storeOrder.setStatus(matchStatus(orderJson.getInteger("orderStatus")));

            storeOrder.setRefundStatus(0);
            storeOrder.setRefundPrice(BigDecimal.ZERO);

            //物流处理
            if (orderJson.getInteger("deliveryType") == 1) {
                //物流
                storeOrder.setDeliveryType(StoreOrderStatusConstants.ORDER_LOG_EXPRESS);
                storeOrder.setShippingType(1);
            } else if (orderJson.getInteger("deliveryType") == 5) {
                //无需物流 送货
                storeOrder.setDeliveryType(StoreOrderStatusConstants.ORDER_LOG_EXPRESS);
                storeOrder.setShippingType(1);
                storeOrder.setTrackingNo("zp666");
            } else if (orderJson.getInteger("deliveryType") == 3) {
                //自提
                storeOrder.setDeliveryType(StoreOrderStatusConstants.ORDER_LOG_PICKUP);
                storeOrder.setShippingType(2);
                if (Objects.nonNull(selfPickupDetail) && Objects.nonNull(selfPickupDetail.getJSONArray("logisticsOrderList"))) {

                    storeOrder.setVerifyCode(selfPickupDetail.getJSONArray("logisticsOrderList").getJSONObject(0).getString("deliveryNo"));
                    //查询自提门店
                    SystemStore selfPickupSiteAddress = systemStoreService.getOne(new LambdaQueryWrapper<SystemStore>().eq(SystemStore::getAddress, selfPickupDetail.getString("selfPickupSiteAddress")));
                    if (Objects.nonNull(selfPickupSiteAddress))
                        storeOrder.setStoreId(selfPickupSiteAddress.getId());
                }

            } else {
                storeOrder.setDeliveryType(StoreOrderStatusConstants.ORDER_LOG_PICKUP);
                storeOrder.setShippingType(1);
            }

            //物流单号处理
            StringBuilder trackingNo = new StringBuilder();
            if (Objects.nonNull(logisticsOrderList)) {
                for (int i = 0; i < logisticsOrderList.size(); i++) {
                    JSONObject logisticsOrderListJSONObject = logisticsOrderList.getJSONObject(i);
                    if (i != 0)
                        trackingNo.append(logisticsOrderListJSONObject.getString("deliveryNo"));
                    else
                        trackingNo.append(",").append(logisticsOrderListJSONObject.getString("deliveryNo"));
                }
                storeOrder.setTrackingNo(trackingNo.toString());
            }

            storeOrder.setGainIntegral(BigDecimal.ZERO);
            storeOrder.setUseIntegral(storeOrder.getTotalPrice().subtract(storeOrder.getPayPrice()));
            storeOrder.setMark(orderJson.getString("buyerRemark"));
            storeOrder.setIsDel(false);
            storeOrder.setMerId(0);
            storeOrder.setIsMerCheck(0);
            storeOrder.setCombinationId(0);
            storeOrder.setPinkId(0);
            storeOrder.setCost(BigDecimal.ZERO);
            storeOrder.setSeckillId(0);
            storeOrder.setBargainId(0);
            storeOrder.setBargainId(0);
            storeOrder.setClerkId(0);
            storeOrder.setIsRemind(false);
            storeOrder.setIsSystemDel(false);
            storeOrder.setUpdateTime(new Date(orderJson.getLong("updateTime")));
            storeOrder.setBargainUserId(0);
            storeOrder.setType(0);
            storeOrder.setProTotalPrice(orderJson.getBigDecimal("goodsAmount"));
            storeOrder.setBeforePayPrice(storeOrder.getTotalPrice());
            storeOrder.setIsAlterPrice(false);
            storeOrder.setOutTradeNo("");

            storeOrder.setPaymentChannel(storeOrder.getPayPrice().compareTo(BigDecimal.ZERO) > 0 ? 1 : 3);
            storeOrder.setAddressDetail("");

            //订单新增
            storeOrderService.save(storeOrder);

            //订单详情
            for (int i = 0; i < itemList.size(); i++) {
                JSONObject item = itemList.getJSONObject(i);
                StoreOrderInfo storeOrderInfo = new StoreOrderInfo();
                storeOrderInfo.setOrderId(storeOrder.getId());
                //查询商品
                StoreProduct storeProduct = storeProductService.getOne(new LambdaQueryWrapper<StoreProduct>().eq(StoreProduct::getStoreName, item.getString("goodsTitle")).eq(StoreProduct::getBarCode, ""));
                if (Objects.nonNull(storeProduct)) {

                    storeOrderInfo.setProductId(storeProduct.getId());
                    //查询规格
                    List<StoreProductAttrValue> listByProductIdAndType = storeProductAttrValueService.getListByProductIdAndType(storeProduct.getId(), 0);
                    StoreProductAttrValue storeProductAttrValue = listByProductIdAndType.get(0);
                    storeOrderInfo.setUnique(storeProductAttrValue.getId().toString());
                    storeOrderInfo.setOrderNo(storeOrder.getOrderId());
                    storeOrderInfo.setProductName(storeProduct.getStoreName());
                    storeOrderInfo.setAttrValueId(storeProductAttrValue.getId());
                    storeOrderInfo.setImage(storeProduct.getImage());
                    storeOrderInfo.setSku("默认");
                    storeOrderInfo.setPayNum(item.getInteger("skuNum"));
                    storeOrderInfo.setPrice(storeProduct.getPrice().multiply(new BigDecimal(storeOrderInfo.getPayNum())));
                    storeOrderInfo.setWeight(storeProductAttrValue.getWeight());
                    storeOrderInfo.setVolume(storeProductAttrValue.getVolume());
                    storeOrderInfo.setGiveIntegral(BigDecimal.ZERO);
                    storeOrderInfo.setIsReply(true);
                    storeOrderInfo.setIsSub(false);
                    storeOrderInfo.setVipPrice(storeProductAttrValue.getPrice());
                    storeOrderInfo.setProductType(storeProductAttrValue.getType());
                    storeOrderInfo.setAttrValueImage(storeProductAttrValue.getImage());
                    storeOrderInfo.setShipNum(item.getInteger("hadDeliveryItemNum"));
                    //构建info对象
                    Map<Object, Object> info = new HashMap<>();
                    info.put("productId", storeOrderInfo.getProductId());
                    info.put("attrValueId", storeOrderInfo.getAttrValueId());
                    info.put("isDeliver", storeProduct.getIsDeliver());
                    info.put("isPickup", storeProduct.getIsPickup());
                    info.put("weight", storeProductAttrValue.getWeight());
                    info.put("giveIntegral", 0);
                    info.put("isSub", false);
                    info.put("productName", storeProduct.getStoreName());
                    info.put("volume", storeProductAttrValue.getVolume());
                    info.put("mainImage", storeProductAttrValue.getImage());
                    info.put("payNum", storeOrderInfo.getPayNum());
                    info.put("price", storeOrderInfo.getPrice());
                    info.put("vipPrice", storeOrderInfo.getPrice());
                    info.put("attrValueImage", storeProductAttrValue.getImage());
                    info.put("tempId", storeProduct.getTempId());
                    info.put("sku", "默认");
                    info.put("productType", 0);
                    storeOrderInfo.setInfo(JSONObject.toJSONString(info));
                    storeOrderInfoService.save(storeOrderInfo);
                }
            }
        }
    }

    private int matchStatus(Integer status) {
        int s = 0;
        switch (status) {
            case 1:
                s = 0;
                break;
            case 2:
                s = 1;
                break;
            case 3:
            case 5:
                s = 3;
        }
        return s;
    }

    private static List<JSONObject> queryOrderList(String accessToken) {
        String url = "https://dopen.weimob.com/api/1_0/ec/order/queryOrderList?accesstoken=" + accessToken;
        // 请求头
        HttpHeaders headers = new HttpHeaders();

        List<JSONObject> total = Lists.newArrayList();
        int totalCount = 0;
        int pageNum = 1;
        while (totalCount == 0 || total.size() < totalCount) {
            Map<String, Object> params = new HashMap<>();
            params.put("pageNum", pageNum);
            params.put("pageSize", 100);
            Map<Object, Object> map = new HashMap<>();
            map.put("createStartTime", 1714528800000L);//2024-05-01 10:00:00
            map.put("createEndTime", 1717034400000L);//2024-05-30 10:00:00
            params.put("queryParameter", map);

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
                    user.setPwd(CommonUtil.createPwd(user.getPhone()));
                }
                if (Objects.nonNull(assets)) {
                    //余额（同步过来为积分）
                    user.setIntegral(assets.getBigDecimal("currentAmount").divide(new BigDecimal(100) ,2, BigDecimal.ROUND_UP));
                }
                if (memberCardInfos.size() != 0) {
                    //注册时间
                    user.setCreateTime(new Date(userJson.getJSONArray("memberCardInfos").getJSONObject(0).getLong("startDate")));
                }
                if (Objects.nonNull(userJson.getJSONObject("customerInfo"))){
                    Long aLong = userJson.getJSONObject("customerInfo").getLong("wid");
                    user.setTagId(aLong.toString());
                }
                userService.save(user);
                //头像处理
                try {
                    if (StringUtils.isNotBlank(customerInfo.getString("headUrl")))
                        user.setAvatar(uploadImg(customerInfo.getString("headUrl"), user.getUid(), 1));
                } catch (Exception e) {
                    user.setAvatar("crmebimage/public/maintain/def_category_logo.jpg");
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
            storeProduct.setSales(goodsDetail.getInteger("sales"));
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
            storeProduct.setPrice(sku.getBigDecimal("salePrice"));
            storeProduct.setCost(sku.getBigDecimal("costPrice"));
            storeProduct.setOtPrice(sku.getBigDecimal("salePrice"));
            storeProduct.setVipPrice(sku.getBigDecimal("salePrice"));

            log.info("保存数据");
            storeProductService.save(storeProduct);

            //处理图片
            log.info("处理图片");
            log.info("+++++++++++===========================" + goodsDetail.getJSONArray("goodsImageUrl").getString(0));
            String mainImgUrl = uploadImg(goodsDetail.getJSONArray("goodsImageUrl").getString(0), storeProduct.getId(), 0);

            //更新数据
            log.info("更新数据");
            StoreProduct newProduct = storeProductService.getById(storeProduct.getId());
            if (StringUtils.isNotBlank(mainImgUrl)) {
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
            storeProductAttrValue.setOtPrice(newProduct.getPrice());

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
                if (jsonObject.getString("deliveryTypeName").equals("到店自提") && jsonObject.getBooleanValue("selected")) {
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
            if (StringUtils.isNotBlank(multipartFile.getContentType())) {
                FileResultVo fileResultVo = uploadService.imageUpload(multipartFile, model, id);
                log.info("==========================fileurl:" + fileResultVo.getUrl());
                return systemAttachmentService.clearPrefix(fileResultVo.getUrl());
            }
        } catch (Exception e) {
            throw new CrmebException(e.getMessage());
        }
        return null;
    }

}
