package com.cshy.common.model.request.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 添加购物车参数Request对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrderRefundComputeRequest", description = "退款金额计算")
public class OrderRefundComputeRequest {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单id", required = true)
    private Integer id;

    @ApiModelProperty(value = "退款商品数量")
    private List<RefundOrderInfoRequest> refundOrderInfoRequestList;
}
