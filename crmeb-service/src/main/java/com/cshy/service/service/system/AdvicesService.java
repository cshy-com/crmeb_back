package com.cshy.service.service.system;

import com.cshy.common.model.dto.AdvicesDto;
import com.cshy.common.model.entity.system.Advices;
import com.cshy.common.model.entity.base.BaseService;
import com.cshy.common.model.query.AdvicesQuery;
import com.cshy.common.model.vo.system.AdvicesVo;

public interface AdvicesService extends BaseService<Advices, AdvicesDto, AdvicesQuery, AdvicesVo>
{
    void reply(String adviceId, String content, Integer userId, String picturePathList);
}
