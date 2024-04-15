package com.cshy.service.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.request.system.SystemFormCheckRequest;
import com.cshy.common.model.vo.ExpressSheetVo;
import com.cshy.common.model.entity.system.SystemConfig;

import java.util.HashMap;
import java.util.List;

/**
 * SystemConfigService 接口
 
 */
public interface SystemConfigService extends IService<SystemConfig> {

    /**
     * 根据menu name 获取 value
     * @param key menu name
     * @return String
     */
    String getValueByKey(String key);

    /**
     * 同时获取多个配置
     * @param keys 多个配置key
     * @return 查询到的多个结果
     */
    List<String> getValuesByKeys(List<String> keys);

    /**
     * 保存或更新配置数据
     * @param name 菜单名称
     * @param value 菜单值
     * @return Boolean
     */
    Boolean updateOrSaveValueByName(String name, String value);

    /**
     * 根据 name 获取 value 找不到抛异常
     * @param key menu name
     * @return String
     */
    String getValueByKeyException(String key);

    /**
     * 整体保存表单数据
     * @param systemFormCheckRequest SystemFormCheckRequest 数据保存
     * @return Boolean
     */
    Boolean saveForm(SystemFormCheckRequest systemFormCheckRequest);

    /**
     * 根据formId查询数据
     * @param formId Integer id
     * @return HashMap<String, String>
     */
    HashMap<String, String> info(Integer formId);

    /**
     * 获取面单默认配置信息
     * @return ExpressSheetVo
     */
    ExpressSheetVo getDeliveryInfo();

    /**
     * 获取颜色配置
     * @return SystemConfig
     */
    SystemConfig getColorConfig();

    /**
     * 加载参数缓存数据
     */
    public void loadingConfigCache();

    /**
     * 重置参数缓存数据
     */
    public void resetConfigCache();

    /**
     * 清空参数缓存数据
     */
    public void clearConfigCache();

    /**
     * 获取所有实体类的名称
     */
    List<String> modelNameList(String modelName);

    /**
     * 查询实体类
     */
    Class<?> queryModel(String modelName);

    /**
     * 根据实体类class查询表名
     * @param clazz
     * @return
     */
    String getTableCNNameByClass(Class clazz);

    /**
     * 根据类获取属性
     * @param clazz
     * @return
     */
    List<String> getFieldsByClass(Class clazz);

    /**
     * 扫描无apiModel或tableName注解的实体类
     */
    void scanNoAnnotationModel();

    /**
     * 配置首页banner位活动
     */
    //TODO
}
