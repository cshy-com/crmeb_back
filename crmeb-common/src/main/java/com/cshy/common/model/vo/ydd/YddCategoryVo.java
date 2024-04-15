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

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "排序")
    private int sort;

    @ApiModelProperty(value = "父节点ID")
    private int pid;

    @ApiModelProperty(value = "状态")
    private int status;

    @ApiModelProperty(value = "创建时间")
    private long createTime;

    @ApiModelProperty(value = "更新时间")
    private long updateTime;

    @ApiModelProperty(value = "品牌列表")
    private String brandList;

    @ApiModelProperty(value = "应用ID")
    private int appId;

    @ApiModelProperty(value = "层级")
    private int level;

    @ApiModelProperty(value = "商品数量")
    private int itemNum;

    @ApiModelProperty(value = "商品数量")
    private List<YddCategoryVo> children;
}
