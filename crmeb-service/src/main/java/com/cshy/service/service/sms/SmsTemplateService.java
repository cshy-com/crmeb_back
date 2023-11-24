package com.cshy.service.service.sms;

import com.cshy.common.model.dto.sms.SmsTemplateDto;
import com.cshy.common.model.entity.base.BaseService;
import com.cshy.common.model.entity.sms.SmsTemplate;
import com.cshy.common.model.query.sms.SmsTemplateQuery;
import com.cshy.common.model.vo.sms.SmsTemplateVo;

/**
 * SmsTemplateService 接口

 */
public interface SmsTemplateService extends BaseService<SmsTemplate, SmsTemplateDto, SmsTemplateQuery, SmsTemplateVo> {

    /**
     * 获取详情
     * @param id 模板id
     * @return SmsTemplate
     */
    SmsTemplate getDetail(Integer id);

    void sync() throws Exception;

    void update(String id, Integer triggerPosition, String signId);

    void updateIsInterNal(String id, Integer isInterNal);
}
