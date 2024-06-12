package com.cshy.common.model.vo.wechat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class WechatShippingListDto {
    @ApiModelProperty(value = "物流单号")
    public String tracking_no;

    @ApiModelProperty(value = "物流公司编码")
    public String express_company;

    @ApiModelProperty(value = "商品信息")
    public String item_desc;

    @ApiModelProperty(value = "收件人联系方式")
    public Map<String, Object> contact;
}
