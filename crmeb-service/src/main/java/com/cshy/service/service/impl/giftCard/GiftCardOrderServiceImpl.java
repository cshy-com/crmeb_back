package com.cshy.service.service.impl.giftCard;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cshy.common.constants.Constants;
import com.cshy.common.enums.SMSTemplateEnum;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.dto.giftCard.GiftCardOrderDto;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.entity.giftCard.GiftCard;
import com.cshy.common.model.entity.giftCard.GiftCardOrder;
import com.cshy.common.model.entity.giftCard.GiftCardProduct;
import com.cshy.common.model.entity.giftCard.GiftCardType;
import com.cshy.common.model.entity.product.StoreProduct;
import com.cshy.common.model.entity.product.StoreProductAttrValue;
import com.cshy.common.model.entity.user.User;
import com.cshy.common.model.entity.user.UserAddress;
import com.cshy.common.model.query.giftCard.GiftCardOrderQuery;
import com.cshy.common.model.response.StoreProductInfoResponse;
import com.cshy.common.model.vo.dateLimitUtilVo;
import com.cshy.common.model.vo.giftCard.GiftCardOrderVo;
import com.cshy.common.token.FrontTokenComponent;
import com.cshy.common.utils.*;
import com.cshy.service.dao.giftCard.GiftCardOrderDao;
import com.cshy.service.service.*;
import com.cshy.service.service.giftCard.GiftCardOrderService;
import com.cshy.service.service.giftCard.GiftCardProductService;
import com.cshy.service.service.giftCard.GiftCardService;
import com.cshy.service.service.giftCard.GiftCardTypeService;
import com.cshy.service.util.IdWorkerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class GiftCardOrderServiceImpl extends BaseServiceImpl<GiftCardOrder, GiftCardOrderDto,
        GiftCardOrderQuery, GiftCardOrderVo, GiftCardOrderDao> implements GiftCardOrderService {
    private static final Logger logger = LoggerFactory.getLogger(GiftCardOrderServiceImpl.class);

    @Resource
    private GiftCardProductService giftCardProductService;

    @Resource
    private GiftCardTypeService giftCardTypeService;

    @Resource
    private GiftCardService giftCardService;

    @Resource
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private SmsService smsService;

    @Value("${url.giftCardUrl}")
    private String longURL;

    @Autowired
    private FrontTokenComponent tokenComponent;

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    protected void onBeforeAdd(GiftCardOrderDto dto) {
        //查询礼品卡
        GiftCard giftCard = giftCardService.getOne(new LambdaQueryWrapper<GiftCard>().eq(GiftCard::getPickupCode, dto.getPickupCode()));
        if (Objects.isNull(giftCard) || giftCard.getCardStatus() == 1 || giftCard.getIsDel() == 1 || giftCard.getUsingStatus() == 2)
            throw new CrmebException("该礼品卡已失效，请联系客服");

        //查询商品/礼品卡类型/礼品卡状态
        GiftCardType giftCardType = giftCardTypeService.getOne(new LambdaQueryWrapper<GiftCardType>().eq(GiftCardType::getId, giftCard.getGiftCardTypeId()));
        if (Objects.isNull(giftCardType) || giftCardType.getStatus() == 1 || giftCardType.getIsDel() == 1)
            throw new CrmebException("该种类礼品卡已下架，请联系客服");

        List<GiftCardProduct> giftCardProductList = giftCardProductService.list(new LambdaQueryWrapper<GiftCardProduct>()
                .eq(GiftCardProduct::getProductId, dto.getProductId())
                .eq(GiftCardProduct::getGiftCardTypeId, giftCard.getGiftCardTypeId()));
//                .eq(GiftCardProduct::getIsDel, 0));

        if (CollUtil.isEmpty(giftCardProductList))
            throw new CrmebException("该商品已下架，请选择返回上级页面选择其他商品");

        //检查规格属性
        StoreProductAttrValue attrValue = storeProductAttrValueService.getById(dto.getAttrValueId());
        if (Objects.isNull(attrValue) || !attrValue.getProductId().equals(dto.getProductId()))
            throw new CrmebException("规格属性错误");

        //生成短连接
        URLShortener urlShortener = new URLShortener();
        String shortenURL = urlShortener.shortenURL(longURL.replace("PICKUPCODE", giftCard.getPickupCode()));

        dto.setShortenUrl(shortenURL);

        Boolean execute = transactionTemplate.execute(e -> {
            //生成订单编号
            String orderNo = IdWorkerUtils.getInstance().nextId();
            dto.setOrderNo(orderNo);

            dto.setGiftCardId(giftCard.getId());
            dto.setGiftCardTypeId(giftCardType.getId());

            //默认状态
            dto.setOrderStatus(0);

            //更新卡状态
            giftCard.setUsingStatus(2);
            giftCardService.updateById(giftCard);

            //更新库存
            // 普通商品库存
            storeProductService.operationStock(dto.getProductId(), 1, "sub");
            // 普通商品规格扣库存
            storeProductAttrValueService.operationStock(attrValue.getId(), 1, "sub", Constants.PRODUCT_TYPE_NORMAL);
            return true;
        });

        if (!execute) {
            throw new CrmebException("订单生成失败");
        }

        super.onBeforeAdd(dto);
    }

    @Override
    protected void onBeforeUpdate(GiftCardOrderDto dto) {
        if (StringUtils.isNotBlank(dto.getTrackingNo()))
            dto.setOrderStatus(1);
        super.onBeforeUpdate(dto);
    }

    @Override
    protected void onAfterPage(Page<GiftCardOrderVo> page) {
        page.getRecords().forEach(giftCardOrderVo -> {
            //查询用户信息
            User info = userService.getById(giftCardOrderVo.getUserId());
            Map<String, Object> user = new HashMap<>();
            user.put("name", info.getNickname());
            user.put("id", giftCardOrderVo.getUserId());
            giftCardOrderVo.setUser(user);

            //查询用户地址
            UserAddress userAddress = userAddressService.getById(giftCardOrderVo.getAddressId(), true);
            Map<String, Object> address = CommonUtil.objToMap(userAddress, UserAddress.class);
            giftCardOrderVo.setUserAddress(address);

            //查询商品信息
            StoreProduct storeProduct = storeProductService.getH5Detail(giftCardOrderVo.getProductId());
            Map<String, Object> product = CommonUtil.objToMap(storeProduct, StoreProduct.class);
            giftCardOrderVo.setStoreProduct(product);

            //卡类型名称
            GiftCardType giftCardType = giftCardTypeService.getById(giftCardOrderVo.getGiftCardTypeId());
            giftCardOrderVo.setGiftCardTypeName(giftCardType.getName());

            //查询规格
            StoreProductAttrValue storeProductAttrValue = storeProductAttrValueService.getById(giftCardOrderVo.getAttrValueId());
            Map<String, Object> attrValue = CommonUtil.objToMap(storeProductAttrValue, StoreProductAttrValue.class);
            giftCardOrderVo.setAttrValue(attrValue);

            //查询礼品卡
            GiftCard giftCard = giftCardService.getById(giftCardOrderVo.getGiftCardId());
            Map<String, Object> giftCardMap = CommonUtil.objToMap(giftCard, GiftCard.class);
            giftCardMap.remove("qrcode");
            giftCardOrderVo.setGiftCard(giftCardMap);
        });
        super.onAfterPage(page);
    }

    @Override
    public Map<String, Object> getOrderStatusNum() {
        //所有
        List<GiftCardOrder> list = this.list();
        //0 待发货
        List<GiftCardOrder> list1 = this.list(new LambdaQueryWrapper<GiftCardOrder>().eq(GiftCardOrder::getOrderStatus, 0));
        //1 待收货
        List<GiftCardOrder> list2 = this.list(new LambdaQueryWrapper<GiftCardOrder>().eq(GiftCardOrder::getOrderStatus, 1));
        // 2 已收货
        List<GiftCardOrder> list3 = this.list(new LambdaQueryWrapper<GiftCardOrder>().eq(GiftCardOrder::getOrderStatus, 2));
        //3 已完结
        List<GiftCardOrder> list4 = this.list(new LambdaQueryWrapper<GiftCardOrder>().eq(GiftCardOrder::getOrderStatus, 3));

        Map<String, Object> res = new HashMap<>();
        res.put("unshipped", list1.size());
        res.put("delivering", list2.size());
        res.put("delivered", list3.size());
        res.put("completed", list4.size());
        res.put("all", list.size());

        return res;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void ship(String orderId, String trackingNo, Integer type, HttpServletRequest request) {
        //更新状态和物流单号
        GiftCardOrder cardOrder = this.getById(orderId);
        cardOrder.setOrderStatus(1);
        cardOrder.setTrackingNo(trackingNo);
        this.updateById(cardOrder);
        //查询商品
        StoreProductInfoResponse productServiceInfo = this.storeProductService.getInfo(cardOrder.getProductId());
        //发送短信
        UserAddress userAddress = userAddressService.getById(cardOrder.getAddressId(), true);
        smsService.sendCode(userAddress.getPhone(), SMSTemplateEnum.ORDER_SHIPPING_Multi_PARAM,  request, productServiceInfo.getStoreName(), "tempMobile");
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String addOrder(GiftCardOrderDto dto, HttpServletRequest request) {
        Integer userId = tokenComponent.getUserId();
        dto.setUserId(userId);
        String id = this.add(dto);

        String phone = systemConfigService.getValueByKey(Constants.SMS_EMPLOYEE_NUMBER);

        //短信通知
        smsService.sendCode(phone, SMSTemplateEnum.ORDER_SUCCESSFUL_2_EMPLOYEE, request, dto.getProductName());

        User info = userService.getInfo();

        smsService.sendCode(info.getPhone(), SMSTemplateEnum.ORDER_SUCCESSFUL_2_CUSTOMER, request, dto.getProductName());
        return id;
    }

    @Override
    protected void onBeforePage(GiftCardOrderQuery query, QueryWrapper<GiftCardOrder> queryWrapper) {
        if (!query.getStatus().equals("all")) {
            switch (query.getStatus()) {
                case "unshipped":
                    query.setOrderStatus(0);
                    break;
                case "delivering":
                    query.setOrderStatus(1);
                    break;
                case "delivered":
                    query.setOrderStatus(2);
                    break;
                case "completed":
                    query.setOrderStatus(3);
                    break;
            }
        }
        if (StringUtils.isNotBlank(query.getDateLimit())) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(query.getDateLimit());
            query.setStartTime(dateLimit.getStartTime());
            query.setEndTime(dateLimit.getEndTime());
        }

        if (Objects.nonNull(query.getStartTime()) && Objects.nonNull(query.getEndTime()))
            queryWrapper.between("create_time", query.getStartTime(), query.getEndTime());

        super.onBeforePage(query, queryWrapper);
    }
}


