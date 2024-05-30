package com.cshy.common.model.dto.coupon;

import com.cshy.common.model.Coupon;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ApiModel("积分券批量新增 - Dto")
public class StoreIntegralCouponListDto {

    @Length(max = 20, message = "失效日期过长")
    @NotBlank(message = "失效日期为空")
    @ApiModelProperty(value = "失效日期", required = true)
    private String expireTime;

    @NotNull(message = "积分数组不能为空")
    @ApiModelProperty(value = "积分数组", required = true)
    private List<Coupon> couponList;
}
