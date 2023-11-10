package com.cshy.common.model.entity.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.cshy.common.model.Type;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Getter
@Setter
@ApiModel("实体 - 基类")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BaseModel<T extends BaseModel<T>> extends Model<T> {

    @NotNull(message = "id 为空", groups = {Type.Update.class})
    @ApiModelProperty(value = "主键", hidden = true)
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @Null(message = "创建时间不可录入")
    @ApiModelProperty(value = "创建时间", hidden = true)
    private String createTime;

    @Null(message = "修改时间不可录入")
    @ApiModelProperty(value = "修改时间", hidden = true)
    private String updateTime;

    @Null(message = "逻辑删除不可录入")
    @TableLogic
    @ApiModelProperty(value = "逻辑删除（0是存在，1是删除）", hidden = true)
    private Integer isDel;
}