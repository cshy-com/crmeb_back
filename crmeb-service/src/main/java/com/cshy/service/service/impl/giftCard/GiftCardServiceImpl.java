package com.cshy.service.service.impl.giftCard;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cshy.common.constants.Constants;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.NoModelWriteData;
import com.cshy.common.model.dto.giftCard.GiftCardDto;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.entity.giftCard.GiftCard;
import com.cshy.common.model.entity.giftCard.GiftCardOrder;
import com.cshy.common.model.entity.giftCard.GiftCardProduct;
import com.cshy.common.model.entity.giftCard.GiftCardType;
import com.cshy.common.model.entity.product.StoreProduct;
import com.cshy.common.model.entity.product.StoreProductAttrValue;
import com.cshy.common.model.entity.user.UserAddress;
import com.cshy.common.model.query.giftCard.GiftCardQuery;
import com.cshy.common.model.vo.giftCard.GiftCardVo;
import com.cshy.common.utils.*;
import com.cshy.service.dao.giftCard.GiftCardDao;
import com.cshy.service.service.StoreProductAttrValueService;
import com.cshy.service.service.StoreProductService;
import com.cshy.service.service.UserAddressService;
import com.cshy.service.service.giftCard.GiftCardOrderService;
import com.cshy.service.service.giftCard.GiftCardProductService;
import com.cshy.service.service.giftCard.GiftCardService;
import com.cshy.service.service.giftCard.GiftCardTypeService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GiftCardServiceImpl extends BaseServiceImpl<GiftCard, GiftCardDto,
        GiftCardQuery, GiftCardVo, GiftCardDao> implements GiftCardService {
    private static final Logger logger = LoggerFactory.getLogger(GiftCardServiceImpl.class);

    @Value("${encryptKey.pickupSecret}")
    private String pickupSecretKey;

    @Value("${url.giftCardUrl}")
    private String giftCardUrl;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private GiftCardTypeService giftCardTypeService;

    @Autowired
    private GiftCardProductService giftCardProductService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private GiftCardOrderService giftCardOrderService;

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private GiftCardDao giftCardDao;

    @Override
    public void batchAdd(GiftCardDto dto) {
        //获取需要生成的数量
        Integer generateNumber = dto.getGenerateNumber();
        for (int i = 0; i < generateNumber; i++) {
            GiftCardDto giftCardDto = new GiftCardDto();
            BeanUtils.copyProperties(dto, giftCardDto);
            this.add(giftCardDto);
        }
    }

    @Override
    public String viewSecret(String id) {
        GiftCard giftCard = this.getById(id);
        String pickupSecret = giftCard.getPickupSecret();
        String decryptPassword = CrmebUtil.decryptPassowrd(pickupSecret, this.pickupSecretKey);
        return decryptPassword;
    }

    @Override
    public void export(GiftCardQuery query, HttpServletResponse response) throws IOException {
        //判断使用id导出还是 条件下全量导出
        List<GiftCard> giftCardList = Lists.newArrayList();
        if (CollUtil.isNotEmpty(query.getIdList())) {
            giftCardList = this.list(new LambdaQueryWrapper<GiftCard>().in(GiftCard::getId, query.getIdList()));
        } else {
            List<GiftCardVo> giftCardVoList = this.list(query);
            List<GiftCard> finalGiftCardList = giftCardList;
            giftCardVoList.forEach(giftCardVo -> {
                GiftCard giftCard = giftCardVo;
                finalGiftCardList.add(giftCard);
            });
            giftCardList = finalGiftCardList;
        }

        List<Integer> collect = giftCardList.stream().map(GiftCard::getUsingStatus).collect(Collectors.toList());
        if (collect.contains(1) || collect.contains(2))
            throw new CrmebException("不能选择已导出/已使用的数据，请重新选择");

        giftCardList.forEach(giftCard -> {
            GiftCardDto card = new GiftCardDto();
            BeanUtils.copyProperties(giftCard, card);
            card.setUsingStatus(1);
            this.update(card);
        });

        giftCardList.forEach(giftCard -> {
            byte[] qrCode = QrCodeUtil.generatePng(giftCardUrl.replace("PICKUPCODE", giftCard.getPickupCode()),
                    QrConfig.create().setCharset(Charset.forName("GBK")));
            giftCard.setQrcodeByte(qrCode);
            giftCard.setPickupSecret(CrmebUtil.decryptPassowrd(giftCard.getPickupSecret(), this.pickupSecretKey));
        });

        EasyExcelUtils<GiftCard> easyExcelUtils = new EasyExcelUtils<GiftCard>();
        NoModelWriteData noModelWriteData = easyExcelUtils.buildData(giftCardList, GiftCard.class, ".xlsx");
        easyExcelUtils.noModelWrite(noModelWriteData, response);
    }

    @Override
    public Map<String, Object> getInfoByPickupCode(String pickupCode) {
        Map<String, Object> res = new HashMap<>();
        GiftCard giftCard = this.getOne(new LambdaQueryWrapper<GiftCard>().eq(GiftCard::getPickupCode, pickupCode));
        if (Objects.isNull(giftCard))
            throw new CrmebException("未查询到礼品卡，请联系客服");

        GiftCardType giftCardType = giftCardTypeService.getById(giftCard.getGiftCardTypeId());

        if (Objects.isNull(giftCardType))
            throw new CrmebException("未查询到礼品卡类型，请联系客服");

        //卡编码 活动名称 使用状态
        Integer usingStatus = giftCard.getUsingStatus();

        res.put("pickupCode", pickupCode);
        res.put("giftCardType", giftCardType.getName());
        res.put("usingStatus", usingStatus);

        if (usingStatus != 2) {
            //未使用查询 对应的商品列表
            List<GiftCardProduct> list = giftCardProductService.list(new LambdaQueryWrapper<GiftCardProduct>()
                    .eq(GiftCardProduct::getGiftCardTypeId, giftCard.getGiftCardTypeId()));
            List<Integer> productIdList = list.stream().map(GiftCardProduct::getProductId).collect(Collectors.toList());
            if (CollUtil.isEmpty(productIdList))
                throw new CrmebException("该礼品卡类型下暂未添加商品，请联系客服");

            List<StoreProduct> storeProductList = storeProductService.list(new LambdaQueryWrapper<StoreProduct>().in(StoreProduct::getId, productIdList).eq(StoreProduct::getIsShow, 1));

            List<Map<String, Object>> productMap = storeProductList.stream().map(storeProduct -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", storeProduct.getId());
                map.put("image", storeProduct.getImage());
                map.put("name", storeProduct.getStoreName());
                return map;
            }).collect(Collectors.toList());

            res.put("products", productMap);
        } else {
            //已使用 查询订单信息 包含 已兑换的商品图片 商品名称 订单备注 订单物流 订单编号 下单时间 收货人信息
            GiftCardOrder giftCardOrder = giftCardOrderService.getOne(new LambdaQueryWrapper<GiftCardOrder>().eq(GiftCardOrder::getGiftCardId, giftCard.getId()));

            if (Objects.isNull(giftCardOrder))
                throw new CrmebException("未查询到相关订单，请联系客服");

            //商品
            Integer productId = giftCardOrder.getProductId();
            StoreProduct product = storeProductService.getH5Detail(productId, true);

            //商品规格
            StoreProductAttrValue attrValue = storeProductAttrValueService.getById(giftCardOrder.getAttrValueId());
            if (Objects.isNull(attrValue))
                throw new CrmebException("商品规格有误，请联系管理员");

            //地址信息
            Integer addressId = giftCardOrder.getAddressId();
            UserAddress userAddress = userAddressService.getById(addressId, true);

            //订单信息
            res.put("orderDetails", new HashMap<String, Object>() {{
                put("id", giftCardOrder.getGiftCardId());
                put("remark", giftCardOrder.getRemark());
                put("trackingNo", giftCardOrder.getTrackingNo());
                put("orderNo", giftCardOrder.getOrderNo());
                put("createTime", giftCardOrder.getCreateTime());
                put("addressinfo", userAddress);
                put("attrValue", attrValue);
            }});
            //商品信息
            res.put("product", new HashMap<String, Object>() {{
                put("name", product.getStoreName());
                put("image", product.getImage());
                put("id", product.getId());
            }});
        }

        return res;
    }

    @Override
    public boolean checkSecret(String code, String secret) {
        GiftCard giftCard = this.getOne(new LambdaQueryWrapper<GiftCard>().eq(GiftCard::getPickupCode, code));
        if (Objects.isNull(giftCard))
            throw new CrmebException("礼品卡不存在，请联系客服");

        if (giftCard.getUsingStatus() != 1)
            throw new CrmebException("礼品卡状态异常，请联系客服");

        if (giftCard.getCardStatus() != 0)
            throw new CrmebException("礼品卡未生效，请联系客服");

        String effectiveTime = DateUtil.dateToStr(giftCard.getEffectiveTime(), Constants.DATE_FORMAT_START);
        String now = DateUtil.nowDate(Constants.DATE_FORMAT_START);

        Date effectiveDate = DateUtil.strToDate(effectiveTime, Constants.DATE_FORMAT_START);
        Date nowDate = DateUtil.strToDate(now, Constants.DATE_FORMAT_START);
        if (effectiveDate.after(nowDate))
            throw new CrmebException("该礼品卡未到可使用时间，生效时间为：" + now);
        String encryptPassword = CrmebUtil.encryptPassword(secret, this.pickupSecretKey);
        if (encryptPassword.equals(giftCard.getPickupSecret()))
            return true;
        return false;
    }

    @Override
    public void syncStatus() {
        List<GiftCard> list = this.list();
        list.stream().forEach(giftCard -> {
            if (Objects.nonNull(giftCard.getEffectiveTime())) {
                String effectiveTime = DateUtil.dateToStr(giftCard.getEffectiveTime(), Constants.DATE_FORMAT_START);
                String now = DateUtil.nowDate(Constants.DATE_FORMAT_START);

                Date effectiveDate = DateUtil.strToDate(effectiveTime, Constants.DATE_FORMAT_START);
                Date nowDate = DateUtil.strToDate(now, Constants.DATE_FORMAT_START);
                if (effectiveDate.equals(nowDate) || effectiveDate.before(nowDate))
                    giftCard.setCardStatus(0);
                if (effectiveDate.after(nowDate))
                    giftCard.setCardStatus(1);

                this.save(giftCard);
            }
        });
    }

    @Override
    public GiftCard getById(String id, Boolean isDel) {
        GiftCard giftCard = giftCardDao.getById(id, isDel);
        return giftCard;
    }

    @Override
    public String updateBatch(Map<String, Object> params) {
        String giftCardTypeId = (String) params.get("giftCardTypeId");
        String serialNoListStr = (String) params.get("serialNoList");
        serialNoListStr = serialNoListStr.trim();
        String[] snArr = serialNoListStr.split("\n");
        List<String> errorSn = Lists.newArrayList();
        Arrays.asList(snArr).forEach(sn -> {
            GiftCard giftCard = this.getOne(new LambdaQueryWrapper<GiftCard>().eq(GiftCard::getSerialNo, sn));
            if (Objects.nonNull(giftCard)){
                giftCard.setGiftCardTypeId(giftCardTypeId);
                this.updateById(giftCard);
            }else {
                errorSn.add(sn);
            }
        });
        if (CollUtil.isNotEmpty(errorSn))
            return "执行失败序列号：" + String.join(",", errorSn);
        else
            return "";
    }

    @Override
    protected void onBeforeAdd(GiftCardDto dto) {
        //自动生成序列号
        int serialNo = this.generateSerialNo();
        dto.setSerialNo(String.format("%08d", serialNo));

        //自动生成提货编码
        String pickupCode = this.generatePickupCode();
        dto.setPickupCode(pickupCode);

        //自动生成提货密码
        //随机生成六位数字
        String randomNumber = generateRandomNumber();
        String encryptSecret = CrmebUtil.encryptPassword(randomNumber, this.pickupSecretKey);
        dto.setPickupSecret(encryptSecret);


        //设置默认值
        if (Objects.isNull(dto.getCardStatus()))
            dto.setCardStatus(0);
        if (Objects.isNull(dto.getUsingStatus()))
            dto.setUsingStatus(0);
        super.onBeforeAdd(dto);
    }

    @Override
    protected void onAfterPage(Page<GiftCardVo> page) {
        page.getRecords().forEach(giftCardVo -> {
            GiftCardType giftCardType = giftCardTypeService.getById(giftCardVo.getGiftCardTypeId());
            if (Objects.nonNull(giftCardType))
                giftCardVo.setGiftCardTypeName(giftCardType.getName());

            //生成二维码
            QrConfig qrConfig = new QrConfig(500, 500);
            qrConfig.setMargin(1);
            String base64 = QrCodeUtil.generateAsBase64(giftCardUrl.replace("PICKUPCODE", giftCardVo.getPickupCode()), qrConfig, ImgUtil.IMAGE_TYPE_PNG);
            giftCardVo.setQrcode(base64);
        });
        super.onAfterPage(page);
    }

    private int generateSerialNo() {
        int serialNo;
        if (!redisUtil.exists(Constants.GIFT_CARD_SERIAL_NUMBER)) {
            //从数据库中查询最大的序列号并设置redis
            List<GiftCard> list = this.list(new LambdaQueryWrapper<GiftCard>().orderByDesc(GiftCard::getSerialNo));
            if (CollUtil.isEmpty(list)) {
                redisUtil.set(Constants.GIFT_CARD_SERIAL_NUMBER, 1);
                serialNo = 1;
            } else {
                serialNo = Integer.valueOf(list.get(0).getSerialNo()) + 1;
                redisUtil.set(Constants.GIFT_CARD_SERIAL_NUMBER, serialNo);
            }
        } else {
            serialNo = (int) redisUtil.incr(Constants.GIFT_CARD_SERIAL_NUMBER, 1);
        }
        return serialNo;
    }

    private String generatePickupCode() {
        Random random = new Random();
        int randomNum = random.nextInt(900000000) + 100000000;
        String formatStr = String.format("%010d", randomNum);
        //检查与数据库的是否有重复
        List<GiftCard> list = this.list(new LambdaQueryWrapper<GiftCard>().eq(GiftCard::getPickupCode, formatStr));
        if (CollUtil.isNotEmpty(list))
            formatStr = this.generatePickupCode();
        return formatStr;
    }

    public static String generateRandomNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        return String.valueOf(randomNumber);
    }

}
