package com.cshy.common.model.request.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * PC登录请求对象 行为验证码

 */
@Data
public class SystemAdminLoginCaptchaRequest {

    @ApiModelProperty(required = true)
    String captchaVerification;

    @ApiModelProperty(required = true)
    String token;

    @ApiModelProperty(required = true)
    String secretKey;


}
