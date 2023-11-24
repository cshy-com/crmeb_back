package com.cshy.common.model.entity.order;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cshy.common.model.entity.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.AccessType;

import static org.springframework.data.annotation.AccessType.Type.FIELD;

@Data
@TableName("s_short_url")
@ApiModel(value="OrderUrl对象", description="短连接关联表")
@AccessType(value = FIELD)
public class ShortUrl extends BaseModel<ShortUrl> {
    @ApiModelProperty(value = "短连接")
    private String code;

    @ApiModelProperty(value = "参数")
    private String param;

    @ApiModelProperty(value = "0 普通订单 1 礼品卡订单")
    private Integer location;

}
