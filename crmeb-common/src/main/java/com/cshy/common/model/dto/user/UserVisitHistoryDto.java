package com.cshy.common.model.dto.user;

import com.cshy.common.model.entity.user.UserVisitHistory;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("用户浏览历史 - Dto")
public class UserVisitHistoryDto extends UserVisitHistory {
}
