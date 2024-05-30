package com.cshy.common.model.vo.ydd;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class YddCategoryVo {
    @ApiModelProperty(value = "分类ID")
    private int id;

    @ApiModelProperty(value = "分类名称")
    private String name;

    @ApiModelProperty(value = "子类")
    private List<YddCategoryVo> children;
}
