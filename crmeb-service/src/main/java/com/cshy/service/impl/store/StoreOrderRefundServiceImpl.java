package com.cshy.service.impl.store;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.constants.PayConstants;
import com.cshy.common.constants.RedisKey;
import com.cshy.common.model.request.store.StoreOrderRefundRequest;
import com.cshy.common.utils.RestTemplateUtil;
import com.cshy.common.utils.WxPayUtil;
import com.cshy.common.model.vo.wechat.WxRefundVo;
import com.cshy.common.model.entity.order.StoreOrder;
import com.cshy.service.dao.store.StoreOrderDao;
import com.cshy.service.service.store.StoreOrderRefundService;
import com.cshy.service.service.system.SystemConfigService;
import com.cshy.service.service.wechat.WechatCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * StoreOrderServiceImpl 接口实现

 */
@Service
public class StoreOrderRefundServiceImpl extends ServiceImpl<StoreOrderDao, StoreOrder> implements StoreOrderRefundService {

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private RestTemplateUtil restTemplateUtil;
    @Autowired
    private WechatCommonService wechatCommonService;

    @Override
    public void refund(StoreOrderRefundRequest request, StoreOrder storeOrder) {
        refundWx(request, storeOrder);
    }

    /**
     * 公共号退款
     * @param request
     * @param storeOrder
     */
    private void refundWx(StoreOrderRefundRequest request, StoreOrder storeOrder) {
        // 获取appid、mch_id
        // 微信签名key
        String appId = "";
        String mchId = "";
        String signKey = "";
        String path = "";
        if (storeOrder.getPaymentChannel() == 0) {// 公众号
            appId = systemConfigService.getValueByKeyException(RedisKey.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
            mchId = systemConfigService.getValueByKeyException(RedisKey.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(RedisKey.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
//            path = systemConfigService.getValueByKeyException("pay_routine_client_p12");
            path = systemConfigService.getValueByKeyException("pay_weixin_certificate_path");
        }
        if (storeOrder.getPaymentChannel() == 1) {// 小程序
            appId = systemConfigService.getValueByKeyException(RedisKey.CONFIG_KEY_PAY_ROUTINE_APP_ID);
            mchId = systemConfigService.getValueByKeyException(RedisKey.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(RedisKey.CONFIG_KEY_PAY_ROUTINE_APP_KEY);
//            path = systemConfigService.getValueByKeyException("pay_mini_client_p12");
            path = systemConfigService.getValueByKeyException("pay_routine_certificate_path");
        }
        if (storeOrder.getPaymentChannel() == 2) {// H5, 使用公众号的
            appId = systemConfigService.getValueByKeyException(RedisKey.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
            mchId = systemConfigService.getValueByKeyException(RedisKey.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(RedisKey.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
//            path = systemConfigService.getValueByKeyException("pay_mini_client_p12");
            path = systemConfigService.getValueByKeyException("pay_weixin_certificate_path");
        }

        String apiDomain = systemConfigService.getValueByKeyException(RedisKey.CONFIG_KEY_API_URL);

        //统一下单数据
        WxRefundVo wxRefundVo = new WxRefundVo();
        wxRefundVo.setAppid(appId);
        wxRefundVo.setMch_id(mchId);
        wxRefundVo.setNonce_str(WxPayUtil.getNonceStr());
        wxRefundVo.setOut_trade_no(storeOrder.getOutTradeNo());
        wxRefundVo.setOut_refund_no(storeOrder.getOrderId());
        wxRefundVo.setTotal_fee(storeOrder.getPayPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        wxRefundVo.setRefund_fee(request.getAmount().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        wxRefundVo.setNotify_url(apiDomain + PayConstants.WX_PAY_REFUND_NOTIFY_API_URI);
        String sign = WxPayUtil.getSign(wxRefundVo, signKey);
        wxRefundVo.setSign(sign);

        wechatCommonService.payRefund(wxRefundVo, path);
    }

}

