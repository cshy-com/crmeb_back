package com.cshy.common.utils.genutils;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用于代码生成 数据库查询分页对象
 
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GenCodePageQueryUtils extends LinkedHashMap<String, Object> {
    private int page;
    private int limit;
    public GenCodePageQueryUtils(Map<String, Object> params){
        this.putAll(params);

        //分页参数
        this.page = Integer.parseInt(params.get("page").toString());
        this.limit = Integer.parseInt(params.get("limit").toString());
        this.put("offset", (page - 1) * limit);
        this.put("page", page);
        this.put("limit", limit);
    }

}
