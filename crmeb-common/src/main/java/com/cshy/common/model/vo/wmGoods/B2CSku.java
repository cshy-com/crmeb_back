package com.cshy.common.model.vo.wmGoods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class B2CSku {
    @ApiModelProperty(value = "重量")
    private int weight;

    @ApiModelProperty(value = "体积")
    private int volume;

}
