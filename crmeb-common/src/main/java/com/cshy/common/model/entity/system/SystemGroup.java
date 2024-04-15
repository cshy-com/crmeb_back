package com.cshy.common.model.entity.system;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 组合数据表

 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_group")
@ApiModel(value="SystemGroup对象", description="组合数据表")
public class SystemGroup implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "组合数据ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "数据组名称")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String name;

    @ApiModelProperty(value = "简介")
    private String info;

    @ApiModelProperty(value = "form 表单 id")
    private Integer formId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
