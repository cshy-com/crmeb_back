package com.cshy.common.model.entity.category;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cshy.common.model.entity.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("s_activity_category")
@ApiModel(value="ActivityCategory对象", description="活动名称分类表")
public class ActivityCategory extends BaseModel<ActivityCategory> {
    @ApiModelProperty(value = "名称")
    private String catName;

    @ApiModelProperty(value = "是否启用")
    private Boolean isEnabled;
}
