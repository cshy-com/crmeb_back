package com.cshy.admin.controller.wechat;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cshy.common.constants.PayType;
import com.cshy.common.constants.StoreOrderStatusConstants;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.entity.order.StoreOrder;
import com.cshy.common.model.entity.user.UserToken;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.response.WeChatJsSdkConfigResponse;
import com.cshy.common.model.vo.wechat.WechatShippingListDto;
import com.cshy.common.model.vo.wechat.WechatShippingOrderKeyDto;
import com.cshy.common.model.vo.wechat.WechatUploadShippingInfoDto;
import com.cshy.common.utils.DateUtil;
import com.cshy.service.service.store.StoreOrderService;
import com.cshy.service.service.user.UserService;
import com.cshy.service.service.user.UserTokenService;
import com.cshy.service.service.wechat.WechatCommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信 -- 开放平台 admin
 */
@Slf4j
@RestController("WeChatAdminController")
@RequestMapping("api/admin/wechat")
@Api(tags = "微信 -- 开放平台 admin")
public class WeChatAdminController {

    @Autowired
    private WechatCommonService wechatCommonService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private UserTokenService userTokenService;

    /**
     * 获取微信公众号js配置
     */
    @PreAuthorize("hasAuthority('admin:wechat:config')")
    @ApiOperation(value = "获取微信公众号js配置")
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    @ApiImplicitParam(name = "url", value = "页面地址url")
    public CommonResult<WeChatJsSdkConfigResponse> configJs(@RequestParam(value = "url") String url) {
        return CommonResult.success(wechatCommonService.getJsSdkConfig(url));
    }

    @PreAuthorize("hasRole('admin')")
    @ApiOperation(value = "获取微信公众号accessToken")
    @RequestMapping(value = "/accessToken", method = RequestMethod.GET)
    public CommonResult<String> accessToken() {
        String publicAccessToken = wechatCommonService.getPublicAccessToken();
        return CommonResult.success(publicAccessToken);
    }

    @ApiOperation(value = "test")
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public CommonResult<Boolean> uploadShippingInfo() {
        List<StoreOrder> storeOrderList = storeOrderService.list(new LambdaQueryWrapper<StoreOrder>().gt(StoreOrder::getStatus, 1).lt(StoreOrder::getStatus, 4).eq(StoreOrder::getPayType, PayType.PAY_TYPE_WE_CHAT).eq(StoreOrder::getRefundStatus, 0).like(StoreOrder::getOrderId, "order"));
        System.out.println(storeOrderList);
        storeOrderList.forEach(storeOrder -> {
//            WechatUploadShippingInfoDto vo = new WechatUploadShippingInfoDto();
//            WechatShippingOrderKeyDto order_key = new WechatShippingOrderKeyDto();
//            order_key.setMchid("1646449278");
//            order_key.setOrder_number_type(1);    // 必填
//            order_key.setOut_trade_no(storeOrder.getOutTradeNo());
//            vo.setOrder_key(order_key);    // 必填
//
//            List<WechatShippingListDto> list = new ArrayList<>();
//            WechatShippingListDto shipping = new WechatShippingListDto();
//
//            Map<String, Object> contact = new HashMap<>();
//            contact.put("receiver_contact", storeOrder.getUserMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
//
//            shipping.setContact(contact);
//
//            if (storeOrder.getDeliveryType().equals(StoreOrderStatusConstants.ORDER_LOG_EXPRESS) && storeOrder.getTrackingNo().equals("zp666")){
//                shipping.setExpress_company("null");
//                shipping.setItem_desc("系统消息，已收到货请忽略");    // 必填
//                shipping.setTracking_no("无");
//                vo.setLogistics_type(2);    // 必填
//            }
//            else if (storeOrder.getDeliveryType().equals(StoreOrderStatusConstants.ORDER_LOG_EXPRESS)) {
//                shipping.setExpress_company("null");
//                shipping.setItem_desc("系统消息，已收到货请忽略");    // 必填
//                shipping.setTracking_no("无");
//                vo.setLogistics_type(1);
//            } else if (storeOrder.getDeliveryType().equals(StoreOrderStatusConstants.ORDER_LOG_PICKUP)) {
//                shipping.setExpress_company("null");
//                shipping.setItem_desc("系统消息，已收到货请忽略");    // 必填
//                shipping.setTracking_no("无");
//                vo.setLogistics_type(4);
//            }
//            list.add(shipping);
//
//            Map<String, Object> payer = new HashMap<>();
//            UserToken tokenByUserId = userTokenService.getTokenByUserId(storeOrder.getUid(), 2);
//
//            payer.put("openid", tokenByUserId.getToken());
//            vo.setPayer(payer);    // 必填
//            vo.setDelivery_mode(1);        // 必填
//            vo.setIs_all_delivered(true);    // 分拆发货模式时必填
//            vo.setShipping_list(list);    // 必填
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
//            vo.setUpload_time(simpleDateFormat.format(DateUtil.nowDateTime()));
//            vo.setPayer(payer);    // 必填
//            try {
//                wechatCommonService.uploadShippingInfo(vo);
//            } catch (Exception e) {
//                if (!e.getMessage().contains("处于不可发货的状态") && !e.getMessage().contains("该笔支付单不属于"))
//                    throw new CrmebException(vo.getOrder_key().getOut_trade_no() + "=========================" + e.getMessage());
//            }
        });
        return CommonResult.success();
    }
}
