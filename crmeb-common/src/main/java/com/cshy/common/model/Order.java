package com.cshy.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "排序类")
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /**
     * 升序
     */
    public transient static final String ASC = "asc";

    /**
     * 降序
     */
    public transient static final String DESC = "desc";

    @ApiModelProperty(value = "排序字段")
    private String field;

    @ApiModelProperty(value = "排序方式")
    private String type;
}
