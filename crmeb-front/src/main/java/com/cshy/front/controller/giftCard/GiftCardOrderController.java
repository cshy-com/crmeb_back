package com.cshy.front.controller.giftCard;

import com.cshy.common.model.dto.giftCard.GiftCardOrderDto;
import com.cshy.common.model.response.CommonResult;
import com.cshy.service.service.giftCard.GiftCardOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
