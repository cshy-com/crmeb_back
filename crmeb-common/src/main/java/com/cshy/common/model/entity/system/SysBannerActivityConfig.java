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
@TableName("sys_banner_activity_config")
@ApiModel(value="SysBannerActivityConfig对象", description="首页banner位活动配置表")
public class SysBannerActivityConfig extends BaseModel<SysBannerActivityConfig> {
    @ApiModelProperty(value = "主banner图片")
    private String mainBanner;

    @ApiModelProperty(value = "banner1图片")
    private String banner1;

    @ApiModelProperty(value = "banner2图片")
    private String banner2;

    @ApiModelProperty(value = "跳转链接1")
    private String url1;

    @ApiModelProperty(value = "跳转链接2")
    private String url2;

    @ApiModelProperty(value = "是否启用 0 启用 1 未启用")
    private Integer isEnabled;
}
