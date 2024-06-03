package com.cshy.admin.task.express;

import com.cshy.common.utils.DateUtil;
import com.cshy.service.service.ExpressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 同步物流数据 定时器
 */
@Component
@Configuration //读取配置
@EnableScheduling // 2.开启定时任务
public class ExpressTask {
    private static final Logger logger = LoggerFactory.getLogger(ExpressTask.class);

    @Autowired
    private ExpressService expressService;

    @Scheduled(cron = "0 0 */3 * * * ") //3小时执行一次
    public void sync(){
        logger.info("正在同步物流数据， 当前时间：{}", DateUtil.nowDateTime());
        try {
            expressService.syncExpressStatus();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("同步物流数据失败， 错误信息：{}", e.getMessage());
        }

    }
}
