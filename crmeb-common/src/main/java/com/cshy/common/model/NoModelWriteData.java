package com.cshy.common.model;

import lombok.Data;
import org.springframework.data.annotation.AccessType;

import java.util.List;
import java.util.Map;

import static org.springframework.data.annotation.AccessType.Type.FIELD;

@Data
public
class NoModelWriteData  {
    private String fileName;//文件名
    private String[] headMap;//表头数组
    private String[] dataStrMap;//对应数据字段数组
    private List<Map<String, Object>> dataList;//数据集合
}