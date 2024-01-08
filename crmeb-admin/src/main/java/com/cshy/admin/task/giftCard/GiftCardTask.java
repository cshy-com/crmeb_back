//package com.cshy.admin.task.giftCard;
//
//import com.cshy.common.utils.DateUtil;
//import com.cshy.service.service.giftCard.GiftCardService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
///**
// * 更新礼品卡券状态 定时器
// */
//@Component
//@Configuration //读取配置
//@EnableScheduling // 2.开启定时任务
//public class GiftCardTask {
//    private static final Logger logger = LoggerFactory.getLogger(GiftCardTask.class);
//
//    @Autowired
//    private GiftCardService giftCardService;
//
//    @Scheduled(cron = "0 0 0 * * ?") //0点执行一次
//    public void sync(){
//        logger.info("正在更新礼品卡券状态， 当前时间：{}", DateUtil.nowDateTime());
//        try {
//            giftCardService.syncStatus();
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("更新礼品卡券状态， 错误信息：{}", e.getMessage());
//        }
//
//    }
//}
