package com.cshy.common.model.request.sms;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 短信模板查询对象

 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SmsTemplateRequest对象", description="短信模板查询对象")
public class SmsTemplateRequest {
    @ApiModelProperty(value = "短信模板代码", required = true)
    public String templateCode;

    @ApiModelProperty(value = "短信模板名称", required = true)
    public String templateName;
}
