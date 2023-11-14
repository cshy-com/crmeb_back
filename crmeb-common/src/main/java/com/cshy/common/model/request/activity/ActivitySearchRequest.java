package com.cshy.common.model.request.activity;

import com.cshy.common.model.entity.activity.Activity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 后台积分查询请求对象

 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ActivitySearchRequest对象", description="后台活动配置查询请求对象")
public class ActivitySearchRequest extends Activity implements Serializable {


}
