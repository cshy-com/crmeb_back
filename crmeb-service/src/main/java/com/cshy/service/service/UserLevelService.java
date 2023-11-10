package com.cshy.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.entity.user.User;
import com.cshy.common.model.entity.user.UserLevel;

import java.util.List;

/**
 * UserLevelService 接口实现

 */
public interface UserLevelService extends IService<UserLevel> {

    /**
     * 用户等级列表
     * @param pageParamRequest 分页参数
     * @return List
     */
    List<UserLevel> getList(PageParamRequest pageParamRequest);

    /**
     * 经验升级
     * @param user 用户
     * @return Boolean
     */
    Boolean upLevel(User user);

    /**
     * 经验降级
     * @param user 用户
     * @return Boolean
     */
    Boolean downLevel(User user);

    /**
     * 删除（通过系统等级id）
     * @param levelId 系统等级id
     * @return Boolean
     */
    Boolean deleteByLevelId(Integer levelId);

}
