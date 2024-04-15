package com.cshy.common.model.entity.product;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 商品规则值（规格）表
 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_product_rule")
@ApiModel(value="StoreProductRule对象", description="商品规则值(规格)表")
public class StoreProductRule implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "规格名称")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String ruleName;

    @ApiModelProperty(value = "规格值")
    private String ruleValue;


}
