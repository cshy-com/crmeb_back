package com.cshy.common.model.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cshy.common.model.entity.system.SystemStore;
import com.cshy.common.model.entity.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 门店店员表

 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_store_staff")
@ApiModel(value="SystemStoreStaffResponse对象", description="门店店员表")
public class SystemStoreStaffResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "微信用户id")
    private Integer uid;

    @ApiModelProperty(value = "店员头像")
    private String avatar;

    @ApiModelProperty(value = "用户信息")
    private User user;

    @ApiModelProperty(value = "门店id")
    private List<Integer> storeId;

    @ApiModelProperty(value = "门店")
    private List<SystemStore> systemStore;

    @ApiModelProperty(value = "店员名称")
    private String staffName;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "核销开关")
    private Integer verifyStatus;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
