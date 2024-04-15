package com.cshy.service.service.system;

import com.cshy.common.model.dto.system.SysFaqDto;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.entity.base.BaseService;
import com.cshy.common.model.entity.system.SysFaq;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.system.SysFaqQuery;
import com.cshy.common.model.vo.system.SysFaqVo;

public interface SysFaqService extends BaseService<SysFaq, SysFaqDto, SysFaqQuery, SysFaqVo>
{
    CommonPage<String> categoryPage(BasePage basePage);
}
