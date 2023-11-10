package com.cshy.common.constants;

/**
 * 状态相关常量
 */
public interface StatusConstants {

    Integer SUCCESS = 0;
    String SUCCESS_MESSAGE = "成功";

    String CODE_ERROR = "验证码错误";

    String DATA_NOT_FIND = "数据不存在";

    String DATA_CONVERSION_FAILED = "数据转换失败";

    String ADD_ERROR = "添加失败";

    String UPDATE_ERROR = "修改失败";

    String DELETE_ERROR = "删除失败";

    /**
     * 存在标识
     */
    Integer EXISTENCE = 0;

    /**
     * 删除标识
     */
    Integer DELETE = 1;

    /**
     * 启用
     */
    Integer ENABLE = 0;

    /**
     * 停用
     */
    Integer DEACTIVATE = 1;

    /**
     * 未使用
     */
    Integer NOT_USED = 0;

    /**
     * 已使用
     */
    Integer USED = 1;

    /**
     * 未导出
     */
    Integer NOT_EXPORTED = 0;

    /**
     * 已导出
     */
    Integer EXPORTED = 1;

    /**
     * 未过期
     */
    Integer NOT_FAILURE = 0;

    /**
     * 已过期
     */
    Integer FAILURE = 1;
}
