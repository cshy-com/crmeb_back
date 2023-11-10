package com.cshy.admin.task.integral;


import com.cshy.common.utils.DateUtil;
import com.cshy.service.service.UserIntegralRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 积分冻结期解冻task

 */
@Component
@Configuration //读取配置
@EnableScheduling // 2.开启定时任务
public class IntegralFrozenTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(IntegralFrozenTask.class);

    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    @Scheduled(fixedDelay = 1000 * 60L) //1分钟同步一次数据
    public void init() {
        logger.info("---IntegralFrozenTask task------produce Data with fixed rate task: Execution Time - {}", DateUtil.nowDateTime());
        try {
            userIntegralRecordService.integralThaw();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("IntegralFrozenTask.task" + " | msg : " + e.getMessage());
        }

    }
}
