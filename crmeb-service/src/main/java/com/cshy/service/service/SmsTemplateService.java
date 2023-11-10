package com.cshy.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.entity.sms.SmsTemplate;

/**
 * SmsTemplateService 接口

 */
public interface SmsTemplateService extends IService<SmsTemplate> {

    /**
     * 获取详情
     * @param id 模板id
     * @return SmsTemplate
     */
    SmsTemplate getDetail(Integer id);
}
