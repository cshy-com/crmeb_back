package com.cshy.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.entity.sms.SmsTemplate;
import com.cshy.service.dao.SmsTemplateDao;
import com.cshy.service.service.SmsTemplateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * SmsTemplateServiceImpl 接口实现
 
 */
@Service
public class SmsTemplateServiceImpl extends ServiceImpl<SmsTemplateDao, SmsTemplate> implements SmsTemplateService {

    @Resource
    private SmsTemplateDao dao;

    /**
     * 获取详情
     * @param id 模板id
     * @return SmsTemplate
     */
    @Override
    public SmsTemplate getDetail(Integer id) {
        SmsTemplate smsTemplate = getById(id);
        if (ObjectUtil.isNull(smsTemplate)) {
            throw new CrmebException("短信模板不存在");
        }
        return smsTemplate;
    }
}

