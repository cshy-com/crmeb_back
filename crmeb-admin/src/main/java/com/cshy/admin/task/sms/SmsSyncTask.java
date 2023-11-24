package com.cshy.admin.task.sms;

import com.cshy.common.utils.DateUtil;
import com.cshy.service.service.sms.SmsSignService;
import com.cshy.service.service.sms.SmsTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 同步短信模板/签名 定时器
 */
@Component
@Configuration //读取配置
@EnableScheduling // 2.开启定时任务
public class SmsSyncTask {
    private static final Logger logger = LoggerFactory.getLogger(SmsSyncTask.class);

    @Autowired
    private SmsTemplateService smsTemplateService;

    @Autowired
    private SmsSignService smsSignService;

    @Scheduled(cron = "0 0 0 * * ?") //0点执行一次
    public void sync(){
        logger.info("正在同步短信模板/签名， 当前时间：{}", DateUtil.nowDateTime());
        try {
            smsTemplateService.sync();
            smsSignService.sync();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("同步短信模板/签名失败， 错误信息：{}", e.getMessage());
        }

    }
}
