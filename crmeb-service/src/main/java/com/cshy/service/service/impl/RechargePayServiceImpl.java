package com.cshy.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.cshy.common.constants.Constants;
import com.cshy.common.utils.DateUtil;
import com.cshy.common.model.entity.finance.UserRecharge;
import com.cshy.common.model.entity.user.User;
import com.cshy.common.model.entity.user.UserBill;
import com.cshy.service.service.RechargePayService;
import com.cshy.service.service.user.UserBillService;
import com.cshy.service.service.user.UserRechargeService;
import com.cshy.service.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;


/**
 * 支付类

 */
@Service
public class RechargePayServiceImpl implements RechargePayService {

    @Autowired
    private UserRechargeService userRechargeService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserBillService userBillService;

    /**
     * 支付成功处理
     * 增加余额，userBill记录
     * @param userRecharge 充值订单
     */
    @Override
    public Boolean paySuccess(UserRecharge userRecharge) {
        userRecharge.setPaid(true);
        userRecharge.setPayTime(DateUtil.nowDateTime());

        User user = userService.getById(userRecharge.getUid());

        BigDecimal payPrice = userRecharge.getPrice().add(userRecharge.getGivePrice());
        BigDecimal balance = user.getNowMoney().add(payPrice);
        // 余额变动对象
        UserBill userBill = new UserBill();
        userBill.setUid(userRecharge.getUid());
        userBill.setLinkId(userRecharge.getOrderId());
        userBill.setPm(1);
        userBill.setTitle("充值支付");
        userBill.setCategory(Constants.USER_BILL_CATEGORY_MONEY);
        userBill.setType(Constants.USER_BILL_TYPE_PAY_RECHARGE);
        userBill.setNumber(payPrice);
        userBill.setBalance(balance);
        userBill.setMark(StrUtil.format("余额增加了{}元", payPrice));
        userBill.setStatus(1);
        userBill.setCreateTime(DateUtil.nowDateTime());

        Boolean execute = transactionTemplate.execute(e -> {
            // 订单变动
            userRechargeService.updateById(userRecharge);
            // 余额变动
            userService.operationNowMoney(user.getUid(), payPrice, user.getNowMoney(), "add");
            // 创建记录
            userBillService.save(userBill);
            return Boolean.TRUE;
        });
        return execute;
    }
}
