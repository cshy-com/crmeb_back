package com.cshy.service.impl.system;

import com.cshy.common.model.dto.system.SysBannerActivityConfigDto;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.entity.system.SysBannerActivityConfig;
import com.cshy.common.model.query.system.SysBannerActivityConfigQuery;
import com.cshy.common.model.vo.system.SysBannerActivityConfigVo;
import com.cshy.service.dao.system.SysBannerActivityConfigDao;
import com.cshy.service.service.system.SysBannerActivityConfigService;
import org.springframework.stereotype.Service;

@Service
public class SysBannerActivityConfigServiceImpl extends BaseServiceImpl<SysBannerActivityConfig, SysBannerActivityConfigDto, SysBannerActivityConfigQuery, SysBannerActivityConfigVo, SysBannerActivityConfigDao> implements SysBannerActivityConfigService {
}
