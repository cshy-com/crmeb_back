package com.cshy.common.model.dto.activity;

import com.cshy.common.model.entity.activity.Activity;
import com.cshy.common.model.entity.activity.ActivityProduct;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("活动商品 - Dto")
public class ActivityProductDto extends ActivityProduct {
}
