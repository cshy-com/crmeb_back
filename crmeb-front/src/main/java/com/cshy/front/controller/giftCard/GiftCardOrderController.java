package com.cshy.front.controller.giftCard;

import com.cshy.common.constants.Constants;
import com.cshy.common.enums.SMSTemplateEnum;
import com.cshy.common.model.dto.giftCard.GiftCardOrderDto;
import com.cshy.common.model.entity.user.User;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.token.FrontTokenComponent;
import com.cshy.service.service.SmsService;
import com.cshy.service.service.SystemConfigService;
import com.cshy.service.service.UserService;
import com.cshy.service.service.giftCard.GiftCardOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/front/giftCard/order")
@Api(tags = "v2 -- 礼品卡订单")
public class GiftCardOrderController {
    @Autowired
    private GiftCardOrderService giftCardOrderService;

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody GiftCardOrderDto dto, HttpServletRequest request) {
        String id = giftCardOrderService.addOrder(dto, request);
        //完成后减库存
        return CommonResult.success(id);
    }
}
