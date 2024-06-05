package com.cshy.service.impl.system;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.constants.MsgConstants;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.request.system.SystemStoreStaffRequest;
import com.cshy.common.model.response.SystemStoreStaffResponse;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.cshy.common.model.entity.system.SystemStore;
import com.cshy.common.model.entity.system.SystemStoreStaff;
import com.cshy.common.model.entity.user.User;
import com.cshy.service.dao.system.SystemStoreStaffDao;
import com.cshy.service.service.system.SystemStoreService;
import com.cshy.service.service.system.SystemStoreStaffService;
import com.cshy.service.service.user.UserService;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SystemStoreStaffServiceImpl 接口实现

 */
@Service
public class SystemStoreStaffServiceImpl extends ServiceImpl<SystemStoreStaffDao, SystemStoreStaff> implements SystemStoreStaffService {

    @Resource
    private SystemStoreStaffDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemStoreService systemStoreService;

    /**
     * 列表
     * @param storeId 门店id
     * @param pageParamRequest 分页类参数
     * @return List<SystemStoreStaff>
     */
    @Override
    public PageInfo<SystemStoreStaffResponse> getList(Integer storeId, PageParamRequest pageParamRequest) {
        Page<SystemStore> systemStorePage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<SystemStoreStaff> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (storeId > 0) {
            lambdaQueryWrapper.eq(SystemStoreStaff::getStoreId, storeId);
        }
        ArrayList<SystemStoreStaffResponse> systemStoreStaffResponseArrayList = new ArrayList<>();
        List<SystemStoreStaff> systemStoreStaffList = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(systemStoreStaffList)) {
            return new PageInfo<>();
        }

        //用户信息
        List<Integer> userIdList = systemStoreStaffList.stream().map(SystemStoreStaff::getUid).collect(Collectors.toList());
        HashMap<Integer, User> userList = null;
        if (userIdList.size() >= 1) {
            userList = userService.getMapListInUid(userIdList);
        }
        //门店信息
        TreeSet<Integer> treeSet = new TreeSet<>();
        systemStoreStaffList.stream().forEach(staff -> {
            String[] split = staff.getStoreId().split(",");
            Arrays.asList(split).forEach(s -> treeSet.add(Integer.valueOf(s)));
        });

        HashMap<Integer, SystemStore> storeList = null;
        if (treeSet.size() >= 1) {
            storeList = systemStoreService.getMapInId(Lists.newArrayList(treeSet));
        } else {
            storeList = new HashMap<>();
        }
        for (SystemStoreStaff systemStoreStaff : systemStoreStaffList) {
            SystemStoreStaffResponse systemStoreStaffResponse = new SystemStoreStaffResponse();
            BeanUtils.copyProperties(systemStoreStaff, systemStoreStaffResponse);
            if (CollUtil.isNotEmpty(userList) && userList.containsKey(systemStoreStaff.getUid())) {
                systemStoreStaffResponse.setUser(userList.get(systemStoreStaff.getUid()));
            }
            if (CollUtil.isNotEmpty(storeList)) {
                HashMap<Integer, SystemStore> finalStoreList = storeList;
                List<SystemStore> responseList = Lists.newArrayList();
                Arrays.asList(systemStoreStaff.getStoreId().split(",")).forEach(sId -> {
                    if (treeSet.contains(Integer.valueOf(sId)))
                        responseList.add(finalStoreList.get(Integer.valueOf(sId)));
                });
                systemStoreStaffResponse.setSystemStore(responseList);
            }
            systemStoreStaffResponseArrayList.add(systemStoreStaffResponse);
        }
        return CommonPage.copyPageInfo(systemStorePage, systemStoreStaffResponseArrayList);
    }

    /**
     * 查询核销员列表
     * @param userIds uidList
     * @return storeList
     */
    private List<SystemStoreStaff> getByAdminUserIds(List<Integer> userIds) {
        LambdaQueryWrapper<SystemStoreStaff> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.in(SystemStoreStaff::getUid, userIds);
        List<SystemStoreStaff> existStaffs = dao.selectList(lambdaQueryWrapper);
        return existStaffs.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 添加核销员 唯一验证
     *
     * @param request 当前添加参数
     * @return 添加结果
     */
    @Override
    public Boolean saveUnique(SystemStoreStaffRequest request) {
        SystemStoreStaff systemStoreStaff = new SystemStoreStaff();
        BeanUtils.copyProperties(request, systemStoreStaff);
        StringJoiner joiner = new StringJoiner(",");

        request.getStoreId().forEach(storeId -> joiner.add(storeId.toString()));

        systemStoreStaff.setStoreId(joiner.toString());
        return dao.insert(systemStoreStaff) > 0;
    }

    /**
     * 更新核销员信息
     * @param id 核销员id
     * @param systemStoreStaffRequest 更新参数
     */
    @Override
    public Boolean edit(Integer id, SystemStoreStaffRequest systemStoreStaffRequest) {
        SystemStoreStaff systemStoreStaff = new SystemStoreStaff();
        BeanUtils.copyProperties(systemStoreStaffRequest, systemStoreStaff);
        StringJoiner joiner = new StringJoiner(",");

        systemStoreStaffRequest.getStoreId().forEach(storeId -> joiner.add(storeId.toString()));

        systemStoreStaff.setStoreId(joiner.toString());
        systemStoreStaff.setId(id);
        return updateById(systemStoreStaff);
    }

    /**
     * 修改核销员状态
     * @param id 核销员id
     * @param status 状态
     * @return Boolean
     */
    @Override
    public Boolean updateStatus(Integer id, Integer status) {
        SystemStoreStaff systemStoreStaff = getById(id);
        if (ObjectUtil.isNull(systemStoreStaff)) {
            throw new CrmebException("核销员不存在");
        }
        if (systemStoreStaff.getStatus().equals(status)) {
            return true;
        }
        systemStoreStaff.setStatus(status);
        return updateById(systemStoreStaff);
    }

    @Override
    public SystemStoreStaffResponse info(Integer id) {
        SystemStoreStaff systemStoreStaff = getById(id);
        SystemStoreStaffResponse systemStoreStaffResponse = new SystemStoreStaffResponse();
        BeanUtils.copyProperties(systemStoreStaff, systemStoreStaffResponse);
        systemStoreStaffResponse.setUser(userService.getById(systemStoreStaff.getUid()));
        List<Integer> integerList = Arrays.asList(systemStoreStaff.getStoreId().split(",")).stream().map(sId -> Integer.valueOf(sId)).collect(Collectors.toList());
        systemStoreStaffResponse.setStoreId(integerList);

        return systemStoreStaffResponse;
    }
}

