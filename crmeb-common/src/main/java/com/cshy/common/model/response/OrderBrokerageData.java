package com.cshy.common.model.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单佣金统计数

 */
@Data
public class OrderBrokerageData {

    private Integer num;
    private BigDecimal price;
}
