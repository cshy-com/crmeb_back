package com.cshy.common.model.entity.giftCard;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cshy.common.model.entity.base.BaseModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.AccessType;

import java.util.Date;

import static org.springframework.data.annotation.AccessType.Type.FIELD;

@Data
@TableName("b_gift_card")
@ApiModel(value="GiftCard对象", description="礼品卡券表")
@AccessType(value = FIELD)
public class GiftCard extends BaseModel<GiftCard> {
    @ApiModelProperty(value = "序列号")
    @ExcelProperty(value = "序列号")
    private String serialNo;

    @ApiModelProperty(value = "礼品卡类型id")
    @ExcelIgnore
    private String giftCardTypeId;

    @ApiModelProperty(value = "二维码")
    @ExcelIgnore
    @TableField(exist = false)
    private String qrcode;

    @ApiModelProperty(value = "提货编码")
    @ExcelProperty(value = "提货编码")
    private String pickupCode;

    @ApiModelProperty(value = "提货密码")
    @ExcelProperty(value = "提货密码")
    private String pickupSecret;

    @ApiModelProperty(value = "卡状态(0. 已生效 1.待生效)")
    @ExcelIgnore
    private Integer cardStatus;

    @ApiModelProperty(value = "使用状态 0.待使用 1.已导出 2.已使用")
    @ExcelIgnore
    private Integer usingStatus;

    @ApiModelProperty(value = "生效时间")
    @ExcelIgnore
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date effectiveTime;

    @ApiModelProperty(value = "二维码")
    @ExcelProperty(value = "二维码")
    @TableField(exist = false)
    private byte[] qrcodeByte;
}
