package com.cshy.common.model.entity.giftCard;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cshy.common.model.entity.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("b_gift_card_type")
@ApiModel(value="GiftCardType对象", description="礼品卡类型表")
public class GiftCardType extends BaseModel<GiftCardType> {
    @ApiModelProperty(value = "礼品卡类型名称")
    private String name;

    @ApiModelProperty(value = "banner")
    private String banner;

    @ApiModelProperty(value = "卡状态(0 停用 1 启用)")
    private Integer status;
}
