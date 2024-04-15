package com.cshy.service.service.system;

import com.cshy.common.model.dto.system.SysHomeConfigDto;
import com.cshy.common.model.entity.base.BaseService;
import com.cshy.common.model.entity.system.SysHomeConfig;
import com.cshy.common.model.query.system.SysHomeConfigQuery;
import com.cshy.common.model.vo.system.SysHomeConfigVo;

import java.util.List;

public interface SysHomeConfigService extends BaseService<SysHomeConfig, SysHomeConfigDto, SysHomeConfigQuery, SysHomeConfigVo> {
    void updateAll(List<SysHomeConfigDto> sysHomeConfigDtoList);
}
