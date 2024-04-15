package com.cshy.service.impl.sms;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.model.entity.sms.SmsRecord;
import com.cshy.service.dao.sms.SmsRecordDao;
import com.cshy.service.service.sms.SmsRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * SmsRecordServiceImpl 接口实现

 */
@Service
public class SmsRecordServiceImpl extends ServiceImpl<SmsRecordDao, SmsRecord> implements SmsRecordService {

    @Resource
    private SmsRecordDao dao;

    /**
     * 短信发送记录保存
     *
     * @param smsRecord 待保存短信记录
     * @return 保存结果
     */
    @Override
    public boolean save(SmsRecord smsRecord) {
        return dao.insert(smsRecord) > 0;
    }
}

