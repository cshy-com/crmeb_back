package com.cshy.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cshy.common.constants.ProductType;
import com.cshy.common.model.dto.user.UserVisitHistoryDto;
import com.cshy.common.model.entity.category.Category;
import com.cshy.common.model.entity.express.ShippingTemplates;
import com.cshy.common.model.entity.product.StoreProductRelation;
import com.cshy.common.model.entity.user.UserVisitHistory;
import com.cshy.common.model.response.*;
import com.cshy.common.utils.StringUtils;
import com.cshy.service.service.category.CategoryService;
import com.cshy.service.service.shipping.ShippingTemplatesService;
import com.cshy.service.service.store.*;
import com.cshy.service.service.system.SystemConfigService;
import com.cshy.service.service.system.SystemUserLevelService;
import com.cshy.service.service.user.UserService;
import com.cshy.service.service.user.UserVisitHistoryService;
import com.cshy.service.service.user.UserVisitRecordService;
import com.github.pagehelper.PageInfo;
import com.cshy.common.constants.CategoryConstants;
import com.cshy.common.constants.Constants;
import com.cshy.common.constants.SysConfigConstants;
import com.cshy.common.model.entity.product.StoreProduct;
import com.cshy.common.model.entity.product.StoreProductAttr;
import com.cshy.common.model.entity.product.StoreProductAttrValue;
import com.cshy.common.model.entity.user.UserVisitRecord;
import com.cshy.common.model.entity.system.SystemUserLevel;
import com.cshy.common.model.entity.user.User;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.request.product.ProductListRequest;
import com.cshy.common.model.request.product.ProductRequest;
import com.cshy.common.utils.CrmebUtil;
import com.cshy.common.utils.RedisUtil;
import com.cshy.common.model.vo.category.CategoryTreeVo;
import com.cshy.common.model.vo.MyRecord;
import com.cshy.front.service.ProductService;
import com.cshy.service.delete.ProductUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * IndexServiceImpl 接口实现
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StoreProductReplyService storeProductReplyService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductRelationService storeProductRelationService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private ProductUtils productUtils;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StoreProductAttrService attrService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private SystemUserLevelService systemUserLevelService;

    @Autowired
    private StoreCartService cartService;

    @Autowired
    private UserVisitRecordService userVisitRecordService;

    @Autowired
    private UserVisitHistoryService userVisitHistoryService;

    @Autowired
    private ShippingTemplatesService shippingTemplatesService;

    /**
     * 获取分类
     *
     * @return List<CategoryTreeVo>
     */
    @Override
    public List<CategoryTreeVo> getCategory() {
        return categoryService.getListTree(CategoryConstants.CATEGORY_TYPE_PRODUCT, 1, "");
    }

    /**
     * 商品列表
     *
     * @return CommonPage<IndexProductResponse>
     */
    @Override
    public CommonPage<IndexProductResponse> getList(ProductRequest request, PageParamRequest pageRequest) {
        List<StoreProduct> storeProductList = storeProductService.findH5List(request, pageRequest);
        if (CollUtil.isEmpty(storeProductList)) {
            return CommonPage.restPage(new ArrayList<>());
        }
        CommonPage<StoreProduct> storeProductCommonPage = CommonPage.restPage(storeProductList);

        List<IndexProductResponse> productResponseArrayList = new ArrayList<>();
        for (StoreProduct storeProduct : storeProductList) {
            IndexProductResponse productResponse = new IndexProductResponse();
            List<Integer> activityList = CrmebUtil.stringToArrayInt(storeProduct.getActivity());
            // 活动类型默认：直接跳过
            if (CollUtil.isNotEmpty(activityList) && activityList.get(0).equals(ProductType.PRODUCT_TYPE_NORMAL)) {
                BeanUtils.copyProperties(storeProduct, productResponse);
                productResponseArrayList.add(productResponse);
                continue;
            }
            // 根据参与活动添加对应商品活动标示
            HashMap<Integer, ProductActivityItemResponse> activityByProduct =
                    productUtils.getActivityByProduct(storeProduct.getId(), storeProduct.getActivity());
            if (CollUtil.isNotEmpty(activityByProduct)) {
                for (Integer activity : activityList) {
                    if (activity.equals(ProductType.PRODUCT_TYPE_NORMAL)) {
                        break;
                    }
                    if (activity.equals(ProductType.PRODUCT_TYPE_SECKILL)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(ProductType.PRODUCT_TYPE_SECKILL);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                    if (activity.equals(ProductType.PRODUCT_TYPE_BARGAIN)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(ProductType.PRODUCT_TYPE_BARGAIN);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                    if (activity.equals(ProductType.PRODUCT_TYPE_PINGTUAN)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(ProductType.PRODUCT_TYPE_PINGTUAN);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                }
            }
            BeanUtils.copyProperties(storeProduct, productResponse);
            productResponseArrayList.add(productResponse);
        }
        CommonPage<IndexProductResponse> productResponseCommonPage = CommonPage.restPage(productResponseArrayList);
        BeanUtils.copyProperties(storeProductCommonPage, productResponseCommonPage, "list");
        return productResponseCommonPage;
    }

    /**
     * 获取商品详情
     *
     * @param id         商品编号
     * @param type       normal-正常，video-视频
     * @param isGiftCard
     * @return 商品详情信息
     */
    @Override
    public ProductDetailResponse getDetail(Integer id, String type, Integer isGiftCard) {
        // 获取用户
        User user = userService.getInfo();
        SystemUserLevel userLevel = null;
        if (ObjectUtil.isNotNull(user) && user.getLevel() > 0) {
            userLevel = systemUserLevelService.getByLevelId(user.getLevel());
        }

        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        // 查询商品
        StoreProduct storeProduct = storeProductService.getH5Detail(id);
        if (ObjectUtil.isNotNull(userLevel)) {
            storeProduct.setVipPrice(storeProduct.getPrice());
        }
        //查询运费模板
        ShippingTemplates shippingTemplates = shippingTemplatesService.getById(storeProduct.getTempId());
        productDetailResponse.setShippingTemplates(shippingTemplates);

        //查询收藏量
        List<StoreProductRelation> productRelationList = storeProductRelationService.getList(id, "collect");
        storeProduct.setCollectNum(productRelationList.size());

        productDetailResponse.setProductInfo(storeProduct);


        // 获取商品规格
        List<StoreProductAttr> attrList = attrService.getListByProductIdAndTypeNotDel(storeProduct.getId(), ProductType.PRODUCT_TYPE_NORMAL);

        // 根据制式设置attr属性
        productDetailResponse.setProductAttr(attrList);

        // 根据制式设置sku属性
        HashMap<String, Object> skuMap = new HashMap<>();
        List<StoreProductAttrValue> storeProductAttrValues = storeProductAttrValueService.getListByProductIdAndType(storeProduct.getId(), ProductType.PRODUCT_TYPE_NORMAL);
        for (StoreProductAttrValue storeProductAttrValue : storeProductAttrValues) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(storeProductAttrValue, atr);
            // 设置会员价
            if (ObjectUtil.isNotNull(userLevel)) {
                atr.setVipPrice(atr.getPrice());
            }
            skuMap.put(atr.getSuk(), atr);
        }
        productDetailResponse.setProductValue(skuMap);

        // 用户收藏、分销返佣
        if (ObjectUtil.isNotNull(user)) {
            // 查询用户是否收藏收藏
            user = userService.getInfo();
            productDetailResponse.setUserCollect(storeProductRelationService.getLikeOrCollectByUser(user.getUid(), id, false).size() > 0);
            // 判断是否开启分销
            String brokerageFuncStatus = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_BROKERAGE_FUNC_STATUS);
            if (brokerageFuncStatus.equals(Constants.COMMON_SWITCH_OPEN)) {// 分销开启
                // 判断是否开启气泡
                String isBubble = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_IS_BUBBLE);
                if (isBubble.equals(Constants.COMMON_SWITCH_OPEN)) {
                    productDetailResponse.setPriceName(getPacketPriceRange(storeProduct.getIsSub(), storeProductAttrValues, user.getIsPromoter()));
                }
            }
        } else {
            productDetailResponse.setUserCollect(false);
        }
        // 商品活动
        List<ProductActivityItemResponse> activityAllH5 = productUtils.getProductAllActivity(storeProduct);
        productDetailResponse.setActivityAllH5(activityAllH5);

        // 商品浏览量+1
        StoreProduct updateProduct = new StoreProduct();
        updateProduct.setId(id);
        updateProduct.setBrowse(storeProduct.getBrowse() + 1);
        updateProduct.setMerId(0);
        storeProductService.updateById(updateProduct);

        // 保存用户访问记录
        if (userService.getUserId() > 0) {
            UserVisitRecord visitRecord = new UserVisitRecord();
            visitRecord.setDate(DateUtil.date().toString("yyyy-MM-dd"));
            visitRecord.setUid(userService.getUserId());
            visitRecord.setVisitType(2);
            userVisitRecordService.save(visitRecord);
        }

        // 创建用户浏览记录
        if (userService.getUserId() > 0 && isGiftCard == 0) {
            UserVisitHistoryDto userVisitHistoryDto = new UserVisitHistoryDto();
            //如果存在记录 更新
            UserVisitHistory userVisitHistory = userVisitHistoryService.getOne(new LambdaQueryWrapper<UserVisitHistory>().eq(UserVisitHistory::getProductId, id).eq(UserVisitHistory::getUserId, userService.getUserId()));
            if (Objects.nonNull(userVisitHistory))
                BeanUtils.copyProperties(userVisitHistory, userVisitHistoryDto);
            userVisitHistoryDto.setUserId(userService.getUserId());
            userVisitHistoryDto.setProductId(id);
            userVisitHistoryDto.setCreateTime(DateUtil.now());
            if (StringUtils.isNotBlank(userVisitHistoryDto.getId()))
                userVisitHistoryService.update(userVisitHistoryDto);
            else
                userVisitHistoryService.add(userVisitHistoryDto);
        }

        return productDetailResponse;
    }

    /**
     * 获取商品SKU详情
     *
     * @param id 商品编号
     * @return 商品详情信息
     */
    @Override
    public ProductDetailResponse getSkuDetail(Integer id) {
        // 获取用户
        User user = userService.getInfo();
        SystemUserLevel userLevel = null;
        if (ObjectUtil.isNotNull(user) && user.getLevel() > 0) {
            userLevel = systemUserLevelService.getByLevelId(user.getLevel());
        }

        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        // 查询商品
        StoreProduct storeProduct = storeProductService.getH5Detail(id);

        // 获取商品规格
        List<StoreProductAttr> attrList = attrService.getListByProductIdAndTypeNotDel(storeProduct.getId(), ProductType.PRODUCT_TYPE_NORMAL);
        // 根据制式设置attr属性
        productDetailResponse.setProductAttr(attrList);

        // 根据制式设置sku属性
        HashMap<String, Object> skuMap = new HashMap<>();
        List<StoreProductAttrValue> storeProductAttrValues = storeProductAttrValueService.getListByProductIdAndType(storeProduct.getId(), ProductType.PRODUCT_TYPE_NORMAL);
        for (StoreProductAttrValue storeProductAttrValue : storeProductAttrValues) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(storeProductAttrValue, atr);
            // 设置会员价
            if (ObjectUtil.isNotNull(userLevel)) {
                BigDecimal vipPrice = atr.getPrice().multiply(new BigDecimal(userLevel.getDiscount())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                atr.setVipPrice(vipPrice);
            }
            skuMap.put(atr.getSuk(), atr);
        }
        productDetailResponse.setProductValue(skuMap);

        return productDetailResponse;
    }

    /**
     * 商品评论列表
     *
     * @param proId            商品编号
     * @param type             评价等级|0=全部,1=好评,2=中评,3=差评
     * @param pageParamRequest 分页参数
     * @return PageInfo<ProductReplyResponse>
     */
    @Override
    public PageInfo<ProductReplyResponse> getReplyList(Integer proId, Integer type, PageParamRequest pageParamRequest) {
        return storeProductReplyService.getH5List(proId, type, pageParamRequest);
    }

    /**
     * 产品评价数量和好评度
     *
     * @return StoreProductReplayCountResponse
     */
    @Override
    public StoreProductReplayCountResponse getReplyCount(Integer id) {
        MyRecord myRecord = storeProductReplyService.getH5Count(id);
        Long sumCount = myRecord.getLong("sumCount");
        Long goodCount = myRecord.getLong("goodCount");
        Long inCount = myRecord.getLong("mediumCount");
        Long poorCount = myRecord.getLong("poorCount");
        String replyChance = myRecord.getStr("replyChance");
        Integer replyStar = myRecord.getInt("replyStar");
        return new StoreProductReplayCountResponse(sumCount, goodCount, inCount, poorCount, replyChance, replyStar);
    }

    /**
     * 获取商品佣金区间
     *
     * @param isSub         是否单独计算分佣
     * @param attrValueList 商品属性列表
     * @param isPromoter    是否推荐人
     * @return String 金额区间
     */
    private String getPacketPriceRange(Boolean isSub, List<StoreProductAttrValue> attrValueList, Boolean isPromoter) {
        String priceName = "0";
        if (!isPromoter) return priceName;
        // 获取一级返佣比例
        String brokerageRatioString = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_RATIO);
        BigDecimal BrokerRatio = new BigDecimal(brokerageRatioString).divide(new BigDecimal("100"), 2, RoundingMode.DOWN);
        BigDecimal maxPrice;
        BigDecimal minPrice;
        // 获取佣金比例区间
        if (isSub) { // 是否单独分拥
            maxPrice = attrValueList.stream().map(StoreProductAttrValue::getBrokerage).reduce(BigDecimal.ZERO, BigDecimal::max);
            minPrice = attrValueList.stream().map(StoreProductAttrValue::getBrokerage).reduce(BigDecimal.ZERO, BigDecimal::min);
        } else {
            BigDecimal _maxPrice = attrValueList.stream().map(StoreProductAttrValue::getPrice).reduce(BigDecimal.ZERO, BigDecimal::max);
            BigDecimal _minPrice = attrValueList.stream().map(StoreProductAttrValue::getPrice).reduce(BigDecimal.ZERO, BigDecimal::min);
            maxPrice = BrokerRatio.multiply(_maxPrice).setScale(2, RoundingMode.HALF_UP);
            minPrice = BrokerRatio.multiply(_minPrice).setScale(2, RoundingMode.HALF_UP);
        }
        if (minPrice.compareTo(BigDecimal.ZERO) == 0 && maxPrice.compareTo(BigDecimal.ZERO) == 0) {
            priceName = "0";
        } else if (minPrice.compareTo(BigDecimal.ZERO) == 0 && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            priceName = maxPrice.toString();
        } else if (minPrice.compareTo(BigDecimal.ZERO) > 0 && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            priceName = minPrice.toString();
        } else if (minPrice.compareTo(maxPrice) == 0) {
            priceName = minPrice.toString();
        } else {
            priceName = minPrice.toString() + "~" + maxPrice.toString();
        }
        return priceName;
    }

    /**
     * 获取热门推荐商品列表
     *
     * @param pageRequest 分页参数
     * @return CommonPage<IndexProductResponse>
     */
    @Override
    public CommonPage<IndexProductResponse> getHotProductList(PageParamRequest pageRequest) {
        List<StoreProduct> storeProductList = storeProductService.getIndexProduct(Constants.INDEX_HOT_BANNER, pageRequest);
        if (CollUtil.isEmpty(storeProductList)) {
            return CommonPage.restPage(new ArrayList<>());
        }
        CommonPage<StoreProduct> storeProductCommonPage = CommonPage.restPage(storeProductList);

        List<IndexProductResponse> productResponseArrayList = new ArrayList<>();
        for (StoreProduct storeProduct : storeProductList) {
            IndexProductResponse productResponse = new IndexProductResponse();
            List<Integer> activityList = CrmebUtil.stringToArrayInt(storeProduct.getActivity());
            // 活动类型默认：直接跳过
            if (CollUtil.isNotEmpty(activityList) && activityList.get(0).equals(ProductType.PRODUCT_TYPE_NORMAL)) {
                BeanUtils.copyProperties(storeProduct, productResponse);
                productResponseArrayList.add(productResponse);
                continue;
            }
            // 根据参与活动添加对应商品活动标示
            HashMap<Integer, ProductActivityItemResponse> activityByProduct =
                    productUtils.getActivityByProduct(storeProduct.getId(), storeProduct.getActivity());
            if (CollUtil.isNotEmpty(activityByProduct)) {
                for (Integer activity : activityList) {
                    if (activity.equals(ProductType.PRODUCT_TYPE_NORMAL)) {
                        break;
                    }
                    if (activity.equals(ProductType.PRODUCT_TYPE_SECKILL)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(ProductType.PRODUCT_TYPE_SECKILL);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                    if (activity.equals(ProductType.PRODUCT_TYPE_BARGAIN)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(ProductType.PRODUCT_TYPE_BARGAIN);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                    if (activity.equals(ProductType.PRODUCT_TYPE_PINGTUAN)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(ProductType.PRODUCT_TYPE_PINGTUAN);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                }
            }
            BeanUtils.copyProperties(storeProduct, productResponse);
            productResponseArrayList.add(productResponse);
        }
        CommonPage<IndexProductResponse> productResponseCommonPage = CommonPage.restPage(productResponseArrayList);
        BeanUtils.copyProperties(storeProductCommonPage, productResponseCommonPage, "list");
        return productResponseCommonPage;
    }

    /**
     * 商品详情评论
     *
     * @param id 商品id
     * @return ProductDetailReplyResponse
     * 评论只有一条，图文
     * 评价总数
     * 好评率
     */
    @Override
    public ProductDetailReplyResponse getProductReply(Integer id) {
        return storeProductReplyService.getH5ProductReply(id);
    }

    /**
     * 优选商品推荐
     *
     * @return CommonPage<IndexProductResponse>
     */
    @Override
    public CommonPage<IndexProductResponse> getGoodProductList() {
        PageParamRequest pageRequest = new PageParamRequest();
        pageRequest.setLimit(9);
        List<StoreProduct> storeProductList = storeProductService.getIndexProduct(Constants.INDEX_RECOMMEND_BANNER, pageRequest);
        if (CollUtil.isEmpty(storeProductList)) {
            return CommonPage.restPage(new ArrayList<>());
        }
        CommonPage<StoreProduct> storeProductCommonPage = CommonPage.restPage(storeProductList);

        List<IndexProductResponse> productResponseArrayList = new ArrayList<>();
        for (StoreProduct storeProduct : storeProductList) {
            IndexProductResponse productResponse = new IndexProductResponse();
            List<Integer> activityList = CrmebUtil.stringToArrayInt(storeProduct.getActivity());
            // 活动类型默认：直接跳过
            if (CollUtil.isNotEmpty(activityList) && activityList.get(0).equals(ProductType.PRODUCT_TYPE_NORMAL)) {
                BeanUtils.copyProperties(storeProduct, productResponse);
                productResponseArrayList.add(productResponse);
                continue;
            }
            // 根据参与活动添加对应商品活动标示
            HashMap<Integer, ProductActivityItemResponse> activityByProduct =
                    productUtils.getActivityByProduct(storeProduct.getId(), storeProduct.getActivity());
            if (CollUtil.isNotEmpty(activityByProduct)) {
                for (Integer activity : activityList) {
                    if (activity.equals(ProductType.PRODUCT_TYPE_NORMAL)) {
                        break;
                    }
                    if (activity.equals(ProductType.PRODUCT_TYPE_SECKILL)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(ProductType.PRODUCT_TYPE_SECKILL);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                    if (activity.equals(ProductType.PRODUCT_TYPE_BARGAIN)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(ProductType.PRODUCT_TYPE_BARGAIN);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                    if (activity.equals(ProductType.PRODUCT_TYPE_PINGTUAN)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(ProductType.PRODUCT_TYPE_PINGTUAN);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                }
            }
            BeanUtils.copyProperties(storeProduct, productResponse);
            productResponseArrayList.add(productResponse);
        }
        CommonPage<IndexProductResponse> productResponseCommonPage = CommonPage.restPage(productResponseArrayList);
        BeanUtils.copyProperties(storeProductCommonPage, productResponseCommonPage, "list");
        return productResponseCommonPage;
    }

    /**
     * 商品列表(个别分类模型使用)
     *
     * @param request          列表请求参数
     * @param pageParamRequest 分页参数
     * @return CommonPage
     */
    @Override
    public CommonPage<IndexProductResponse> getCategoryProductList(ProductListRequest request, PageParamRequest pageParamRequest) {
        ProductRequest searchRequest = new ProductRequest();
        BeanUtils.copyProperties(searchRequest, request);
        List<StoreProduct> storeProductList = storeProductService.findH5List(searchRequest, pageParamRequest);
        if (CollUtil.isEmpty(storeProductList)) {
            return CommonPage.restPage(new ArrayList<>());
        }
        CommonPage<StoreProduct> storeProductCommonPage = CommonPage.restPage(storeProductList);

        User user = userService.getInfo();
        List<IndexProductResponse> productResponseArrayList = new ArrayList<>();
        for (StoreProduct storeProduct : storeProductList) {
            IndexProductResponse productResponse = new IndexProductResponse();
            // 获取商品购物车数量
            if (ObjectUtil.isNotNull(user)) {
                productResponse.setCartNum(cartService.getProductNumByUidAndProductId(user.getUid(), storeProduct.getId()));
            }
            BeanUtils.copyProperties(storeProduct, productResponse);
            productResponseArrayList.add(productResponse);
        }
        CommonPage<IndexProductResponse> productResponseCommonPage = CommonPage.restPage(productResponseArrayList);
        BeanUtils.copyProperties(storeProductCommonPage, productResponseCommonPage, "list");
        return productResponseCommonPage;
    }

    /**
     * 获取商品排行榜
     *
     * @return List
     */
    @Override
    public List<StoreProduct> getLeaderboard() {
        return storeProductService.getLeaderboard();
    }

    @Override
    public List<Map<String, Object>> queryByCategoryIdAndFeature(Integer categoryId, Integer feature) {
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreProduct::getIsShow, true);
        lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
        lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
        //判断categoryId是二级分类id还是1级分类id categoryId为空则查询所有分类
        if (null != categoryId) {
            Category category = categoryService.getById(categoryId);
            List<Integer> cateIdList = Lists.newArrayList();
            if (category.getPid().equals(0)) {
                //查询二级分类
                List<Category> childVoListByPid = categoryService.getChildVoListByPid(categoryId);
                cateIdList = childVoListByPid.stream().map(Category::getId).collect(Collectors.toList());
            } else {
                cateIdList.add(categoryId);
            }
            List<Integer> finalCateIdList = cateIdList;
            lambdaQueryWrapper.and(l -> {
                List<String> sqlList = Lists.newArrayList();
                finalCateIdList.forEach(id -> sqlList.add(" FIND_IN_SET('" + id + "', cate_id) > 0 "));
                String join = String.join(" OR ", sqlList);
                l.apply(join);
            });
        }

        switch (feature) {
            case 0:
                lambdaQueryWrapper.eq(StoreProduct::getIsHot, true);
                break;
            case 1:
                lambdaQueryWrapper.eq(StoreProduct::getIsBenefit, true);
                break;
            case 2:
                lambdaQueryWrapper.eq(StoreProduct::getIsBest, true);
                break;
            case 3:
                lambdaQueryWrapper.eq(StoreProduct::getIsNew, true);
                break;
            case 4:
                lambdaQueryWrapper.eq(StoreProduct::getIsSafe, true);
                break;
            case 5:
                lambdaQueryWrapper.eq(StoreProduct::getIsEnjoy, true);
                break;
            case 6:
                lambdaQueryWrapper.eq(StoreProduct::getIsGood, true);
                break;
        }

        List<StoreProduct> storeProductList = storeProductService.list(lambdaQueryWrapper);
        List<Map<String, Object>> resList = storeProductList.stream().map(storeProduct -> {
            Map<String, Object> map = new HashMap<>();
            map.put("otPrice", storeProduct.getOtPrice());
            map.put("price", storeProduct.getPrice());
            map.put("ficti", storeProduct.getFicti());
            map.put("image", storeProduct.getImage());
            map.put("stock", storeProduct.getStock());
            map.put("unitName", storeProduct.getUnitName());
            map.put("storeName", storeProduct.getStoreName());
            map.put("sales", storeProduct.getSales());
            map.put("id", storeProduct.getId());
            return map;
        }).collect(Collectors.toList());
        return resList;
    }

    @Override
    public List<CategoryTreeVo> getFirstCategory() {
        List<Category> list = categoryService.list(new LambdaQueryWrapper<Category>().eq(Category::getPid, 0).eq(Category::getType, 1).eq(Category::getStatus, true));
        return list.parallelStream().filter(category -> {
            //查询三级分类中是否有商品
            List<Category> categories = categoryService.list(new LambdaQueryWrapper<Category>().select(Category::getId).like(Category::getPath, category.getId()).like(Category::getPath, "/%/%/").eq(Category::getStatus, true));
            List<Integer> categoryIdList = categories.stream().map(Category::getId).collect(Collectors.toList());
            if (CollUtil.isEmpty(categoryIdList))
                return false;
            int count = storeProductService.count(new LambdaQueryWrapper<StoreProduct>().in(StoreProduct::getCateId, categoryIdList).eq(StoreProduct::getIsShow, true));
            return count > 0;
        }).map(category -> {
            CategoryTreeVo categoryTreeVo = new CategoryTreeVo();
            categoryTreeVo.setId(category.getId());
            categoryTreeVo.setPid(category.getPid());
            categoryTreeVo.setPath(category.getPath());
            categoryTreeVo.setName(category.getName());
            categoryTreeVo.setType(category.getType());
            categoryTreeVo.setUrl(category.getUrl());
            categoryTreeVo.setExtra(category.getExtra());
            categoryTreeVo.setStatus(category.getStatus());
            categoryTreeVo.setSort(category.getSort());
            return categoryTreeVo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CategoryTreeVo> getChildCategory(Integer parentId) {
        List<CategoryTreeVo> voList = Lists.newArrayList();
        List<Category> list = categoryService.list(new LambdaQueryWrapper<Category>().like(Category::getPath, "%/" + parentId + "%").eq(Category::getStatus, true));
        list.parallelStream().forEach(category -> {
            if (category.getPid().equals(parentId)) {
                CategoryTreeVo categoryTreeVo = new CategoryTreeVo();
                categoryTreeVo.setId(category.getId());
                categoryTreeVo.setPid(category.getPid());
                categoryTreeVo.setPath(category.getPath());
                categoryTreeVo.setName(category.getName());
                categoryTreeVo.setType(category.getType());
                categoryTreeVo.setUrl(category.getUrl());
                categoryTreeVo.setExtra(category.getExtra());
                categoryTreeVo.setStatus(category.getStatus());
                categoryTreeVo.setSort(category.getSort());

                List<CategoryTreeVo> children = Lists.newArrayList();
                //子集
                list.parallelStream().filter(category1 -> {
                    return category1.getPath().matches("(?:[^/]*/){3}[^/]*") && category1.getPid().equals(category.getId());
                }).forEach(category1 -> {
                    //查询分类中是否有商品
                    int count = storeProductService.count(new LambdaQueryWrapper<StoreProduct>().eq(StoreProduct::getIsShow, true)
                            .and(wrapper -> wrapper.like(StoreProduct::getCateId, "," + category1.getId() + ",")
                                    .or()
                                    .likeRight(StoreProduct::getCateId, category1.getId() + ",")
                                    .or()
                                    .likeLeft(StoreProduct::getCateId, "," + category1.getId())
                                    .or()
                                    .eq(StoreProduct::getCateId, category1.getId()))
                    );
                    if (count > 1) {
                        CategoryTreeVo categoryTreeVo1 = new CategoryTreeVo();
                        categoryTreeVo1.setId(category1.getId());
                        categoryTreeVo1.setPid(category1.getPid());
                        categoryTreeVo1.setPath(category1.getPath());
                        categoryTreeVo1.setName(category1.getName());
                        categoryTreeVo1.setType(category1.getType());
                        categoryTreeVo1.setUrl(category1.getUrl());
                        categoryTreeVo1.setExtra(category1.getExtra());
                        categoryTreeVo1.setStatus(category1.getStatus());
                        categoryTreeVo1.setSort(category1.getSort());

                        children.add(categoryTreeVo1);
                    }
                });
                categoryTreeVo.setChild(children);
                voList.add(categoryTreeVo);
            }
        });
        //过滤没有三级分类的二级分类
        List<CategoryTreeVo> finalList = voList.parallelStream().filter(vo -> CollUtil.isNotEmpty(vo.getChild())).collect(Collectors.toList());
        return finalList;
    }

}

