package com.cshy.service.impl.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cshy.common.constants.Constants;
import com.cshy.common.constants.DateConstants;
import com.cshy.common.constants.IntegralRecordConstants;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.Coupon;
import com.cshy.common.model.NoModelWriteData;
import com.cshy.common.model.dto.coupon.StoreIntegralCouponDto;
import com.cshy.common.model.dto.coupon.StoreIntegralCouponListDto;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.entity.coupon.StoreIntegralCoupon;
import com.cshy.common.model.entity.user.User;
import com.cshy.common.model.entity.user.UserIntegralRecord;
import com.cshy.common.model.query.coupon.StoreIntegralCouponQuery;
import com.cshy.common.model.vo.coupon.StoreIntegralCouponVo;
import com.cshy.common.utils.EasyExcelUtils;
import com.cshy.common.utils.StringUtils;
import com.cshy.service.dao.coupon.StoreIntegralCouponDao;
import com.cshy.service.service.store.StoreIntegralCouponService;
import com.cshy.service.service.user.UserIntegralRecordService;
import com.cshy.service.service.user.UserService;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class StoreIntegralCouponServiceImpl extends BaseServiceImpl<StoreIntegralCoupon, StoreIntegralCouponDto,
        StoreIntegralCouponQuery, StoreIntegralCouponVo, StoreIntegralCouponDao> implements StoreIntegralCouponService {
    private final String integralUrl = "https://cshy.store/receive?code=CODE";

    @Autowired
    private UserService userService;

    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    protected void onBeforeAddOrUpdate(StoreIntegralCouponDto dto) {
        super.onBeforeAddOrUpdate(dto);

        // 失效日期校验
        long estimatedTime = DateUtil.parse(dto.getExpireTime()).getTime();
        long time = DateUtil.date().getTime();
        Assert.isTrue(estimatedTime >= time, "失效时间必须大于当前时间");

        try {
            LocalDate date = LocalDate.parse(dto.getExpireTime());
            LocalDateTime dateTime = date.atStartOfDay();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String format = dateTime.format(formatter);
            dto.setExpireTime(format);
        } catch (DateTimeParseException e) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(dto.getExpireTime(), formatter);
        } catch (Exception e) {
            throw new CrmebException("时间解析失败");
        }
    }

    @Override
    protected void onBeforePage(StoreIntegralCouponQuery query, QueryWrapper<StoreIntegralCoupon> queryWrapper) {
        super.onBeforePage(query, queryWrapper);
        if (StringUtils.isNotBlank(query.getExpireTime())){
            try {
                LocalDate date = LocalDate.parse(query.getExpireTime());
                LocalDateTime dateTime = date.atStartOfDay();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String format = dateTime.format(formatter);
                query.setExpireTime(format);
            } catch (DateTimeParseException e) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(query.getExpireTime(), formatter);
            } catch (Exception e) {
                throw new CrmebException("时间解析失败");
            }
        }

    }

    @Override
    protected void onBeforeAdd(StoreIntegralCouponDto dto) {
        super.onBeforeAdd(dto);

        String code = IdUtil.simpleUUID();

        dto.setCouponCode(code);
        dto.setIsUsed(Boolean.FALSE);
        dto.setIsExported(Boolean.FALSE);
    }

    @Override
    protected void onAfterAdd(StoreIntegralCouponDto dto) {
        super.onAfterAdd(dto);

        // 生成二维码
        QrConfig qrConfig = new QrConfig(300, 300);
        String base64 = QrCodeUtil.generateAsBase64(integralUrl.replace("CODE", dto.getCouponCode()), qrConfig, ImgUtil.IMAGE_TYPE_PNG);
        dto.setQrCode(base64);

        this.update(dto);
    }

    /**
     * 批量新增
     */
    @Override
    public void addList(StoreIntegralCouponListDto dto) {
        onBeforeAddOrUpdate(Convert.convert(StoreIntegralCouponDto.class, dto));

        // 数量校验
        int sum = dto.getCouponList().stream().mapToInt(Coupon::getNumber).sum();
        Assert.isTrue(sum <= 10000, "批量生成最多每次一万条");

        dto.getCouponList().forEach(obj -> IntStream.range(0, obj.getNumber()).forEach(i -> {
            StoreIntegralCouponDto couponDto = new StoreIntegralCouponDto();
            couponDto.setExpireTime(dto.getExpireTime());
            couponDto.setCouponCode(IdUtil.simpleUUID());
            couponDto.setIntegral(obj.getIntegral());
            couponDto.setIsUsed(Boolean.FALSE);
            couponDto.setCreateTime(DateUtil.now());
            couponDto.setIsExported(Boolean.FALSE);
            this.add(couponDto);


            // 生成二维码
            QrConfig qrConfig = new QrConfig(300, 300);
            String base64 = QrCodeUtil.generateAsBase64(integralUrl.replace("CODE", couponDto.getCouponCode()).replace("ID", couponDto.getId()), qrConfig, ImgUtil.IMAGE_TYPE_PNG);
            couponDto.setQrCode(base64);
            this.update(couponDto);
        }));
    }

    /**
     * 批量导出
     */
    @Override
    public void export(StoreIntegralCouponQuery query, HttpServletResponse response) throws Exception {
        exp(query, response);
    }

    @Override
    public void addIntegral(String code, Integer userId) {
        //查询券
        StoreIntegralCoupon coupon = getByCode(code);
        User user = userService.getById(userId);
        Assert.notNull(user, "用户不存在");
        Assert.notNull(coupon, "积分券不存在");
        Assert.isFalse(coupon.getIsUsed(), "积分券已被使用");

        // 失效日期校验
        long estimatedTime = DateUtil.parse(coupon.getExpireTime()).getTime();
        long time = DateUtil.date().getTime();
        Assert.isTrue(estimatedTime >= time, "积分券已失效");

        if (!coupon.getIntegral().equals(0)) {
            if ((user.getIntegral() + coupon.getIntegral()) > 99999999) {
                throw new CrmebException("积分添加后不能大于99999999");
            }
        }

        Boolean execute = transactionTemplate.execute(e -> {
            // 处理积分
            if (coupon.getIntegral() > 0) {
                // 生成记录
                UserIntegralRecord integralRecord = new UserIntegralRecord();
                integralRecord.setUid(user.getUid());
                integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_COUPON);
                integralRecord.setTitle(IntegralRecordConstants.BROKERAGE_RECORD_TITLE_COUPON);
                integralRecord.setIntegral(coupon.getIntegral());
                integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
                integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
                integralRecord.setBalance(user.getIntegral() + coupon.getIntegral());
                integralRecord.setMark(StrUtil.format("积分券获取了{}积分", coupon.getIntegral()));

                userService.operationIntegral(user.getUid(), coupon.getIntegral(), user.getIntegral(), "add");
                userIntegralRecordService.save(integralRecord);
            }

            //更新券状态
            coupon.setIsUsed(Boolean.TRUE);
            coupon.setUserId(userId);
            this.updateById(coupon);
            return Boolean.TRUE;
        });

        if (!execute) {
            throw new CrmebException("修改积分/余额失败");
        }
    }

    @Override
    public Boolean isUsed(String code) {
        return getByCode(code).getIsUsed();
    }

    private StoreIntegralCoupon getByCode(String code) {
        return this.getOne(new LambdaQueryWrapper<StoreIntegralCoupon>().eq(StoreIntegralCoupon::getCouponCode, code));
    }

    private void exp(StoreIntegralCouponQuery query, HttpServletResponse response) throws IOException {
        //判断使用id导出还是 条件下全量导出
        List<StoreIntegralCoupon> storeIntegralCouponList = Lists.newArrayList();
        //查询未使用数据
        query.setIsUsed(Boolean.FALSE);

        if (CollUtil.isNotEmpty(query.getIdList())) {
            storeIntegralCouponList = this.list(new LambdaQueryWrapper<StoreIntegralCoupon>().in(StoreIntegralCoupon::getId, query.getIdList()).eq(StoreIntegralCoupon::getIsUsed, Boolean.FALSE));
        } else {
            List<StoreIntegralCouponVo> storeIntegralCouponVoList = this.list(query);
            storeIntegralCouponList.addAll(storeIntegralCouponVoList);
        }

        List<Boolean> booleanList = storeIntegralCouponList.stream().map(StoreIntegralCoupon::getIsUsed).collect(Collectors.toList());
        if (booleanList.contains(Boolean.TRUE))
            throw new CrmebException("不能选择已导出/已使用的数据，请重新选择");

        storeIntegralCouponList.forEach(coupon -> {
            StoreIntegralCouponDto dto = new StoreIntegralCouponDto();
            BeanUtils.copyProperties(coupon, dto);
            dto.setIsExported(Boolean.TRUE);
            this.update(dto);

            byte[] qrCode = Base64.getDecoder().decode(coupon.getQrCode().substring("data:image/png;base64,".length()));
            coupon.setQrcodeByte(qrCode);
        });

        EasyExcelUtils<StoreIntegralCoupon> easyExcelUtils = new EasyExcelUtils<StoreIntegralCoupon>();
        NoModelWriteData noModelWriteData = easyExcelUtils.buildData(storeIntegralCouponList, StoreIntegralCoupon.class, ".xlsx");
        easyExcelUtils.noModelWrite(noModelWriteData, response);
    }
}
