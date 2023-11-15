package com.cshy.common.model.dto.sms;

import com.cshy.common.model.entity.sms.SmsTemplate;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel("短信模板 - Dto")
public class SmsTemplateDto extends SmsTemplate {
}
