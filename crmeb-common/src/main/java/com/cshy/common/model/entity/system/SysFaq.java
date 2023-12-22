package com.cshy.common.model.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cshy.common.model.entity.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_faq")
@ApiModel(value="SysFaq对象", description="常见问题表")
public class SysFaq extends BaseModel<SysFaq> {
    @ApiModelProperty(value = "问题")
    private String title;

    @ApiModelProperty(value = "问题类型")
    private String questionType;

    @ApiModelProperty(value = "回答")
    private String answer;

    @ApiModelProperty(value = "相关问题id")
    private String relatedQuestionId;

    @ApiModelProperty(value = "父级id")
    private String parentId;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "是否联系客服 0 否 1 是")
    private String contactCustomerService;


}
