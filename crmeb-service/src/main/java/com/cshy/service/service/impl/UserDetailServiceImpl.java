package com.cshy.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cshy.common.model.entity.system.SystemAdmin;
import com.cshy.common.model.entity.system.SystemMenu;
import com.cshy.common.model.entity.system.SystemPermissions;
import com.cshy.common.model.entity.system.SystemRole;
import com.cshy.common.model.vo.LoginUserVo;
import com.cshy.service.service.SystemAdminService;
import com.cshy.service.service.SystemMenuService;
import com.cshy.service.service.SystemRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用户验证处理
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private SystemMenuService systemMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemAdmin user = systemAdminService.selectUserByUserName(username);
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new UsernameNotFoundException("登录用户：" + username + " 不存在");
        } else if (!user.getStatus()) {
            log.info("登录用户：{} 已被停用.", username);
            throw new UsernameNotFoundException("对不起，您的账号：" + username + " 已停用");
        }

        return createLoginUser(user);
    }

    public UserDetails createLoginUser(SystemAdmin user) {
        List<Integer> roles = Stream.of(user.getRoles().split(",")).map(Integer::valueOf).collect(Collectors.toList());
        //菜单权限
        List<SystemMenu> menuList;
        if (roles.contains(1)) {// 超级管理员
            // 获取全部权限
            menuList = systemMenuService.getAllPermissions();
        } else {
            menuList = systemMenuService.findPermissionByUserId(user.getId());
        }
        menuList = menuList.stream().filter(e -> StrUtil.isNotEmpty(e.getPerms())).collect(Collectors.toList());
        List<SystemPermissions> permissionsList = menuList.stream().map(e -> {
            SystemPermissions permissions = new SystemPermissions();
            permissions.setId(e.getId());
            permissions.setPid(e.getPid());
            permissions.setName(e.getName());
            permissions.setPath(e.getPerms());
            permissions.setSort(e.getSort());
            return permissions;
        }).collect(Collectors.toList());

        //角色权限
        List<SystemAdmin> adminList = this.systemAdminService.list();
        List<SystemPermissions> adminPermissionsList = adminList.stream().map(e -> {
            SystemPermissions permissions = new SystemPermissions();
            permissions.setId(e.getId());
            permissions.setPid(0);
            permissions.setName(e.getAccount());
            permissions.setPath("ROLE_" + e.getAccount());
            permissions.setSort(0);
            return permissions;
        }).collect(Collectors.toList());

        permissionsList.addAll(adminPermissionsList);
        return new LoginUserVo(user, permissionsList);
    }


}
