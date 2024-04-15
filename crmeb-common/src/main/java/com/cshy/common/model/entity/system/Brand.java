package com.cshy.common.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cshy.common.model.entity.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_brand")
@ApiModel(value="Brand对象", description="品牌表")
public class Brand extends BaseModel<Brand> {
    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    @ApiModelProperty(value = "品牌图标")
    private String icon;

    @ApiModelProperty(value = "状态,1正常，0失效")
    private Boolean status;
}
