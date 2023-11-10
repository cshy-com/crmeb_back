//package com.cshy.common.config;
//
//import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
//import com.github.houbb.sensitive.word.support.allow.WordAllows;
//import com.github.houbb.sensitive.word.support.deny.WordDenys;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class SensitiveWordConfig {
//
//
//    /**
//     * 初始化引导类
//     * @return 初始化引导类
//     * @since 1.0.0
//     */
//    @Bean
//    public SensitiveWordBs sensitiveWordBs() {
//        SensitiveWordBs sensitiveWordBs = SensitiveWordBs.newInstance()
//                .wordAllow(WordAllows.defaults())
//                .wordDeny(WordDenys.defaults())
//                // 各种其他配置
//                .init();
//
//        return sensitiveWordBs;
//    }
//
//}