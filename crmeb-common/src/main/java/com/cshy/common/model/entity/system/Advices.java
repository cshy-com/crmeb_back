package com.cshy.common.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cshy.common.model.entity.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@TableName("sys_advices")
@ApiModel(value = "Advices对象", description = "建议意见表")
public class Advices extends BaseModel<Advices> {

    @NotNull(message = "UserId为空")
    @ApiModelProperty(value = "UserId", required = true)
    private Integer userId;

    @NotNull(message = "ParentId为空")
    @ApiModelProperty(value = "ParentId", required = true)
    private String parentId;

    @NotBlank(message = "手机号")
    @ApiModelProperty(value = "手机号", required = true)
    private String mobile;

    @NotBlank(message = "标题为空")
    @ApiModelProperty(value = "标题", required = true)
    private String title;

    @NotBlank(message = "内容为空")
    @ApiModelProperty(value = "内容", required = true)
    private String content;

    @ApiModelProperty(value = "图片", required = true)
    private String picture;

    @ApiModelProperty(value = "是否回复（0为未回复，1为已回复）", required = true)
    private Integer replied;

}
