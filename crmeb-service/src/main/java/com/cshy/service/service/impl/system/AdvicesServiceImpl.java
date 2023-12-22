package com.cshy.service.service.impl.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cshy.common.model.dto.AdvicesDto;
import com.cshy.common.model.entity.system.Advices;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.entity.user.User;
import com.cshy.common.model.query.AdvicesQuery;
import com.cshy.common.model.vo.system.AdvicesVo;
import com.cshy.common.model.vo.BaseSysUserVo;
import com.cshy.common.token.FrontTokenComponent;
import com.cshy.service.dao.AdvicesDao;
import com.cshy.service.service.system.AdvicesService;
import com.cshy.service.service.system.SystemAttachmentService;
import com.cshy.service.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdvicesServiceImpl extends BaseServiceImpl<Advices, AdvicesDto, AdvicesQuery, AdvicesVo, AdvicesDao> implements AdvicesService {

    @Resource
    private UserService userService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Override
    protected void onBeforePage(AdvicesQuery query, QueryWrapper<Advices> queryWrapper) {
        if (StringUtils.isNotBlank(query.getCondition())){
            queryWrapper.like("title", query.getCondition());
        }
        queryWrapper.isNull("parent_id");
        queryWrapper.eq("is_del", 0);
        super.onBeforePage(query, queryWrapper);
    }

    @Override
    protected void onBeforeAddOrUpdate(AdvicesDto dto) {
        dto.setPicture(systemAttachmentService.clearPrefix(dto.getPicture()));
        super.onBeforeAddOrUpdate(dto);
    }

    @Override
    protected void onAfterObj(AdvicesVo vo) {
        //查询建议下的所有回复
        List<Advices> list = this.list(new LambdaQueryWrapper<Advices>().eq(Advices::getParentId, vo.getId()).eq(Advices::getIsDel, 0));
        vo.setAdvices(list);
        super.onAfterObj(vo);
    }

    @Override
    protected void onAfterObjOrListOrPage(AdvicesVo vo) {
        BaseSysUserVo baseSysUserVo = new BaseSysUserVo();
        User user = userService.getById(vo.getUserId());
        baseSysUserVo.setAvatar(user.getAvatar());
        baseSysUserVo.setNickName(user.getNickname());
        baseSysUserVo.setPhoneNumber(user.getPhone());
        vo.setBaseSysUserVo(baseSysUserVo);
        //查询回复
        Advices advices = this.getOne(new LambdaQueryWrapper<Advices>().eq(Advices::getParentId, vo.getId()));
        if (Objects.nonNull(advices))
            vo.setReply(advices.getContent());
        super.onAfterObjOrListOrPage(vo);
    }

    @Override
    public void reply(String adviceId, String content, Integer userId, String picturePathList) {
        //回复平台建议
        //查询回复数据
        Advices advices = this.getById(adviceId);
        advices.setReplied(1);
        this.updateById(advices);
        //新增回复数据
        AdvicesDto advicesDto = new AdvicesDto();
        advicesDto.setContent(content);
        advicesDto.setParentId(advices.getId().toString());
        advicesDto.setUserId(userId);
        if (Objects.nonNull(picturePathList) && !picturePathList.isEmpty()) {
            String join = String.join(",", picturePathList);
            advicesDto.setPicture(join);
        }
        this.add(advicesDto);
    }
}
