package com.cshy.service.service;

import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.entity.user.UserTag;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.request.user.UserTagRequest;

import java.util.List;

/**
 * UserTagService 接口实现
 
 */
public interface UserTagService extends IService<UserTag> {

    /**
     * 用户标签列表
     * @param pageParamRequest 分页参数
     * @return List
     */
    List<UserTag> getList(PageParamRequest pageParamRequest);

    String getGroupNameInId(String tagIdValue);

    /**
     * 新增用户标签
     * @param userTagRequest 标签参数
     */
    Boolean create(UserTagRequest userTagRequest);

    /**
     * 删除用户标签
     * @param id 标签id
     */
    Boolean delete(Integer id);

    /**
     * 修改用户标签
     * @param id 标签id
     * @param userTagRequest 标签参数
     */
    Boolean updateTag(Integer id, UserTagRequest userTagRequest);
}
