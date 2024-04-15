package com.cshy.common.model.vo.ydd;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class YddBrandVo {
    @ApiModelProperty(value = "品牌id")
    private Serializable id;

    @ApiModelProperty(value = "品牌名称")
    private String name;

    @ApiModelProperty(value = "品牌图标")
    private String icon;

    @ApiModelProperty(value = "状态")
    private int status;

    @ApiModelProperty(value = "类型")
    private int type;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private long createTime;

    @ApiModelProperty(value = "更新时间")
    private long updateTime;

    @ApiModelProperty(value = "应用id")
    private int appId;

    @ApiModelProperty(value = "级别")
    private int level;

    @ApiModelProperty(value = "商品数量")
    private int itemCount;
}
