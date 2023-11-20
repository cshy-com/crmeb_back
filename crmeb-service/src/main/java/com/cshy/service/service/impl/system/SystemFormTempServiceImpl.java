package com.cshy.service.service.impl.system;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.request.*;
import com.cshy.common.model.request.system.SystemFormCheckRequest;
import com.cshy.common.model.request.system.SystemFormItemCheckRequest;
import com.cshy.common.model.request.system.SystemFormTempRequest;
import com.cshy.common.model.request.system.SystemFormTempSearchRequest;
import com.cshy.common.model.vo.system.SystemConfigFormItemConfigRegListVo;
import com.cshy.common.model.vo.system.SystemConfigFormItemVo;
import com.cshy.common.model.vo.system.SystemConfigFormVo;
import com.github.pagehelper.PageHelper;
import com.cshy.common.utils.ValidateFormUtil;
import com.cshy.common.model.entity.system.SystemFormTemp;
import com.cshy.service.dao.system.SystemFormTempDao;
import com.cshy.service.service.system.SystemFormTempService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * SystemFormTempServiceImpl 接口实现

 */
@Service
public class SystemFormTempServiceImpl extends ServiceImpl<SystemFormTempDao, SystemFormTemp> implements SystemFormTempService {

    @Resource
    private SystemFormTempDao dao;

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @return List<SystemFormTemp>
    */
    @Override
    public List<SystemFormTemp> getList(SystemFormTempSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 SystemFormTemp 类的多条件查询
        LambdaQueryWrapper<SystemFormTemp> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isBlank(request.getKeywords())) {
            lambdaQueryWrapper.eq(SystemFormTemp::getId, request.getKeywords()).
                    or().like(SystemFormTemp::getName, request.getKeywords()).
                    or().like(SystemFormTemp::getInfo, request.getKeywords());
        }
        lambdaQueryWrapper.orderByDesc(SystemFormTemp::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 验证item规则
     * @param systemFormCheckRequest SystemFormCheckRequest 表单数据提交
     */
    @Override
    public void checkForm(SystemFormCheckRequest systemFormCheckRequest) {
        //循环取出item数据， 组合成 key => val 的map格式
        HashMap<String, String> map = new HashMap<>();
        for (SystemFormItemCheckRequest systemFormItemCheckRequest : systemFormCheckRequest.getFields()) {
            map.put(systemFormItemCheckRequest.getName(), systemFormItemCheckRequest.getValue());
        }

        //取出表单模型的数据
        SystemFormTemp formTemp = getById(systemFormCheckRequest.getId());

        //解析表单规则进行验证
        SystemConfigFormVo systemConfigFormVo;
        try {
            systemConfigFormVo =  JSONObject.parseObject(formTemp.getContent(), SystemConfigFormVo.class);
        } catch (Exception e) {
            throw new CrmebException("模板表单 【" + formTemp.getName() + "】 的内容不是正确的JSON格式！");
        }

        SystemConfigFormItemVo systemConfigFormItemVo;
        for (String item : systemConfigFormVo.getFields()) {
            systemConfigFormItemVo = JSONObject.parseObject(item, SystemConfigFormItemVo.class);
            String model = systemConfigFormItemVo.get__vModel__(); //字段 name

            if(systemConfigFormItemVo.get__config__().getRequired() && "".equals(map.get(model))) {
                throw new CrmebException(systemConfigFormItemVo.get__config__().getLabel() + "不能为空！");
            }
            //正则验证
            checkRule(systemConfigFormItemVo.get__config__().getRegList(), map.get(model),  systemConfigFormItemVo.get__config__().getLabel());
        }
    }

    /**
     * 新增表单模板
     * @param systemFormTempRequest 新增参数
     */
    @Override
    public Boolean add(SystemFormTempRequest systemFormTempRequest) {
        try {
            JSONObject.parseObject(systemFormTempRequest.getContent(), SystemConfigFormVo.class);
        } catch (Exception e) {
            throw new CrmebException("模板表单 【" + systemFormTempRequest.getName() + "】 的内容不是正确的JSON格式！");
        }

        SystemFormTemp systemFormTemp = new SystemFormTemp();
        BeanUtils.copyProperties(systemFormTempRequest, systemFormTemp);
        return save(systemFormTemp);
    }

    /**
     * 修改表单模板
     * @param id integer id
     * @param systemFormTempRequest 修改参数
     */
    @Override
    public Boolean edit(Integer id, SystemFormTempRequest systemFormTempRequest) {
        try {
            JSONObject.parseObject(systemFormTempRequest.getContent(), SystemConfigFormVo.class);
        } catch (Exception e) {
            throw new CrmebException("模板表单 【" + systemFormTempRequest.getName() + "】 的内容不是正确的JSON格式！");
        }

        SystemFormTemp systemFormTemp = new SystemFormTemp();
        BeanUtils.copyProperties(systemFormTempRequest, systemFormTemp);
        systemFormTemp.setId(id);
        return updateById(systemFormTemp);
    }

    /**
     * 验证item规则
     * @param regList List<SystemConfigFormItemConfigRegListVo 正则表达式列表
     * @param value String 验证的值
     * @param name String 提示语字段名称
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    private void checkRule(List<SystemConfigFormItemConfigRegListVo> regList, String value, String name) {
        if(regList.size() > 0) {
            for (SystemConfigFormItemConfigRegListVo systemConfigFormItemConfigRegListVo : regList) {
                if(!ValidateFormUtil.regular(value, name, systemConfigFormItemConfigRegListVo.getPattern())) {
                    throw new CrmebException(systemConfigFormItemConfigRegListVo.getMessage());
                }
            }
        }
    }
}
