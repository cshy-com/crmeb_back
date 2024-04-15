package com.cshy.common.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cshy.common.model.entity.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("sys_template_column_config")
@ApiModel(value="SysTemplateColumnConfig对象", description="模板字段配置表")
public class SysTemplateColumnConfig extends BaseModel<SysTemplateColumnConfig> {
    @ApiModelProperty(value = "关联消息提醒id")
    private String notificationId;

    @ApiModelProperty(value = "关联消息模板id")
    private String tempId;

    @ApiModelProperty(value = "实体字段")
    private String modelColumn;

    @ApiModelProperty(value = "模板字段")
    private String tempColumn;

    @ApiModelProperty(value = "排序")
    private Integer sort;
}
