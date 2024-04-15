package com.cshy.service.impl.system;

import com.cshy.common.model.dto.system.SysTemplateColumnConfigDto;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.entity.system.SysTemplateColumnConfig;
import com.cshy.common.model.query.system.SysTemplateColumnConfigQuery;
import com.cshy.common.model.vo.system.SysTemplateColumnConfigVo;
import com.cshy.service.dao.system.SysTemplateColumnConfigDao;
import com.cshy.service.service.system.SysTemplateColumnConfigService;
import org.springframework.stereotype.Service;

@Service
public class SysTemplateColumnConfigServiceImpl extends BaseServiceImpl<SysTemplateColumnConfig, SysTemplateColumnConfigDto, SysTemplateColumnConfigQuery, SysTemplateColumnConfigVo, SysTemplateColumnConfigDao> implements SysTemplateColumnConfigService {
}
