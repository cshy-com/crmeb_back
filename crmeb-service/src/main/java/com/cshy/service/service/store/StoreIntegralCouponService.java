package com.cshy.service.service.store;


import com.cshy.common.model.dto.coupon.StoreIntegralCouponDto;
import com.cshy.common.model.dto.coupon.StoreIntegralCouponListDto;
import com.cshy.common.model.entity.base.BaseService;
import com.cshy.common.model.entity.coupon.StoreIntegralCoupon;
import com.cshy.common.model.query.coupon.StoreIntegralCouponQuery;
import com.cshy.common.model.vo.coupon.StoreIntegralCouponVo;

import javax.servlet.http.HttpServletResponse;

public interface StoreIntegralCouponService extends BaseService<StoreIntegralCoupon, StoreIntegralCouponDto, StoreIntegralCouponQuery, StoreIntegralCouponVo> {
    /**
     * 批量新增
     */
    void addList(StoreIntegralCouponListDto dto);


    /**
     * 批量导出
     */
    void export(StoreIntegralCouponQuery query, HttpServletResponse response) throws Exception;

    void addIntegral(String code, Integer userId);

    Boolean isUsed(String code);
}
