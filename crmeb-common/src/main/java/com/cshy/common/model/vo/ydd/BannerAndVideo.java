package com.cshy.common.model.vo.ydd;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
public class BannerAndVideo {
    @ApiModelProperty(value = "ID")
    private int id;

    @ApiModelProperty(value = "创建时间")
    private long createTime;

    @ApiModelProperty(value = "更新时间")
    private long updateTime;

    @ApiModelProperty(value = "商品ID")
    private int productId;

    @ApiModelProperty(value = "类型")
    private int type;

    @ApiModelProperty(value = "URL")
    private String url;

    @ApiModelProperty(value = "封面")
    private String cover;

}