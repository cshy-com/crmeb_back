package com.cshy.common.model.vo.wechat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class WechatUploadShippingInfoDto {
    @ApiModelProperty(value = "物流模式，发货方式枚举值：1、实体物流配送采用快递公司进行实体物流配送形式 2、同城配送 3、虚拟商品，虚拟商品，例如话费充值，点卡等，无实体配送形式 4、用户自提")
    public Integer delivery_mode;

    @ApiModelProperty(value = "发货模式，发货模式枚举值：1、UNIFIED_DELIVERY（统一发货）2、SPLIT_DELIVERY（分拆发货） 示例值: UNIFIED_DELIVERY")
    public Integer logistics_type;

    @ApiModelProperty(value = "订单")
    public WechatShippingOrderKeyDto order_key;

    @ApiModelProperty(value = "标识分拆发货模式下是否已全部发货完成")
    public Boolean is_all_delivered;

    @ApiModelProperty(value = "标识分拆发货模式下是否已全部发货完成")
    public List<WechatShippingListDto> shipping_list;

    @ApiModelProperty(value = "上传时间")
    public String upload_time;

    @ApiModelProperty(value = "支付者 ， 放openId")
    public Map<String, Object> payer;
}
