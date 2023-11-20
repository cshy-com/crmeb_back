package com.cshy.common.model.vo.user;

import com.cshy.common.model.entity.user.UserVisitHistory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ApiModel("用户浏览历史 - Vo")
public class UserVisitHistoryVo extends UserVisitHistory {
    @ApiModelProperty(value = "商品信息")
    private Map<String, Object> productInfo;
}
