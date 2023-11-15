package com.cshy.common.model.entity.sms;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cshy.common.model.entity.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 短信签名表

 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sms_sign")
@ApiModel(value="SmsSign对象", description="短信签名表")
@AllArgsConstructor
@NoArgsConstructor
public class SmsSign extends BaseModel<SmsSign>  implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "模板名称")
    private String tempName;

    @ApiModelProperty(value = "类型 0：验证码短信。\n" +
            "1：通知短信。\n" +
            "2：推广短信。\n" +
            "3：国际/港澳台短信。\n" +
            "7：数字短信。")
    private String BusinessType;

    @ApiModelProperty(value = "审核状态 AUDIT_STATE_INIT：审核中。\n" +
            "AUDIT_STATE_PASS：审核通过。\n" +
            "AUDIT_STATE_NOT_PASS：审核未通过，请在返回参数Reason中查看审核未通过原因。\n" +
            "AUDIT_STATE_CANCEL：取消审核。")
    private String status;
}
