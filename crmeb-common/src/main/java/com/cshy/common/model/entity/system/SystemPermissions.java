package com.cshy.common.model.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_permissions")
@ApiModel(value="SystemPermissions对象", description="系统权限表")
public class SystemPermissions implements Serializable {

    private static final long serialVersionUID=1L;

//    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "父级ID")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer pid;

    @ApiModelProperty(value = "权限名称")
    private String name;

    @ApiModelProperty(value = "权限地址")
    private String path;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDelte;


}
