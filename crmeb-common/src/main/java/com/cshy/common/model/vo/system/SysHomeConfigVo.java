package com.cshy.common.model.vo.system;

import com.cshy.common.model.entity.system.SysHomeConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SysHomeConfigVo extends SysHomeConfig {
    @ApiModelProperty("子集")
    private List<SysHomeConfigVo> childConfigList;
}
