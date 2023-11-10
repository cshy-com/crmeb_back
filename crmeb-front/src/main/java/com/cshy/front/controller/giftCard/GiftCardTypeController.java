package com.cshy.front.controller.giftCard;

import com.cshy.common.model.Type;
import com.cshy.common.model.dto.giftCard.GiftCardTypeDto;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.giftCard.GiftCardTypeQuery;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.vo.giftCard.GiftCardTypeVo;
import com.cshy.common.model.vo.giftCard.GiftCardVo;
import com.cshy.service.service.giftCard.GiftCardTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/front/giftCard/type")
@Api(tags = "v2 -- 礼品卡类型")
public class GiftCardTypeController {
    @Autowired
    private GiftCardTypeService giftCardTypeService;

}
