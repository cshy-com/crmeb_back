package com.cshy.common.model.vo.delivery;

import lombok.Data;

import java.util.List;

@Data
public class ExpressDetailVo {
    private String number;
    private String type;
    private List<?> list;
    private String deliveryStatus;
    private String isSign;
    private String expName;
    private String expSite;
    private String expPhone;
    private String logo;
    private String courier;
    private String courierPhone;
    private String updateTime;
    private String takeTime;
}
