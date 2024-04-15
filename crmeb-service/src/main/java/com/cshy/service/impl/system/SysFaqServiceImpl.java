package com.cshy.service.impl.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cshy.common.model.dto.system.SysFaqDto;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.entity.system.SysFaq;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.query.system.SysFaqQuery;
import com.cshy.common.model.vo.system.SysFaqVo;
import com.cshy.service.dao.SysFaqDao;
import com.cshy.service.service.system.SysFaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysFaqServiceImpl extends BaseServiceImpl<SysFaq, SysFaqDto, SysFaqQuery, SysFaqVo, SysFaqDao> implements SysFaqService {
    @Override
    protected void onBeforePage(SysFaqQuery query, QueryWrapper<SysFaq> queryWrapper) {
//        queryWrapper.isNull("parent_id");
        super.onBeforePage(query, queryWrapper);
    }

    @Override
    protected void onAfterPage(Page<SysFaqVo> page) {
//        page.getRecords().forEach(sysFaqVo -> {
//            queryChildren(sysFaqVo);
//            this.queryChildren(sysFaqVo);
//        });
        super.onAfterPage(page);
    }

//    private void queryChildren(SysFaqVo sysFaqVo){
//        List<SysFaq> list = this.list(new LambdaQueryWrapper<SysFaq>().eq(SysFaq::getParentId, sysFaqVo.getId()));
//        while (CollUtil.isNotEmpty(list)) {
//            List<SysFaqVo> voList = Lists.newArrayList();
//            list.forEach(faq -> {
//                SysFaqVo childSysFaqVo = new SysFaqVo();
//                BeanUtils.copyProperties(faq, childSysFaqVo);
//                queryChildren(childSysFaqVo);
//                voList.add(childSysFaqVo);
//            });
//            sysFaqVo.setChildSysFaqVo(voList);
//            break;
//        }
//    }

    @Override
    public CommonPage<String> categoryPage(BasePage basePage) {
        Page<SysFaq> page = new Page<>();
        page.setSize(basePage.getSize());
        page.setCurrent(basePage.getCurrent());
        Page<SysFaq> sysFaqPage = this.page(page, new LambdaQueryWrapper<SysFaq>().isNotNull(SysFaq::getQuestionType).groupBy(SysFaq::getQuestionType).orderByAsc(SysFaq::getSort));

        List<String> typeList = sysFaqPage.getRecords().stream().map(SysFaq::getQuestionType).collect(Collectors.toList());
        CommonPage<String> typePage = new CommonPage<>();
        BeanUtils.copyProperties(sysFaqPage, typePage);
        typePage.setTotalPage((int) sysFaqPage.getPages());
        typePage.setList(typeList);
        return typePage;
    }
}
