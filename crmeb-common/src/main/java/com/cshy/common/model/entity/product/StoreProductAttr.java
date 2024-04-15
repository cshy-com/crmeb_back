package com.cshy.common.model.entity.product;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 商品属性表

 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_product_attr")
@ApiModel(value="StoreProductAttr对象", description="商品属性表")
public class StoreProductAttr implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "attrId")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商品ID")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer productId;

    @ApiModelProperty(value = "属性名")
    private String attrName;

    @ApiModelProperty(value = "属性值")
    private String attrValues;

    @ApiModelProperty(value = "活动类型 0=商品，1=秒杀，2=砍价，3=拼团")
    private Integer type;

    @ApiModelProperty(value = "是否删除,0-否，1-是")
    private Boolean isDel;
}
