package com.cshy.common.model.request.activity;

import com.cshy.common.model.entity.activity.Activity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ArticleRequest对象", description="文章管理表")
public class ActivityRequest extends Activity {
}
