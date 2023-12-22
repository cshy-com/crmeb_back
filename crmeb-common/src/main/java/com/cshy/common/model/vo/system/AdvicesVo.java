package com.cshy.common.model.vo.system;

import com.cshy.common.model.entity.system.Advices;
import com.cshy.common.model.vo.BaseSysUserVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AdvicesVo extends Advices {
    @ApiModelProperty("图片路径")
    private List<String> picturePathList;

    @ApiModelProperty("回复列表")
    private List<AdvicesVo> replyList;

    @ApiModelProperty("用户信息")
    private BaseSysUserVo baseSysUserVo;

    @ApiModelProperty("用户信息")
    private List<Advices> advices;

    @ApiModelProperty("回复内容")
    private String reply;
}
