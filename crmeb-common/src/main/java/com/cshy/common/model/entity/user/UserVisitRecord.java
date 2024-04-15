package com.cshy.common.model.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 用户访问记录表

 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("u_visit_record")
@ApiModel(value="UserVisitRecord对象", description="用户访问记录表")
public class UserVisitRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "日期")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String date;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "访问类型:1-首页，2-详情页，3-营销活动详情页，4-个人中心")
    private Integer visitType;


}
