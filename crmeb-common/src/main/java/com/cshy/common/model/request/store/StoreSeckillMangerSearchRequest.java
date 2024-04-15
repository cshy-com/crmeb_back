package com.cshy.common.model.request.store;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品秒杀配置

 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("StoreSeckillMangerSearchRequest")
@ApiModel(value="StoreSeckillMangerSearchRequest", description="商品秒杀配置")
public class StoreSeckillMangerSearchRequest {

    @ApiModelProperty(value = "秒杀名称")
    private String name;

    @ApiModelProperty(value = "活动分类名称")
    private String activityCategoryName;

    @ApiModelProperty(value = "状态 0=关闭 1=开启")
    private Integer status;

    @ApiModelProperty(value = "0 活动 1 秒杀")
    private Integer type;
}
