package com.cshy.service.impl.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.constants.Constants;
import com.cshy.common.constants.ProductType;
import com.cshy.common.constants.RedisKey;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.Order;
import com.cshy.common.model.entity.category.Category;
import com.cshy.common.model.entity.coupon.StoreCoupon;
import com.cshy.common.model.entity.giftCard.GiftCardProduct;
import com.cshy.common.model.entity.product.*;
import com.cshy.common.model.request.*;
import com.cshy.common.model.request.product.ProductRequest;
import com.cshy.common.model.request.store.*;
import com.cshy.common.model.response.*;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.utils.CrmebUtil;
import com.cshy.common.utils.DateUtil;
import com.cshy.common.utils.RedisUtil;
import com.cshy.common.model.vo.MyRecord;
import com.cshy.service.service.*;
import com.cshy.service.service.category.CategoryService;
import com.cshy.service.service.giftCard.GiftCardProductService;
import com.cshy.service.service.store.*;
import com.cshy.service.service.system.SystemAttachmentService;
import com.cshy.service.service.system.SystemConfigService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.cshy.service.dao.store.StoreProductDao;
import com.cshy.service.delete.ProductUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class StoreProductServiceImpl extends ServiceImpl<StoreProductDao, StoreProduct>
        implements StoreProductService {

    @Resource
    private StoreProductDao dao;

    @Autowired
    private StoreProductAttrService attrService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private StoreProductDescriptionService storeProductDescriptionService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StoreProductRelationService storeProductRelationService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private StoreProductCouponService storeProductCouponService;

    @Autowired
    private StoreCouponService storeCouponService;

    @Autowired
    private ProductUtils productUtils;

    @Autowired
    private StoreBargainService storeBargainService;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private StoreSeckillService storeSeckillService;

    @Autowired
    private OnePassService onePassService;

    @Autowired
    private StoreCartService storeCartService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private GiftCardProductService giftCardProductService;

    private static final Logger logger = LoggerFactory.getLogger(StoreProductServiceImpl.class);

    /**
     * 获取产品列表Admin
     *
     * @param request          筛选参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<StoreProductResponse> getAdminList(StoreProductSearchRequest request, PageParamRequest pageParamRequest) {
        //带 StoreProduct 类的多条件查询
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //类型搜索
        switch (request.getType()) {
            case 1:
                //出售中（已上架）
                lambdaQueryWrapper.eq(StoreProduct::getIsShow, true);
                lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            case 2:
                //仓库中（未上架）
                lambdaQueryWrapper.eq(StoreProduct::getIsShow, false);
                lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            case 3:
                //已售罄
                lambdaQueryWrapper.le(StoreProduct::getStock, 0);
                lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            case 4:
                //警戒库存
                Integer stock = Integer.parseInt(systemConfigService.getValueByKey("store_stock"));
                lambdaQueryWrapper.le(StoreProduct::getStock, stock);
                lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            case 5:
                //回收站
                lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, true);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            default:
                break;
        }

        //关键字搜索
        if (StrUtil.isNotBlank(request.getKeywords())) {
            lambdaQueryWrapper.and(i -> i
                    .or().eq(StoreProduct::getId, request.getKeywords())
                    .or().like(StoreProduct::getStoreName, request.getKeywords())
                    .or().like(StoreProduct::getKeyword, request.getKeywords()));
        }
        lambdaQueryWrapper.apply(StringUtils.isNotBlank(request.getCateId()), "FIND_IN_SET ('" + request.getCateId() + "', cate_id)");
        if (StringUtils.isNotBlank(request.getPriceOrder())){
            if (request.getPriceOrder().equals(Order.DESC))
                lambdaQueryWrapper.orderByDesc(StoreProduct::getPrice);
            else
                lambdaQueryWrapper.orderByAsc(StoreProduct::getPrice);
        } else if (StringUtils.isNotBlank(request.getSalesOrder())) {
            if (request.getSalesOrder().equals(Order.DESC))
                lambdaQueryWrapper.orderByDesc(StoreProduct::getSales);
            else
                lambdaQueryWrapper.orderByAsc(StoreProduct::getSales);
        } else {
            lambdaQueryWrapper.orderByDesc(StoreProduct::getSort).orderByDesc(StoreProduct::getId);
        }

        Page<StoreProduct> storeProductPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<StoreProduct> storeProducts = dao.selectList(lambdaQueryWrapper);
        List<StoreProductResponse> storeProductResponses = new ArrayList<>();
        for (StoreProduct product : storeProducts) {
            StoreProductResponse storeProductResponse = new StoreProductResponse();
            BeanUtils.copyProperties(product, storeProductResponse);
            StoreProductAttr storeProductAttrPram = new StoreProductAttr();
            storeProductAttrPram.setProductId(product.getId()).setType(ProductType.PRODUCT_TYPE_NORMAL);
            List<StoreProductAttr> attrs = attrService.getByEntity(storeProductAttrPram);

            if (attrs.size() > 0) {
                storeProductResponse.setAttr(attrs);
            }
            List<StoreProductAttrValueResponse> storeProductAttrValueResponse = new ArrayList<>();

            StoreProductAttrValue storeProductAttrValuePram = new StoreProductAttrValue();
            storeProductAttrValuePram.setProductId(product.getId()).setType(ProductType.PRODUCT_TYPE_NORMAL);
            List<StoreProductAttrValue> storeProductAttrValues = storeProductAttrValueService.getByEntity(storeProductAttrValuePram);
            storeProductAttrValues.stream().map(e -> {
                StoreProductAttrValueResponse response = new StoreProductAttrValueResponse();
                BeanUtils.copyProperties(e, response);
                storeProductAttrValueResponse.add(response);
                return e;
            }).collect(Collectors.toList());
            storeProductResponse.setAttrValue(storeProductAttrValueResponse);
            // 处理富文本
            StoreProductDescription sd = storeProductDescriptionService.getOne(
                    new LambdaQueryWrapper<StoreProductDescription>()
                            .eq(StoreProductDescription::getProductId, product.getId())
                            .eq(StoreProductDescription::getType, ProductType.PRODUCT_TYPE_NORMAL));
            if (null != sd) {
                storeProductResponse.setContent(null == sd.getDescription() ? "" : sd.getDescription());
            }
            // 处理分类中文
            List<Category> cg = categoryService.getByIds(CrmebUtil.stringToArray(product.getCateId()));
            if (CollUtil.isEmpty(cg)) {
                storeProductResponse.setCateValues("");
            } else {
                storeProductResponse.setCateValues(cg.stream().map(Category::getName).collect(Collectors.joining(",")));
            }

            storeProductResponse.setCollectCount(
                    storeProductRelationService.getList(product.getId(), "collect").size());
            storeProductResponses.add(storeProductResponse);
        }
        // 多条sql查询处理分页正确
        return CommonPage.copyPageInfo(storeProductPage, storeProductResponses);
    }

    /**
     * 根据商品id集合获取
     *
     * @param productIds id集合
     * @return
     */
    @Override
    public List<StoreProduct> getListInIds(List<Integer> productIds) {
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(StoreProduct::getId, productIds);
        lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 新增产品
     *
     * @param request 新增产品request对象
     * @return 新增结果
     */
    @Override
    public Boolean save(StoreProductAddRequest request) {
        // 多规格需要校验规格参数
        if (!request.getSpecType()) {
            if (request.getAttrValue().size() > 1) {
                throw new CrmebException("单规格商品属性值不能大于1");
            }
        }

        StoreProduct storeProduct = new StoreProduct();
        BeanUtils.copyProperties(request, storeProduct);
        storeProduct.setId(null);
        storeProduct.setAddTime(DateUtil.getNowTime());
        storeProduct.setIsShow(false);

        // 设置Acticity活动
        storeProduct.setActivity(getProductActivityStr(request.getActivity()));

        //主图
        storeProduct.setImage(systemAttachmentService.clearPrefix(storeProduct.getImage()));

        //轮播图
        storeProduct.setSliderImage(systemAttachmentService.clearPrefix(storeProduct.getSliderImage()));
        // 展示图
        if (StrUtil.isNotEmpty(storeProduct.getFlatPattern())) {
            storeProduct.setFlatPattern(systemAttachmentService.clearPrefix(storeProduct.getFlatPattern()));
        }

        List<StoreProductAttrValueAddRequest> attrValueAddRequestList = request.getAttrValue();
        //计算价格
        StoreProductAttrValueAddRequest minAttrValue = attrValueAddRequestList.stream().min(Comparator.comparing(StoreProductAttrValueAddRequest::getPrice)).get();
        storeProduct.setPrice(minAttrValue.getPrice());
        storeProduct.setOtPrice(minAttrValue.getOtPrice());
        storeProduct.setCost(minAttrValue.getCost());
        storeProduct.setStock(attrValueAddRequestList.stream().mapToInt(StoreProductAttrValueAddRequest::getStock).sum());

        // 默认值设置
        if (ObjectUtil.isNull(request.getSort())) {
            storeProduct.setSort(0);
        }
        if (ObjectUtil.isNull(request.getIsHot())) {
            storeProduct.setIsHot(false);
        }
        if (ObjectUtil.isNull(request.getIsBenefit())) {
            storeProduct.setIsBenefit(false);
        }
        if (ObjectUtil.isNull(request.getIsBest())) {
            storeProduct.setIsBest(false);
        }
        if (ObjectUtil.isNull(request.getIsNew())) {
            storeProduct.setIsNew(false);
        }
        if (ObjectUtil.isNull(request.getIsGood())) {
            storeProduct.setIsGood(false);
        }
        if (ObjectUtil.isNull(request.getGiveIntegral())) {
            storeProduct.setGiveIntegral(0);
        }
        if (ObjectUtil.isNull(request.getFicti())) {
            storeProduct.setFicti(0);
        }

        List<StoreProductAttrAddRequest> addRequestList = request.getAttr();
        List<StoreProductAttr> attrList = addRequestList.stream().map(e -> {
            StoreProductAttr attr = new StoreProductAttr();
            BeanUtils.copyProperties(e, attr);
            attr.setType(ProductType.PRODUCT_TYPE_NORMAL);
            return attr;
        }).collect(Collectors.toList());

        List<StoreProductAttrValue> attrValueList = attrValueAddRequestList.stream().map(e -> {
            StoreProductAttrValue attrValue = new StoreProductAttrValue();
            BeanUtils.copyProperties(e, attrValue);
            attrValue.setId(null);
            attrValue.setSuk(getSku(e.getAttrValue()));
            attrValue.setQuota(0);
            attrValue.setQuotaShow(0);
            attrValue.setType(ProductType.PRODUCT_TYPE_NORMAL);
            attrValue.setImage(systemAttachmentService.clearPrefix(e.getImage()));
            return attrValue;
        }).collect(Collectors.toList());

        // 处理富文本
        StoreProductDescription spd = new StoreProductDescription();
        spd.setDescription(request.getContent().length() > 0 ? systemAttachmentService.clearPrefix(request.getContent()) : "");
        spd.setType(ProductType.PRODUCT_TYPE_NORMAL);

        Boolean execute = transactionTemplate.execute(e -> {
            //生成商品唯一编码
            Snowflake snowflake = IdUtil.getSnowflake();
            String idStr = snowflake.nextIdStr();
            String code = idStr.substring(idStr.length() - 10);
            storeProduct.setCode(code);
            storeProduct.setMerId(0);
            save(storeProduct);

            attrList.forEach(attr -> attr.setProductId(storeProduct.getId()));
            attrValueList.forEach(value -> value.setProductId(storeProduct.getId()));
            attrService.saveBatch(attrList);
            storeProductAttrValueService.saveBatch(attrValueList);

            spd.setProductId(storeProduct.getId());
            storeProductDescriptionService.deleteByProductId(storeProduct.getId(), ProductType.PRODUCT_TYPE_NORMAL);
            storeProductDescriptionService.save(spd);

            if (CollUtil.isNotEmpty(request.getCouponIds())) {
                List<StoreProductCoupon> couponList = new ArrayList<>();
                for (Integer couponId : request.getCouponIds()) {
                    StoreProductCoupon spc = new StoreProductCoupon(storeProduct.getId(), couponId, DateUtil.getNowTime());
                    couponList.add(spc);
                }
                storeProductCouponService.saveBatch(couponList);
            }
            return Boolean.TRUE;
        });

        return execute;
    }

    /**
     * 商品sku
     *
     * @param attrValue json字符串
     * @return sku
     */
    private String getSku(String attrValue) {
        LinkedHashMap<String, String> linkedHashMap = JSONObject.parseObject(attrValue, LinkedHashMap.class, Feature.OrderedField);
        Iterator<Map.Entry<String, String>> iterator = linkedHashMap.entrySet().iterator();
        List<String> strings = CollUtil.newArrayList();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            strings.add(next.getValue());
        }
//        List<String> strings = jsonObject.values().stream().map(o -> (String) o).collect(Collectors.toList());
        return String.join(",", strings);
    }

    /**
     * 商品活动字符串
     *
     * @param activityList 活动数组
     * @return 商品活动字符串
     */
    private String getProductActivityStr(List<String> activityList) {
        if (CollUtil.isEmpty(activityList)) {
            return "0, 1, 2, 3";
        }
        List<Integer> activities = new ArrayList<>();
        activityList.forEach(e -> {
            switch (e) {
                case ProductType.PRODUCT_TYPE_NORMAL_STR:
                    activities.add(ProductType.PRODUCT_TYPE_NORMAL);
                    break;
                case ProductType.PRODUCT_TYPE_SECKILL_STR:
                    activities.add(ProductType.PRODUCT_TYPE_SECKILL);
                    break;
                case ProductType.PRODUCT_TYPE_BARGAIN_STR:
                    activities.add(ProductType.PRODUCT_TYPE_BARGAIN);
                    break;
                case ProductType.PRODUCT_TYPE_PINGTUAN_STR:
                    activities.add(ProductType.PRODUCT_TYPE_PINGTUAN);
                    break;
            }
        });
        return activities.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    /**
     * 更新商品信息
     *
     * @param storeProductRequest 商品参数
     * @return 更新结果
     */
    @Override
    public Boolean update(StoreProductAddRequest storeProductRequest) {
        if (ObjectUtil.isNull(storeProductRequest.getId())) {
            throw new CrmebException("商品ID不能为空");
        }

        if (!storeProductRequest.getSpecType()) {
            if (storeProductRequest.getAttrValue().size() > 1) {
                throw new CrmebException("单规格商品属性值不能大于1");
            }
        }

        StoreProduct tempProduct = getById(storeProductRequest.getId());
        if (ObjectUtil.isNull(tempProduct)) {
            throw new CrmebException("商品不存在");
        }
        if (tempProduct.getIsRecycle() || tempProduct.getIsDel()) {
            throw new CrmebException("商品已删除");
        }
        if (tempProduct.getIsShow()) {
            throw new CrmebException("请先下架商品，再进行修改");
        }
        // 如果商品是活动商品主商品不允许修改
//        if (storeSeckillService.isExistByProductId(storeProductRequest.getId())) {
//            throw new CrmebException("商品作为秒杀商品的主商品，需要修改请先删除对应秒杀商品");
//        }
//        if (storeBargainService.isExistByProductId(storeProductRequest.getId())) {
//            throw new CrmebException("商品作为砍价商品的主商品，需要修改请先删除对应砍价商品");
//        }
//        if (storeCombinationService.isExistByProductId(storeProductRequest.getId())) {
//            throw new CrmebException("商品作为拼团商品的主商品，需要修改请先删除对应拼团商品");
//        }

        StoreProduct storeProduct = dao.selectById(storeProductRequest.getId());
        BeanUtils.copyProperties(storeProductRequest, storeProduct);

        // 设置Activity活动
        storeProduct.setActivity(getProductActivityStr(storeProductRequest.getActivity()));

        //主图
        storeProduct.setImage(systemAttachmentService.clearPrefix(storeProduct.getImage()));

        //轮播图
        storeProduct.setSliderImage(systemAttachmentService.clearPrefix(storeProduct.getSliderImage()));

        List<StoreProductAttrValueAddRequest> attrValueAddRequestList = storeProductRequest.getAttrValue();
        //计算价格
        StoreProductAttrValueAddRequest minAttrValue = attrValueAddRequestList.stream().min(Comparator.comparing(StoreProductAttrValueAddRequest::getPrice)).get();
        storeProduct.setPrice(minAttrValue.getPrice());
        storeProduct.setOtPrice(minAttrValue.getOtPrice());
        storeProduct.setCost(minAttrValue.getCost());
        storeProduct.setStock(attrValueAddRequestList.stream().mapToInt(StoreProductAttrValueAddRequest::getStock).sum());

        // attr部分
        List<StoreProductAttrAddRequest> addRequestList = storeProductRequest.getAttr();
        List<StoreProductAttr> attrAddList = CollUtil.newArrayList();
        List<StoreProductAttr> attrUpdateList = CollUtil.newArrayList();
        addRequestList.forEach(e -> {
            StoreProductAttr attr = new StoreProductAttr();
            BeanUtils.copyProperties(e, attr);
            attr.setProductId(storeProduct.getId());
            if (ObjectUtil.isNull(attr.getId())) {
                attr.setType(ProductType.PRODUCT_TYPE_NORMAL);
                attrAddList.add(attr);
            } else {
                attr.setIsDel(false);
                attrUpdateList.add(attr);
            }
        });

        // attrValue部分
        List<StoreProductAttrValue> attrValueAddList = CollUtil.newArrayList();
        List<StoreProductAttrValue> attrValueUpdateList = CollUtil.newArrayList();
        attrValueAddRequestList.forEach(e -> {
            StoreProductAttrValue attrValue = new StoreProductAttrValue();
            BeanUtils.copyProperties(e, attrValue);
            attrValue.setSuk(getSku(e.getAttrValue()));
            attrValue.setImage(systemAttachmentService.clearPrefix(e.getImage()));
            attrValue.setProductId(storeProduct.getId());
            if (ObjectUtil.isNull(attrValue.getId()) || attrValue.getId().equals(0)) {
                attrValue.setId(null);
                attrValue.setQuota(0);
                attrValue.setQuotaShow(0);
                attrValue.setType(ProductType.PRODUCT_TYPE_NORMAL);
                attrValueAddList.add(attrValue);
            } else {
                attrValue.setIsDel(false);
                attrValueUpdateList.add(attrValue);
            }
        });

        // 处理富文本
        StoreProductDescription spd = new StoreProductDescription();
        spd.setDescription(storeProductRequest.getContent().length() > 0 ? systemAttachmentService.clearPrefix(storeProductRequest.getContent()) : "");
        spd.setType(ProductType.PRODUCT_TYPE_NORMAL);
        spd.setProductId(storeProduct.getId());

        Boolean execute = transactionTemplate.execute(e -> {
            dao.updateById(storeProduct);

            // 先删除原用attr+value
            attrService.deleteByProductIdAndType(storeProduct.getId(), ProductType.PRODUCT_TYPE_NORMAL);
            storeProductAttrValueService.deleteByProductIdAndType(storeProduct.getId(), ProductType.PRODUCT_TYPE_NORMAL);

            if (CollUtil.isNotEmpty(attrAddList)) {
                attrService.saveBatch(attrAddList);
            }
            if (CollUtil.isNotEmpty(attrUpdateList)) {
                attrService.saveOrUpdateBatch(attrUpdateList);
            }

            if (CollUtil.isNotEmpty(attrValueAddList)) {
                storeProductAttrValueService.saveBatch(attrValueAddList);
            }
            if (CollUtil.isNotEmpty(attrValueUpdateList)) {
                storeProductAttrValueService.saveOrUpdateBatch(attrValueUpdateList);
            }

            storeProductDescriptionService.deleteByProductId(storeProduct.getId(), ProductType.PRODUCT_TYPE_NORMAL);
            storeProductDescriptionService.save(spd);

            if (CollUtil.isNotEmpty(storeProductRequest.getCouponIds())) {
                storeProductCouponService.deleteByProductId(storeProduct.getId());
                List<StoreProductCoupon> couponList = new ArrayList<>();
                for (Integer couponId : storeProductRequest.getCouponIds()) {
                    StoreProductCoupon spc = new StoreProductCoupon(storeProduct.getId(), couponId, DateUtil.getNowTime());
                    couponList.add(spc);
                }
                storeProductCouponService.saveBatch(couponList);
            } else {
                storeProductCouponService.deleteByProductId(storeProduct.getId());
            }

            return Boolean.TRUE;
        });

        return execute;
    }

    /**
     * 商品详情（管理端）
     *
     * @param id 商品id
     * @return StoreProductInfoResponse
     */
    @Override
    public StoreProductInfoResponse getInfo(Integer id) {
        StoreProduct storeProduct = dao.selectById(id);
        if (ObjectUtil.isNull(storeProduct)) {
            throw new CrmebException("未找到对应商品信息");
        }

        StoreProductInfoResponse storeProductResponse = new StoreProductInfoResponse();
        BeanUtils.copyProperties(storeProduct, storeProductResponse);

        // 设置商品所参与的活动
        List<String> activityList = getProductActivityList(storeProduct.getActivity());
        storeProductResponse.setActivity(activityList);

        List<StoreProductAttr> attrList = attrService.getListByProductIdAndTypeNotDel(storeProduct.getId(), ProductType.PRODUCT_TYPE_NORMAL);
        storeProductResponse.setAttr(attrList);

        List<StoreProductAttrValue> attrValueList = storeProductAttrValueService.getListByProductIdAndType(storeProduct.getId(), ProductType.PRODUCT_TYPE_NORMAL);
        List<AttrValueResponse> valueResponseList = attrValueList.stream().map(e -> {
            AttrValueResponse valueResponse = new AttrValueResponse();
            BeanUtils.copyProperties(e, valueResponse);
            return valueResponse;
        }).collect(Collectors.toList());
        storeProductResponse.setAttrValue(valueResponseList);

        StoreProductDescription sd = storeProductDescriptionService.getByProductIdAndType(storeProduct.getId(), ProductType.PRODUCT_TYPE_NORMAL);
        if (ObjectUtil.isNotNull(sd)) {
            storeProductResponse.setContent(ObjectUtil.isNull(sd.getDescription()) ? "" : sd.getDescription());
        }

        // 获取已关联的优惠券
        List<StoreProductCoupon> storeProductCoupons = storeProductCouponService.getListByProductId(storeProduct.getId());
        if (CollUtil.isNotEmpty(storeProductCoupons)) {
            List<Integer> ids = storeProductCoupons.stream().map(StoreProductCoupon::getIssueCouponId).collect(Collectors.toList());
            storeProductResponse.setCouponIds(ids);
        }
        return storeProductResponse;
    }

    /**
     * 商品活动字符列表
     *
     * @param activityStr 商品活动字符串
     * @return 商品活动字符列表
     */
    private List<String> getProductActivityList(String activityStr) {
        List<String> activityList = CollUtil.newArrayList();
        if ("0, 1, 2, 3".equals(activityStr)) {
            activityList.add(ProductType.PRODUCT_TYPE_NORMAL_STR);
            activityList.add(ProductType.PRODUCT_TYPE_SECKILL_STR);
            activityList.add(ProductType.PRODUCT_TYPE_BARGAIN_STR);
            activityList.add(ProductType.PRODUCT_TYPE_PINGTUAN_STR);
            return activityList;
        }

        if (StringUtils.isNotBlank(activityStr)) {
            String[] split = activityStr.split(",");
            for (String s : split) {
                Integer integer = Integer.valueOf(s);
                if (integer.equals(ProductType.PRODUCT_TYPE_NORMAL)) {
                    activityList.add(ProductType.PRODUCT_TYPE_NORMAL_STR);
                }
                if (integer.equals(ProductType.PRODUCT_TYPE_SECKILL)) {
                    activityList.add(ProductType.PRODUCT_TYPE_SECKILL_STR);
                }
                if (integer.equals(ProductType.PRODUCT_TYPE_BARGAIN)) {
                    activityList.add(ProductType.PRODUCT_TYPE_BARGAIN_STR);
                }
                if (integer.equals(ProductType.PRODUCT_TYPE_PINGTUAN)) {
                    activityList.add(ProductType.PRODUCT_TYPE_PINGTUAN_STR);
                }
            }
        }
        return activityList;
    }

    /**
     * 根据商品tabs获取对应类型的产品数量
     *
     * @return List
     */
    @Override
    public List<StoreProductTabsHeader> getTabsHeader() {
        List<StoreProductTabsHeader> headers = new ArrayList<>();
        StoreProductTabsHeader header1 = new StoreProductTabsHeader(0, "出售中商品", 1);
        StoreProductTabsHeader header2 = new StoreProductTabsHeader(0, "仓库中商品", 2);
        StoreProductTabsHeader header3 = new StoreProductTabsHeader(0, "已经售馨商品", 3);
        StoreProductTabsHeader header4 = new StoreProductTabsHeader(0, "警戒库存", 4);
        StoreProductTabsHeader header5 = new StoreProductTabsHeader(0, "商品回收站", 5);
        headers.add(header1);
        headers.add(header2);
        headers.add(header3);
        headers.add(header4);
        headers.add(header5);
        for (StoreProductTabsHeader h : headers) {
            LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            switch (h.getType()) {
                case 1:
                    //出售中（已上架）
                    lambdaQueryWrapper.eq(StoreProduct::getIsShow, true);
                    lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                case 2:
                    //仓库中（未上架）
                    lambdaQueryWrapper.eq(StoreProduct::getIsShow, false);
                    lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                case 3:
                    //已售罄
                    lambdaQueryWrapper.le(StoreProduct::getStock, 0);
                    lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                case 4:
                    //警戒库存
                    Integer stock = Integer.parseInt(systemConfigService.getValueByKey("store_stock"));
                    lambdaQueryWrapper.le(StoreProduct::getStock, stock);
                    lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                case 5:
                    //回收站
                    lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, true);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                default:
                    break;
            }
            List<StoreProduct> storeProducts = dao.selectList(lambdaQueryWrapper);
            h.setCount(storeProducts.size());
        }

        return headers;
    }

    /**
     * 根据商品id取出二级分类
     *
     * @param productIdStr String 商品分类
     * @return List<Integer>
     */
    @Override
    public List<Integer> getSecondaryCategoryByProductId(String productIdStr) {
        List<Integer> idList = new ArrayList<>();

        if (StringUtils.isBlank(productIdStr)) {
            return idList;
        }
        List<Integer> productIdList = CrmebUtil.stringToArray(productIdStr);
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(StoreProduct::getId, productIdList);
        List<StoreProduct> productList = dao.selectList(lambdaQueryWrapper);
        if (productIdList.size() < 1) {
            return idList;
        }

        //把所有的分类id写入集合
        for (StoreProduct storeProduct : productList) {
            List<Integer> categoryIdList = CrmebUtil.stringToArray(storeProduct.getCateId());
            idList.addAll(categoryIdList);
        }

        //去重
        List<Integer> cateIdList = idList.stream().distinct().collect(Collectors.toList());
        if (cateIdList.size() < 1) {
            return idList;
        }

        //取出所有的二级分类
        List<Category> categoryList = categoryService.getByIds(cateIdList);
        if (categoryList.size() < 1) {
            return idList;
        }

        for (Category category : categoryList) {
            List<Integer> parentIdList = CrmebUtil.stringToArrayByRegex(category.getPath(), "/");
            if (parentIdList.size() > 2) {
                Integer secondaryCategoryId = parentIdList.get(2);
                if (secondaryCategoryId > 0) {
                    idList.add(secondaryCategoryId);
                }
            }
        }
        return idList;
    }

    /**
     * 根据其他平台url导入产品信息
     *
     * @param url 待导入平台url
     * @param tag 1=淘宝，2=京东，3=苏宁，4=拼多多， 5=天猫
     * @return StoreProductRequest
     */
    @Override
    public StoreProductRequest importProductFromUrl(String url, int tag) {
        StoreProductRequest productRequest = null;
        try {
            switch (tag) {
                case 1:
                    productRequest = productUtils.getTaobaoProductInfo(url, tag);
                    break;
                case 2:
                    productRequest = productUtils.getJDProductInfo(url, tag);
                    break;
                case 3:
                    productRequest = productUtils.getSuningProductInfo(url, tag);
                    break;
                case 4:
                    productRequest = productUtils.getPddProductInfo(url, tag);
                    break;
                case 5:
                    productRequest = productUtils.getTmallProductInfo(url, tag);
                    break;
            }
        } catch (Exception e) {
            throw new CrmebException("确认URL和平台是否正确，以及平台费用是否足额" + e.getMessage());
        }
        return productRequest;
    }

    /**
     * @param productId 商品id
     * @param type      类型：recycle——回收站 delete——彻底删除
     * @return Boolean
     */
    @Override
    public Boolean deleteProduct(Integer productId, String type) {
        StoreProduct product = getById(productId);
        if (ObjectUtil.isNull(product)) {
            throw new CrmebException("商品不存在");
        }
        if (StrUtil.isNotBlank(type) && "recycle".equals(type) && product.getIsDel()) {
            throw new CrmebException("商品已存在回收站");
        }

        LambdaUpdateWrapper<StoreProduct> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        if (StrUtil.isNotBlank(type) && "delete".equals(type)) {
            // 判断商品活动状态(秒杀、砍价、拼团)
            isExistActivity(productId);

            lambdaUpdateWrapper.eq(StoreProduct::getId, productId);
            lambdaUpdateWrapper.set(StoreProduct::getIsDel, true);
            return update(lambdaUpdateWrapper);
        }
        lambdaUpdateWrapper.eq(StoreProduct::getId, productId);
        lambdaUpdateWrapper.set(StoreProduct::getIsRecycle, true);
        update(lambdaUpdateWrapper);

        //查询礼品卡中的关联商品 如果存在则删除
        this.giftCardProductService.update(new LambdaUpdateWrapper<GiftCardProduct>().eq(GiftCardProduct::getProductId, productId).set(GiftCardProduct::getIsDel, 1));
        return true;
    }

    /**
     * 判断商品活动状态(秒杀、砍价、拼团)
     *
     * @param productId
     */
    private void isExistActivity(Integer productId) {
        Boolean existActivity = false;
        // 秒杀活动判断
        existActivity = storeSeckillService.isExistActivity(productId);
        if (existActivity) {
            throw new CrmebException("有商品关联的秒杀商品活动开启中，不能删除");
        }
        // 砍价活动判断
        existActivity = storeBargainService.isExistActivity(productId);
        if (existActivity) {
            throw new CrmebException("有商品关联的砍价商品活动开启中，不能删除");
        }
        // 拼团活动判断
        existActivity = storeCombinationService.isExistActivity(productId);
        if (existActivity) {
            throw new CrmebException("有商品关联的拼团商品活动开启中，不能删除");
        }
    }

    /**
     * 恢复已删除的商品
     *
     * @param productId 商品id
     * @return 恢复结果
     */
    @Override
    public Boolean reStoreProduct(Integer productId) {
        LambdaUpdateWrapper<StoreProduct> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(StoreProduct::getId, productId);
        lambdaUpdateWrapper.set(StoreProduct::getIsRecycle, false);

        update(lambdaUpdateWrapper);
        //礼品卡关联商品重新上架
        this.giftCardProductService.restore(productId);
        return true;
    }

    /**
     * 获取复制商品配置
     *
     * @return copyType 复制类型：1：一号通
     * copyNum 复制条数(一号通类型下有值)
     */
    @Override
    public MyRecord copyConfig() {
        String copyType = systemConfigService.getValueByKey("system_product_copy_type");
        if (StrUtil.isBlank(copyType)) {
            throw new CrmebException("请先进行采集商品配置");
        }
        int copyNum = 0;
        if ("1".equals(copyType)) {// 一号通
            JSONObject info = onePassService.info();
            copyNum = Optional.ofNullable(info.getJSONObject("copy").getInteger("num")).orElse(0);
        }
        MyRecord record = new MyRecord();
        record.set("copyType", copyType);
        record.set("copyNum", copyNum);
        return record;
    }

    /**
     * 复制平台商品
     *
     * @param url 商品链接
     * @return MyRecord
     */
    @Override
    public MyRecord copyProduct(String url) {
        JSONObject jsonObject = onePassService.copyGoods(url);
        StoreProductRequest storeProductRequest = ProductUtils.onePassCopyTransition(jsonObject);
        MyRecord record = new MyRecord();
        return record.set("info", storeProductRequest);
    }

    /**
     * 添加/扣减库存
     *
     * @param id   商品id
     * @param num  数量
     * @param type 类型：add—添加，sub—扣减
     */
    @Override
    public Boolean operationStock(Integer id, Integer num, String type) {
        UpdateWrapper<StoreProduct> updateWrapper = new UpdateWrapper<>();
        if ("add".equals(type)) {
            updateWrapper.setSql(StrUtil.format("stock = stock + {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales - {}", num));
        }
        if ("sub".equals(type)) {
            updateWrapper.setSql(StrUtil.format("stock = stock - {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales + {}", num));
            // 扣减时加乐观锁保证库存不为负
            updateWrapper.last(StrUtil.format(" and (stock - {} >= 0)", num));
        }
        updateWrapper.eq("id", id);
        boolean update = update(updateWrapper);
        if (!update) {
            throw new CrmebException("更新普通商品库存失败,商品id = " + id);
        }
        return update;
    }

    /**
     * 下架
     *
     * @param id 商品id
     */
    @Override
    public Boolean offShelf(Integer id) {
        StoreProduct storeProduct = getById(id);
        if (ObjectUtil.isNull(storeProduct)) {
            throw new CrmebException("商品不存在");
        }
        if (!storeProduct.getIsShow()) {
            return true;
        }

        storeProduct.setIsShow(false);
        Boolean execute = transactionTemplate.execute(e -> {
            dao.updateById(storeProduct);
            storeCartService.productStatusNotEnable(id);
            // 商品下架时，清除用户收藏
            storeProductRelationService.deleteByProId(storeProduct.getId());
            //查询礼品卡中的关联商品 如果存在则删除
            this.giftCardProductService.update(new LambdaUpdateWrapper<GiftCardProduct>().eq(GiftCardProduct::getProductId, id).set(GiftCardProduct::getIsDel, 1));
            return Boolean.TRUE;
        });

        return execute;
    }

    /**
     * 上架
     *
     * @param id 商品id
     * @return Boolean
     */
    @Override
    public Boolean putOnShelf(Integer id) {
        StoreProduct storeProduct = getById(id);
        if (ObjectUtil.isNull(storeProduct)) {
            throw new CrmebException("商品不存在");
        }
        if (storeProduct.getIsShow()) {
            return true;
        }

        // 获取商品skuid
        StoreProductAttrValue tempSku = new StoreProductAttrValue();
        tempSku.setProductId(id);
        tempSku.setType(ProductType.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttrValue> skuList = storeProductAttrValueService.getByEntity(tempSku);
        List<Integer> skuIdList = skuList.stream().map(StoreProductAttrValue::getId).collect(Collectors.toList());

        storeProduct.setIsShow(true);
        Boolean execute = transactionTemplate.execute(e -> {
            dao.updateById(storeProduct);
            storeCartService.productStatusNoEnable(skuIdList);

            //礼品卡关联商品重新上架
            this.giftCardProductService.restore(id);
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 首页商品列表
     *
     * @param type             类型 【1 精品推荐 2 热门榜单 3首发新品 4促销单品】
     * @param pageParamRequest 分页参数
     * @return CommonPage
     */
    @Override
    public List<StoreProduct> getIndexProduct(Integer type, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.select(StoreProduct::getId, StoreProduct::getImage, StoreProduct::getStoreName,
                StoreProduct::getPrice, StoreProduct::getOtPrice, StoreProduct::getActivity);
        switch (type) {
            case Constants.INDEX_RECOMMEND_BANNER: //精品推荐
                lambdaQueryWrapper.eq(StoreProduct::getIsBest, true);
                break;
            case Constants.INDEX_HOT_BANNER: //热门榜单
                lambdaQueryWrapper.eq(StoreProduct::getIsHot, true);
                break;
            case Constants.INDEX_NEW_BANNER: //首发新品
                lambdaQueryWrapper.eq(StoreProduct::getIsNew, true);
                break;
            case Constants.INDEX_BENEFIT_BANNER: //促销单品
                lambdaQueryWrapper.eq(StoreProduct::getIsBenefit, true);
                break;
            case Constants.INDEX_GOOD_BANNER: // 优选推荐
                lambdaQueryWrapper.eq(StoreProduct::getIsGood, true);
                break;
        }

        lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
        lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
        lambdaQueryWrapper.gt(StoreProduct::getStock, 0);
        lambdaQueryWrapper.eq(StoreProduct::getIsShow, true);

        lambdaQueryWrapper.orderByDesc(StoreProduct::getSort);
        lambdaQueryWrapper.orderByDesc(StoreProduct::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 获取商品移动端列表
     *
     * @param request     筛选参数
     * @param pageRequest 分页参数
     * @return List
     */
    @Override
    public List<StoreProduct> findH5List(ProductRequest request, PageParamRequest pageRequest) {

        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();
        // id、名称、图片、价格、销量、活动
        lqw.select(StoreProduct::getId, StoreProduct::getStoreName, StoreProduct::getImage, StoreProduct::getPrice, StoreProduct::getOtPrice,
                StoreProduct::getActivity, StoreProduct::getSales, StoreProduct::getFicti, StoreProduct::getUnitName,
                StoreProduct::getFlatPattern, StoreProduct::getStock, StoreProduct::getIsDeliver, StoreProduct::getIsPickup);

        lqw.eq(StoreProduct::getIsRecycle, false);
        lqw.eq(StoreProduct::getIsDel, false);
        lqw.eq(StoreProduct::getMerId, false);
        lqw.gt(StoreProduct::getStock, 0);
        lqw.eq(StoreProduct::getIsShow, true);

        if (ObjectUtil.isNotNull(request.getCid()) && request.getCid() > 0) {
            //查找当前类下的所有子类
            List<Category> childVoListByPid = categoryService.getChildVoListByPid(request.getCid());
            List<Integer> categoryIdList = childVoListByPid.stream().map(Category::getId).collect(Collectors.toList());
            categoryIdList.add(request.getCid());
            lqw.apply(CrmebUtil.getFindInSetSql("cate_id", (ArrayList<Integer>) categoryIdList));
        }

        if (StrUtil.isNotBlank(request.getKeyword())) {
            if (CrmebUtil.isString2Num(request.getKeyword())) {
                Integer productId = Integer.valueOf(request.getKeyword());
                lqw.like(StoreProduct::getId, productId);
            } else {
                lqw.like(StoreProduct::getStoreName, request.getKeyword());
            }
        }

        //价格筛选
        if (null != request.getMaxSalePrice()) {
            lqw.le(StoreProduct::getPrice, request.getMaxSalePrice());
        }
        if (null != request.getMinSalePrice()) {
            lqw.ge(StoreProduct::getPrice, request.getMinSalePrice());
        }

        // 排序部分
        if (StrUtil.isNotBlank(request.getSalesOrder())) {
            if (request.getSalesOrder().equals(Order.DESC)) {
                lqw.last(" order by (sales + ficti) desc, sort desc, id desc");
            } else {
                lqw.last(" order by (sales + ficti) asc, sort asc, id asc");
            }
        } else {
            if (StrUtil.isNotBlank(request.getPriceOrder())) {
                if (request.getPriceOrder().equals(Order.DESC)) {
                    lqw.orderByDesc(StoreProduct::getPrice);
                } else {
                    lqw.orderByAsc(StoreProduct::getPrice);
                }
            }

            lqw.orderByDesc(StoreProduct::getSort);
            lqw.orderByDesc(StoreProduct::getId);
        }
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        return dao.selectList(lqw);
    }

    /**
     * 获取移动端商品详情
     *
     * @param id 商品id
     * @return StoreProduct
     */
    @Override
    public StoreProduct getH5Detail(Integer id) {
        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreProduct::getId, StoreProduct::getImage, StoreProduct::getStoreName, StoreProduct::getSliderImage,
                StoreProduct::getOtPrice, StoreProduct::getStock, StoreProduct::getSales, StoreProduct::getPrice, StoreProduct::getActivity,
                StoreProduct::getFicti, StoreProduct::getIsSub, StoreProduct::getStoreInfo, StoreProduct::getBrowse, StoreProduct::getUnitName, StoreProduct::getTempId, StoreProduct::getIsPickup, StoreProduct::getIsDeliver);
        lqw.eq(StoreProduct::getId, id);
        lqw.eq(StoreProduct::getIsRecycle, false);
        lqw.eq(StoreProduct::getIsDel, false);
        lqw.eq(StoreProduct::getIsShow, true);
        StoreProduct storeProduct = dao.selectOne(lqw);
        if (ObjectUtil.isNull(storeProduct)) {
            throw new CrmebException(StrUtil.format("未找到编号为{}的商品", id));
        }

        StoreProductDescription sd = storeProductDescriptionService.getOne(
                new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, storeProduct.getId())
                        .eq(StoreProductDescription::getType, ProductType.PRODUCT_TYPE_NORMAL));
        if (ObjectUtil.isNotNull(sd)) {
            storeProduct.setContent(StrUtil.isBlank(sd.getDescription()) ? "" : sd.getDescription());
        }
        return storeProduct;
    }

    /**
     * 获取移动端商品详情
     *
     * @param id 商品id
     * @return StoreProduct
     */
    @Override
    public StoreProduct getH5Detail(Integer id, Boolean isDel) {
        StoreProduct storeProduct = dao.getOne(id);
        if (ObjectUtil.isNull(storeProduct)) {
            throw new CrmebException(StrUtil.format("未找到编号为{}的商品", id));
        }

        StoreProductDescription sd = storeProductDescriptionService.getOne(
                new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, storeProduct.getId())
                        .eq(StoreProductDescription::getType, ProductType.PRODUCT_TYPE_NORMAL));
        if (ObjectUtil.isNotNull(sd)) {
            storeProduct.setContent(StrUtil.isBlank(sd.getDescription()) ? "" : sd.getDescription());
        }
        return storeProduct;
    }

    /**
     * 获取购物车商品信息
     *
     * @param productId 商品编号
     * @return StoreProduct
     */
    @Override
    public StoreProduct getCartByProId(Integer productId) {
        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreProduct::getId, StoreProduct::getImage, StoreProduct::getStoreName, StoreProduct::getIsDeliver, StoreProduct::getIsPickup);
        lqw.eq(StoreProduct::getId, productId);
        return dao.selectOne(lqw);
    }

    /**
     * 根据日期获取新增商品数量
     *
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    @Override
    public Integer getNewProductByDate(String date) {
        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreProduct::getId);
        lqw.eq(StoreProduct::getIsDel, 0);
        lqw.apply("date_format(add_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(lqw);
    }

    /**
     * 获取所有未删除的商品
     *
     * @return List<StoreProduct>
     */
    @Override
    public List<StoreProduct> findAllProductByNotDelte() {
        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreProduct::getId);
        lqw.eq(StoreProduct::getIsDel, 0);
        return dao.selectList(lqw);
    }

    /**
     * 模糊搜索商品名称
     *
     * @param productName 商品名称
     * @return List
     */
    @Override
    public List<StoreProduct> likeProductName(String productName) {
        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreProduct::getId);
        lqw.like(StoreProduct::getStoreName, productName);
        lqw.eq(StoreProduct::getIsDel, 0);
        return dao.selectList(lqw);
    }

    /**
     * 警戒库存数量
     *
     * @return Integer
     */
    @Override
    public Integer getVigilanceInventoryNum() {
        Integer stock = Integer.parseInt(systemConfigService.getValueByKey("store_stock"));
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.le(StoreProduct::getStock, stock);
        lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
        lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 销售中（上架）商品数量
     *
     * @return Integer
     */
    @Override
    public Integer getOnSaleNum() {
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(StoreProduct::getIsShow, true);
        lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
        lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 未销售（仓库）商品数量
     *
     * @return Integer
     */
    @Override
    public Integer getNotSaleNum() {
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(StoreProduct::getIsShow, false);
        lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
        lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 获取商品排行榜
     * 1.   3个商品以内不返回数据
     * 2.   TOP10
     *
     * @return List
     */
    @Override
    public List<StoreProduct> getLeaderboard() {
        QueryWrapper<StoreProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_show", true);
        queryWrapper.eq("is_recycle", false);
        queryWrapper.eq("is_del", false);
        queryWrapper.last("limit 10");
        Integer count = dao.selectCount(queryWrapper);
        if (count < 4) {
            return CollUtil.newArrayList();
        }
        queryWrapper.select("id", "store_name", "image", "price", "ot_price", "(sales + ficti) as sales");
        queryWrapper.orderByDesc("sales");
        return dao.selectList(queryWrapper);
    }

    @Override
    public void updateFeature(List<Integer> idList, String feature, boolean flag) {
        LambdaUpdateWrapper<StoreProduct> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        switch (feature) {
            case "isHot":
                lambdaUpdateWrapper.set(StoreProduct::getIsHot, flag);
                break;
            case "isBenefit":
                lambdaUpdateWrapper.set(StoreProduct::getIsBenefit, flag);
                break;
            case "isBest":
                lambdaUpdateWrapper.set(StoreProduct::getIsBest, flag);
                break;
            case "isNew":
                lambdaUpdateWrapper.set(StoreProduct::getIsNew, flag);
                break;
            case "isGood":
                lambdaUpdateWrapper.set(StoreProduct::getIsGood, flag);
                break;
            case "isSafe":
                lambdaUpdateWrapper.set(StoreProduct::getIsSafe, flag);
                break;
            case "isEnjoy":
                lambdaUpdateWrapper.set(StoreProduct::getIsEnjoy, flag);
                break;
        }
        lambdaUpdateWrapper.in(StoreProduct::getId, idList);
        update(lambdaUpdateWrapper);
    }

}

