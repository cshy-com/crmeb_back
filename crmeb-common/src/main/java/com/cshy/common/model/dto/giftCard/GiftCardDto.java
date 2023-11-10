package com.cshy.common.model.dto.giftCard;

import com.cshy.common.model.entity.activity.Activity;
import com.cshy.common.model.entity.giftCard.GiftCard;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("礼品卡券 - Dto")
public class GiftCardDto extends GiftCard {
    @ApiModelProperty(value = "生成张数")
    private Integer generateNumber;
}
