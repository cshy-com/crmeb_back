package com.cshy.common.model.dto.activity;

import com.cshy.common.model.entity.activity.Activity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@ApiModel("活动 - Dto")
public class ActivityDto extends Activity {
}
