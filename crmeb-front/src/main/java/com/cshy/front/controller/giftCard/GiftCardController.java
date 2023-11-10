package com.cshy.front.controller.giftCard;

import com.cshy.common.model.response.CommonResult;
import com.cshy.service.service.giftCard.GiftCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/front/giftCard")
@Api(tags = "v2 -- 礼品卡券接口")
public class GiftCardController {
    @Autowired
    private GiftCardService giftCardService;


    //    @PreAuthorize("hasAuthority('admin:GiftCard:list')")
    @ApiOperation(value = "根据提货编码查询相关信息")
    @RequestMapping(value = "/getInfoByPickupCode", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> getInfoByPickupCode(@RequestParam String pickupCode) {
        Map<String, Object> infoByPickupCode = giftCardService.getInfoByPickupCode(pickupCode);
        return  CommonResult.success(infoByPickupCode);
    }
}