package com.cshy.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@ApiModel("积分券批量新增类")
public class Coupon {

    @NotNull(message = "积分为空")
    @ApiModelProperty(value = "积分", required = true)
    private BigDecimal integral;

    @NotNull(message = "新增数量为空")
    @ApiModelProperty(value = "新增数量", required = true)
    private Integer number;
}
