package com.cshy.common.model.vo.wmGoods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PointDeductRule {
    @ApiModelProperty(value = "是否开启积分抵扣")
    private int openPointDeduct;

    @ApiModelProperty(value = "使用条件")
    private String useCondition;

    @ApiModelProperty(value = "抵扣金额")
    private String deductAmount;

    @ApiModelProperty(value = "使用折扣")
    private String useDiscount;

    @ApiModelProperty(value = "抵扣折扣")
    private String deductDiscount;

}