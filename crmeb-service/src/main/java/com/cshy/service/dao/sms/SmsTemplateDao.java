package com.cshy.service.dao.sms;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cshy.common.model.entity.sms.SmsTemplate;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 短信模板表 Mapper 接口

 */
public interface SmsTemplateDao extends BaseMapper<SmsTemplate> {

    @Update("UPDATE sms_template SET trigger_position = null where id = #{id}")
    void initTriggerPosition(@Param("id") String id);

    @Delete("<script>" +
            "   delete from sms_template " +
            "   where 1=1 " +
            "   <if test=\"null != ids and ids.size > 0\"> " +
            "       AND id in  " +
            "       <foreach collection=\"ids\" item=\"id\" open=\"(\" separator=\",\"  close=\")\"> " +
            "           #{id} " +
            "       </foreach> " +
            "   </if> " +
            " </script>")
    void batchDeleteByIds(@Param("ids") List<String> ids);
}
