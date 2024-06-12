package com.cshy.common.model.vo.wechat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WechatShippingOrderKeyDto {
    @ApiModelProperty(value = "订单单号类型，用于确认需要上传详情的订单。枚举值1，使用下单商户号和商户侧单号；枚举值2，使用微信支付单号。")
    Integer order_number_type;
    @ApiModelProperty(value = "支付下单商户的商户号")
    String mchid;
    @ApiModelProperty(value = "商户系统内部订单号")
    String out_trade_no;
    @ApiModelProperty(value = "原支付交易对应的微信订单号")
    String transaction_id;
}
