package com.cshy.common.utils;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.cshy.common.model.NoModelWriteData;
import com.cshy.common.model.entity.giftCard.GiftCard;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

public class EasyExcelUtils<T> {
    public NoModelWriteData buildData(List<T> dataList, Class<T> clazz, String suffix) {
        //设置文件名称
        ApiModel annotation = clazz.getAnnotation(ApiModel.class);
        String filename;
        if (Objects.nonNull(annotation))
            filename = annotation.description();
        else
            filename = "未命名表格";

        filename = filename.replace("表", "导出") + "_" + new Date().getTime() + suffix;

        //处理数据
        List<Map<String, Object>> mapList = Lists.newArrayList();
        dataList.forEach(data -> {
            mapList.add(CommonUtil.objToMap4Excel(data, clazz));
        });

        //获取需要导出的字段、名称
        List<String> headList = Lists.newArrayList();
        Field[] declaredFields = GiftCard.class.getDeclaredFields();
        List<String> fieldNameList = Arrays.asList(declaredFields).stream().filter(field -> field.getAnnotationsByType(ExcelProperty.class).length > 0)
                .map(field -> {
                    //字段
                    headList.add(field.getAnnotationsByType(ExcelProperty.class)[0].value()[0]);
                    //字段名称
                    return field.getName().toLowerCase();
                }).collect(Collectors.toList());

        String[] nameArr = fieldNameList.toArray(new String[fieldNameList.size()]);
        String[] headArr = headList.toArray(new String[headList.size()]);

        //构建对象
        NoModelWriteData noModelWriteData = new NoModelWriteData();
        noModelWriteData.setDataList(mapList);
        noModelWriteData.setHeadMap(headArr);
        noModelWriteData.setFileName(filename);
        noModelWriteData.setDataStrMap(nameArr);

        return noModelWriteData;
    }

    //不创建对象的导出
    public void noModelWrite(NoModelWriteData data, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = data.getFileName().substring(data.getFileName().lastIndexOf("\\") + 1);
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            EasyExcel.write(response.getOutputStream())
                    .registerWriteHandler(new CustomCellWriteWidthConfig())
                    .registerWriteHandler(new CustomCellWriteHeightConfig())
                    .registerWriteHandler(EasyExcelUtils.getStyleStrategy())
                    .head(head(data.getHeadMap()))
                    .sheet(fileName.substring(0, fileName.indexOf("_")))
                    .doWrite(dataList(data.getDataList(), data.getDataStrMap()));
        } catch (Exception e) {
            // 重置response
//            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = new HashMap<String, String>();
            map.put("status", "failure");
            map.put("message", "下载文件失败" + e.getMessage());
            response.getWriter().println(JSON.toJSONString(map));
        }
    }

    //设置导出的数据内容
    private List<List<Object>> dataList(List<Map<String, Object>> dataList, String[] dataStrMap) {
        List<List<Object>> list = new ArrayList<List<Object>>();
        for (Map<String, Object> map : dataList) {
            List<Object> data = new ArrayList<Object>();
            for (int i = 0; i < dataStrMap.length; i++) {
                data.add(map.get(dataStrMap[i]));
            }
            list.add(data);
        }
        return list;
    }

    //设置表头
    private List<List<String>> head(String[] headMap) {
        List<List<String>> list = new ArrayList<List<String>>();
        for (String head : headMap) {
            List<String> headList = new ArrayList<String>();
            headList.add(head);
            list.add(headList);
        }
        return list;
    }

    /**
     * 设置excel样式
     */
    public static HorizontalCellStyleStrategy getStyleStrategy() {
        // 头的策略  样式调整
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 头背景 浅绿
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        WriteFont headWriteFont = new WriteFont();
        // 头字号
        headWriteFont.setFontHeightInPoints((short) 12);
        // 字体样式
        headWriteFont.setFontName("宋体");
        headWriteCellStyle.setWriteFont(headWriteFont);
        // 自动换行
        headWriteCellStyle.setWrapped(true);
        // 设置细边框
        headWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        headWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        headWriteCellStyle.setBorderRight(BorderStyle.THIN);
        headWriteCellStyle.setBorderTop(BorderStyle.THIN);
        // 设置边框颜色 25灰度
        headWriteCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        headWriteCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headWriteCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headWriteCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        // 水平对齐方式
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 垂直对齐方式
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 内容的策略 宋体
        WriteCellStyle contentStyle = new WriteCellStyle();
        // 设置垂直居中
        contentStyle.setWrapped(true);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置 水平居中
//        contentStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        WriteFont contentWriteFont = new WriteFont();
        // 内容字号
        contentWriteFont.setFontHeightInPoints((short) 12);
        // 字体样式
        contentWriteFont.setFontName("宋体");
        contentStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentStyle);
    }
}
