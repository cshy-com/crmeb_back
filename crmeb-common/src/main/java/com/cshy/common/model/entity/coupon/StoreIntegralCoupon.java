package com.cshy.common.model.entity.coupon;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cshy.common.model.entity.base.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.AccessType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;

import static org.springframework.data.annotation.AccessType.Type.FIELD;

@Data
@TableName("s_integral_coupon")
@ApiModel(value="StoreIntegralCoupon", description="积分券")
@AccessType(value = FIELD)
public class StoreIntegralCoupon extends BaseModel<StoreIntegralCoupon> {
    @Null(message = "兑换码不可录入")
    @ApiModelProperty(value = "积分兑换码", hidden = true)
    @ExcelProperty(value = "序列号")
    private String couponCode;

    @NotNull(message = "积分为空")
    @ApiModelProperty(value = "积分", required = true)
    @ExcelProperty(value = "积分")
    private BigDecimal integral;

    @Null(message = "二维码")
    @ApiModelProperty(value = "二维码", hidden = true)
    private String qrCode;

    @Length(max = 20, message = "失效日期过长")
    @NotBlank(message = "失效日期为空")
    @ApiModelProperty(value = "失效日期", required = true)
    @ExcelProperty(value = "失效日期")
    private String expireTime;

    @Null(message = "是否已使用不可录入")
    @ApiModelProperty(value = "是否已使用：0是未使用，1是已使用", hidden = true)
    @ExcelIgnore
    private Boolean isUsed;

    @Null(message = "使用者Id不可录入")
    @ApiModelProperty(value = "使用者Id", hidden = true)
    @ExcelIgnore
    private Integer userId;

    @Null(message = "是否已经导出不可录入")
    @ApiModelProperty(value = "是否已经导出：0是未导出，1是已导出", hidden = true)
    @ExcelIgnore
    private Boolean isExported;

    @Null(message = "导出时间不可录入")
    @ApiModelProperty(value = "导出时间", hidden = true)
    @ExcelIgnore
    private String exportTime;

    @Null(message = "使用时间不可录入")
    @ApiModelProperty(value = "使用时间", hidden = true)
    @ExcelIgnore
    private String useTime;

    @ApiModelProperty(value = "二维码")
    @ExcelProperty(value = "二维码")
    @TableField(exist = false)
    private byte[] qrcodeByte;
}
