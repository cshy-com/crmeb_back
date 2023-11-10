package com.cshy.common.model.vo.giftCard;

import com.alibaba.excel.annotation.ExcelProperty;
import com.cshy.common.model.entity.activity.Activity;
import com.cshy.common.model.entity.giftCard.GiftCard;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("礼品卡券 - Vo")
public class GiftCardVo extends GiftCard {
    @ApiModelProperty(value = "卡类型名称")
    private String giftCardTypeName;
}
