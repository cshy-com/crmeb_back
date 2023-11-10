package com.cshy.common.model.vo;

import lombok.Data;

/**
 * 获取接入状态 response

 */
@Data
public class RegisterCheckResponseVo  extends BaseResultResponseVo {

    private RegisterCheckDataItemnVo data;
}
