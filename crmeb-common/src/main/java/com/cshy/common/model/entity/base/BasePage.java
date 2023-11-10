package com.cshy.common.model.entity.base;

import com.cshy.common.model.Type;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("分页 - 基类")
public class BasePage {

    @NotNull(message = "分页参数 - 每页数量为空", groups = {Type.Page.class})
    @ApiModelProperty(value = "每页数量", required = true)
    private Long size;

    @NotNull(message = "分页参数 - 当前页为空", groups = {Type.Page.class})
    @ApiModelProperty(value = "当前页", required = true)
    private Long current;
}
