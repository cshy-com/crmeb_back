package com.cshy.common.model.dto.system;

import com.cshy.common.model.entity.system.SysHomeConfig;
import lombok.Data;

import java.util.List;

@Data
public class SysHomeConfigDto extends SysHomeConfig {
    List<SysHomeConfigDto> childConfigList;
}
