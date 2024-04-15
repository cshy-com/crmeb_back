package com.cshy.service.dao.system;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cshy.common.model.entity.system.SysHomeConfig;
import org.apache.ibatis.annotations.Delete;

public interface SysHomeConfigDao extends BaseMapper<SysHomeConfig> {
    @Delete("DELETE FROM sys_home_config")
    void deleteAll();
}
