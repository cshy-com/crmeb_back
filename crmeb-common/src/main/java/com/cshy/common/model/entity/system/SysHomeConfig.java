package com.cshy.common.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cshy.common.model.entity.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 功能管理表

 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_home_config")
@ApiModel(value="SysFeatureConfig对象", description="功能管理表")
public class SysHomeConfig extends BaseModel<SysHomeConfig> {
    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "类型分类")
    private String type;

    @ApiModelProperty(value = "跳转链接")
    private String link;

    @ApiModelProperty(value = "备用跳转链接")
    private String bakLink;

    @ApiModelProperty(value = "图片")
    private String img;

    @ApiModelProperty(value = "文字")
    private String text;

    @ApiModelProperty(value = "提示文字")
    private String infoText;

    @ApiModelProperty(value = "备用图片")
    private String bakImg;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "子级link")
    private String linkUrl;

    @ApiModelProperty(value = "提示")
    private String tip;

    @ApiModelProperty(value = "组件id")
    private String componentId;

    @ApiModelProperty(value = "父级id")
    private String parentId;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "是否启用 1 启用 0 未启用")
    private Boolean isEnabled;
}
