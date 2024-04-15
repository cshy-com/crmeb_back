package com.cshy.common.model.vo.oldMall;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class OldMallListVo {
    @ApiModelProperty(value = "品牌id")
    private String id;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "排序")
    private int sort;

    @ApiModelProperty(value = "供应价格")
    private double supplyPrice;

    @ApiModelProperty(value = "主图片")
    private String mainPicture;

    @ApiModelProperty(value = "列表图片")
    private String listPicture;

    @ApiModelProperty(value = "第一详情")
    private String firstDetails;

    @ApiModelProperty(value = "第二详情")
    private String secondDetails;

    @ApiModelProperty(value = "第三详情")
    private String thirdDetails;

    @ApiModelProperty(value = "序列号")
    private String serialNumber;

    @ApiModelProperty(value = "状态")
    private int state;

    @ApiModelProperty(value = "是否基准价格")
    private int isBasePrice;

    @ApiModelProperty(value = "零售价")
    private int retailPrice;

    @ApiModelProperty(value = "图片路径列表")
    private List<String> picturePathList;
}
