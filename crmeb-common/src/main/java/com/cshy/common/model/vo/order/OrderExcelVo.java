package com.cshy.common.model.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderExcelVo", description = "产品导出")
public class OrderExcelVo implements Serializable {

    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "订单总金额")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "支付时间")
    private String payTime;

    @ApiModelProperty(value = "用户手机号")
    private String userMobile;

//    @ApiModelProperty(value = "订单状态（0：待发货；1：待收货；2：已收货，待评价；3：已完成；）")
//    private String status;

    @ApiModelProperty(value = "商品信息")
    private String productName;

    @ApiModelProperty(value = "购买数量")
    private Integer payNum;

    @ApiModelProperty(value = "减免邮费")
    private BigDecimal deductionPostage;

    @ApiModelProperty(value = "抵扣金额")
    private BigDecimal deductionPrice;

    @ApiModelProperty(value = "商户系统内部的订单号")
    private String outTradeNo;

    @ApiModelProperty(value = "订单状态")
    private String statusStr;

    @ApiModelProperty(value = "支付方式")
    private String payTypeStr;

    @ApiModelProperty(value = "邮费")
    private BigDecimal totalPostage;

    @ApiModelProperty(value = "商品价格")
    private BigDecimal productPrice;

    @ApiModelProperty(value = "支付邮费")
    private BigDecimal payPostage;

//    @ApiModelProperty(value = "是否删除")
//    private String isDel;
//
//    @ApiModelProperty(value = "退款图片")
//    private String refundReasonWapImg;

    @ApiModelProperty(value = "退款用户说明")
    private String refundReasonWapExplain;

    @ApiModelProperty(value = "退款时间")
    private String refundReasonTime;

    @ApiModelProperty(value = "前台退款原因")
    private String refundReasonWap;

    @ApiModelProperty(value = "不退款的理由")
    private String refundReason;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;

    @ApiModelProperty(value = "0 未退款 1 申请中 2 已退款")
    private String refundStatus;

    @ApiModelProperty(value = "核销码")
    private String verifyCode;

    @ApiModelProperty(value = "自提点")
    private String pickUpAddress;

    @ApiModelProperty(value = "配送方式 1=快递 ，2=门店自提")
    private String shippingType;

    @ApiModelProperty(value = "快递单号")
    private String trackingNo;

    @ApiModelProperty(value = "订单类型")
    private String orderType;

    @ApiModelProperty(value = "订单管理员备注")
    private String remark;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "商品总价")
    private BigDecimal proTotalPrice;

    @ApiModelProperty(value = "用户姓名")
    private String realName;

    @ApiModelProperty(value = "核销人")
    private String clerkName;

    @ApiModelProperty(value = "给用户退了多少积分")
    private BigDecimal backIntegral;

    @ApiModelProperty(value = "支付状态")
    private String paid;

    @ApiModelProperty(value = "收货地址")
    private String address;

    @ApiModelProperty(value = "用户真实姓名")
    private String userName;

    @ApiModelProperty(value = "供应商名称")
    private String supplier;

//    @ApiModelProperty(value = "订单类型:0-普通订单，1-视频号订单")
//    private String type;
//
//    @ApiModelProperty(value = "是否改价,0-否，1-是")
//    private String isAlterPrice;
}
