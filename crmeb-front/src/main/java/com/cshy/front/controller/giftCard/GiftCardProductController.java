package com.cshy.front.controller.giftCard;

import com.cshy.common.model.Type;
import com.cshy.common.model.dto.giftCard.GiftCardProductDto;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.giftCard.GiftCardProductQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.giftCard.GiftCardProductVo;
import com.cshy.service.service.giftCard.GiftCardProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/front/giftCard/product")
@Api(tags = "v2 -- 礼品卡-商品")
public class GiftCardProductController {
    @Resource
    private GiftCardProductService giftCardProductService;


}
