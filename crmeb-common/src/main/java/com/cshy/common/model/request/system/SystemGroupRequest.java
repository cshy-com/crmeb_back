package com.cshy.common.model.request.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;

/**
 * 组合数据表

 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_group")
@ApiModel(value="SystemGroupRequest对象", description="组合数据表")
public class SystemGroupRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "数据组名称")
    @Length(max = 50, message = "数据组名称长度不能超过50个字符")
    private String name;

    @ApiModelProperty(value = "简介")
    @Length(max = 256, message = "数据组名称长度不能超过256个字符")
    private String info;

    @ApiModelProperty(value = "form 表单 id")
    private Integer formId;

}
