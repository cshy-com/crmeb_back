package com.cshy.common.model.request.store;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品属性值表
 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreProductAttrValueUpdateRequest", description="商品规格属性修改对象")
public class AttrValueUpdateRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "库存增加/减少", required = true)
    private Integer num;

    @ApiModelProperty(value = "规格属性金额", required = true)
    @NotNull(message = "规格属性金额不能为空")
    @DecimalMin(value = "0", message = "金额不能小于0")
    private BigDecimal price;

    @ApiModelProperty(value = "成本价", required = true)
    @NotNull(message = "规格属性成本价不能为空")
    @DecimalMin(value = "0", message = "成本价不能小于0")
    private BigDecimal cost;

    @ApiModelProperty(value = "原价", required = true)
    @NotNull(message = "规格属性原价不能为空")
    @DecimalMin(value = "0", message = "原价不能小于0")
    private BigDecimal otPrice;

}
