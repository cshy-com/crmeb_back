package com.cshy.common.model.request.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 秒杀搜索参数

 */
@Data
public class StoreSeckillSearchRequest {

    @ApiModelProperty(value = "搜索关键字 商品id或者名称")
    private String keywords;

    @ApiModelProperty(value = "秒杀时段")
    private Integer timeId;

    @ApiModelProperty(value = "是否显示关键字 0/1")
    private Integer status;

    @ApiModelProperty(value = "0 活动 1 秒杀")
    private Integer type;
}

