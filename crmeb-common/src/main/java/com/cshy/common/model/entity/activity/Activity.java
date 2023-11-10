package com.cshy.common.model.entity.activity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cshy.common.model.entity.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@TableName("b_activity")
@ApiModel(value="Activity对象", description="活动配置表")
public class Activity extends BaseModel<Activity> {
    @ApiModelProperty(value = "活动名称")
    private String name;

    @ApiModelProperty(value = "主图片")
    private String mainPicture;

    @ApiModelProperty(value = "跳转链接")
    private String url;

    @ApiModelProperty(value = "活动类型： 0.导航下方活动 1.中部活动 2.顶部banner活动 3.普通活动")
    private Integer type;

    @ApiModelProperty(value = "状态（0：关闭，1：开启）")
    private Integer status;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;
}
