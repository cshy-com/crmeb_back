package com.cshy.common.model.vo.shop;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * 生成订单Vo对象
 
 */
@Data
public class ShopOrderDeliveryInfoVo {

    /** 快递公司ID，通过获取快递公司列表获取 */
    @TableField(value = "delivery_id")
    private Integer deliveryId;

    /** 快递单号 */
    @TableField(value = "waybill_id")
    private Integer waybillId;
}
