package com.cshy.common.enums;

public enum OperationType {
    INSERT(0, "新增"),
    UPDATE(1, "修改"),
    DELETE(2, "删除"),
    ;

    private Integer code;
    private String describe;

    OperationType(Integer code, String describe){
        this.code = code;
        this.describe = describe;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }

    // 根据描述获取对应的操作类型代码
    public static Integer getCodeByDescribe(String desc) {
        for (OperationType operationType : OperationType.values()) {
            if (operationType.describe.equals(desc)) {
                return operationType.code;
            }
        }
        return null;
    }

}
