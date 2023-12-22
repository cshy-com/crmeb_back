package com.cshy.common.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("基础用户 - Vo")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BaseSysUserVo {
    @ApiModelProperty("用户昵称")
    private String nickName;

    @ApiModelProperty("手机号码")
    private String phoneNumber;

    @ApiModelProperty("用户类型")
    private String userType;

    @ApiModelProperty("用户头像")
    private String avatar;
}
