package com.cshy.service.impl.system;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cshy.common.model.dto.system.SysHomeConfigDto;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.entity.system.SysHomeConfig;
import com.cshy.common.model.query.system.SysHomeConfigQuery;
import com.cshy.common.model.vo.system.SysHomeConfigVo;
import com.cshy.service.dao.system.SysHomeConfigDao;
import com.cshy.service.service.system.SysHomeConfigService;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class SysHomeConfigServiceImpl extends BaseServiceImpl<SysHomeConfig, SysHomeConfigDto, SysHomeConfigQuery, SysHomeConfigVo, SysHomeConfigDao> implements SysHomeConfigService {
    @Override
    public List<SysHomeConfigVo> list(SysHomeConfigQuery query) {
        List<SysHomeConfigVo> resList = Lists.newArrayList();
        //查询所有父级数据
        List<SysHomeConfig> parentList = this.list(new LambdaQueryWrapper<SysHomeConfig>().isNull(SysHomeConfig::getParentId).orderByAsc(SysHomeConfig::getSort));
        parentList.forEach(sysHomeConfig -> {
            SysHomeConfigVo sysHomeConfigVo = new SysHomeConfigVo();
            BeanUtils.copyProperties(sysHomeConfig, sysHomeConfigVo);
            //查询子级数据
            List<SysHomeConfig> childList = this.list(new LambdaQueryWrapper<SysHomeConfig>().eq(SysHomeConfig::getParentId, sysHomeConfig.getId()).orderByAsc(SysHomeConfig::getSort));
            List<SysHomeConfigVo> homeConfigVoList = childList.stream().map(child -> {
                SysHomeConfigVo childVo = new SysHomeConfigVo();
                BeanUtils.copyProperties(child, childVo);
                return childVo;
            }).collect(Collectors.toList());
            sysHomeConfigVo.setChildConfigList(homeConfigVoList);
            resList.add(sysHomeConfigVo);
        });
        return resList;
    }

    @Override
    public void updateAll(List<SysHomeConfigDto> sysHomeConfigDtoList) {
        //删除所有数据
        this.baseMapper.deleteAll();
        AtomicInteger j = new AtomicInteger(1);
        sysHomeConfigDtoList.forEach(sysHomeConfigDto -> {
            sysHomeConfigDto.setSort(j.get());
            String pid = this.add(sysHomeConfigDto);
            List<SysHomeConfigDto> childConfigList = sysHomeConfigDto.getChildConfigList();
            if (CollUtil.isNotEmpty(childConfigList)) {
                AtomicInteger i = new AtomicInteger(1);
                childConfigList.forEach(child -> {
                    child.setParentId(pid);
                    child.setSort(i.get());
                    this.add(child);
                    i.getAndIncrement();
                });
            }
            j.getAndIncrement();
        });
    }
}
