package com.cshy.common.model;

import javax.validation.groups.Default;

/**
 * 参数校验类型
 */
public interface Type {
    interface Add extends Default {
    }

    interface Update extends Default {
    }

    interface Page {
    }
}
