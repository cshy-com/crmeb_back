package com.cshy.service.service.sms;

import com.cshy.common.model.dto.sms.SmsSignDto;
import com.cshy.common.model.entity.base.BaseService;
import com.cshy.common.model.entity.sms.SmsSign;
import com.cshy.common.model.query.sms.SmsSignQuery;
import com.cshy.common.model.vo.sms.SmsSignVo;

/**
 * SmsSignService 接口

 */
public interface SmsSignService extends BaseService<SmsSign, SmsSignDto, SmsSignQuery, SmsSignVo> {
    void sync() throws Exception;
}
