package com.cshy.common.model.entity.sms;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 短信发送记录表

 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sms_record")
@ApiModel(value="SmsRecord对象", description="短信发送记录表")
public class SmsRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "短信发送记录编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "接受短信的手机号")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String phone;

    @ApiModelProperty(value = "短信内容")
    private String content;

    @ApiModelProperty(value = "添加记录ip")
    private String addIp;

    @ApiModelProperty(value = "短信模板ID")
    private String template;

    @ApiModelProperty(value = "短信模板名称")
    private String templateName;

    @ApiModelProperty(value = "状态码")
    private String resultCode;

    @ApiModelProperty(value = "发送记录id")
    private Integer recordId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "备注")
    private String memo;
}
