package com.cshy.common.model.query.sms;

import com.cshy.common.model.Order;
import com.cshy.common.model.entity.base.BaseOrder;
import com.cshy.common.model.entity.sms.SmsTemplate;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApiModel("短信模板 - query")
public class SmsTemplateQuery extends SmsTemplate implements BaseOrder{
    @Override
    public List<Order> getOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("create_time", Order.DESC));
        return orders;
    }
}
