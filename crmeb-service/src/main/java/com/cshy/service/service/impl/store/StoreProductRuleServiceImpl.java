package com.cshy.service.service.impl.store;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.request.store.StoreProductRuleRequest;
import com.cshy.common.model.request.store.StoreProductRuleSearchRequest;
import com.github.pagehelper.PageHelper;
import com.cshy.common.model.entity.product.StoreProductRule;
import com.cshy.service.dao.store.StoreProductRuleDao;
import com.cshy.service.service.store.StoreProductRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * StoreProductRuleServiceImpl 接口实现

 */
@Service
public class StoreProductRuleServiceImpl extends ServiceImpl<StoreProductRuleDao, StoreProductRule> implements StoreProductRuleService {

    @Resource
    private StoreProductRuleDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @return List<StoreProductRule>
    */
    @Override
    public List<StoreProductRule> getList(StoreProductRuleSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreProductRule 类的多条件查询
        LambdaQueryWrapper<StoreProductRule> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(null != request.getKeywords()){
            lambdaQueryWrapper.like(StoreProductRule::getRuleName, request.getKeywords());
            lambdaQueryWrapper.or().like(StoreProductRule::getRuleValue, request.getKeywords());
        }
        lambdaQueryWrapper.orderByDesc(StoreProductRule::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 新增商品规格
     * @param storeProductRuleRequest 规格参数
     * @return 新增结果
     */
    @Override
    public boolean save(StoreProductRuleRequest storeProductRuleRequest) {
        if(getListByRuleName(storeProductRuleRequest.getRuleName()).size() > 0){
            throw new CrmebException("此规格值已经存在");
        }
        StoreProductRule storeProductRule = new StoreProductRule();
        BeanUtils.copyProperties(storeProductRuleRequest, storeProductRule);
        return save(storeProductRule);
    }

    /**
     * 修改规格
     * @param storeProductRuleRequest 规格参数
     * @return Boolean
     */
    @Override
    public Boolean updateRule(StoreProductRuleRequest storeProductRuleRequest) {
        StoreProductRule storeProductRule = new StoreProductRule();
        BeanUtils.copyProperties(storeProductRuleRequest, storeProductRule);
        storeProductRule.setId(storeProductRuleRequest.getId());
        return updateById(storeProductRule);
    }

    /**
     * 根据规格名称查询同名规格
     * @param ruleName 规格名称
     * @return 查询到的数据
     */
    private List<StoreProductRule> getListByRuleName(String ruleName){
        LambdaQueryWrapper<StoreProductRule> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isBlank(ruleName)){
            return new ArrayList<>();
        }
        lambdaQueryWrapper.eq(StoreProductRule::getRuleName, ruleName);
        return dao.selectList(lambdaQueryWrapper);
    }
}
