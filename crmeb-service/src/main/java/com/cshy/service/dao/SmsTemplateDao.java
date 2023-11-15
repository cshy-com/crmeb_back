package com.cshy.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cshy.common.model.entity.sms.SmsTemplate;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 短信模板表 Mapper 接口

 */
public interface SmsTemplateDao extends BaseMapper<SmsTemplate> {

    @Update("UPDATE sms_template SET trigger_position = null where id = #{id}")
    void initTriggerPosition(@Param("id") String id);
}
