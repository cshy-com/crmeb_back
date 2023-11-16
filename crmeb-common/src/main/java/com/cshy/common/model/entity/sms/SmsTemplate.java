package com.cshy.common.model.entity.sms;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cshy.common.model.entity.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 短信模板表

 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sms_template")
@ApiModel(value="SmsTemplate对象", description="短信模板表")
@AllArgsConstructor
@NoArgsConstructor
public class SmsTemplate extends BaseModel<SmsTemplate>  implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "模板编码")
    private String tempCode;

    @ApiModelProperty(value = "关联签名id")
    private String signId;

    @ApiModelProperty(value = "关联签名名称")
    private String signName;

    @ApiModelProperty(value = "模板名称")
    private String tempName;

    @ApiModelProperty(value = "类型 0：验证码短信。" +
            "1：通知短信。" +
            "2：推广短信。" +
            "3：国际/港澳台短信。" +
            "7：数字短信。")
    private String type;

    @ApiModelProperty(value = "审核状态 AUDIT_STATE_INIT：审核中。" +
            "AUDIT_STATE_PASS：审核通过。" +
            "AUDIT_STATE_NOT_PASS：审核未通过，请在返回参数Reason中查看审核未通过原因。" +
            "AUDIT_STATE_CANCEL或AUDIT_SATE_CANCEL：取消审核。")
    private String status;

    @ApiModelProperty(value = "短信内容")
    private String content;

    @ApiModelProperty(value = "触发位置 0 发送验证码 1 下单成功通知客户 2 下单成功通知员工 3 发货后通知用户 4 退货到达通知 5 退款申请提交通知 6 退款申请通过通知 ")
    private Integer triggerPosition;

    @ApiModelProperty(value = "是否为内部短信(0 否 1 是)")
    private Integer isInternal;
}
