package com.cshy.common.utils;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import org.apache.commons.codec.digest.DigestUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用工具类

 */
public class CommonUtil {

    /**
     * 随机生成密码
     *
     * @param phone 手机号
     * @return 密码
     * 使用des方式加密
     */
    public static String createPwd(String phone) {
        String password = "Abc" + CrmebUtil.randomCount(10000, 99999);
        return CrmebUtil.encryptPassword(password, phone);
    }

    /**
     * 随机生成用户昵称
     *
     * @param phone 手机号
     * @return 昵称
     */
    public static String createNickName(String phone) {
        return DigestUtils.md5Hex(phone + DateUtil.getNowTime()).
                subSequence(0, 12).
                toString();
    }

    public static Map<String, Object> objToMap4Excel(Object obj, Class clazz) {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        ArrayUtil.reverse(fields);
        Arrays.stream(fields).filter(field -> field.getAnnotationsByType(ExcelProperty.class).length > 0).forEach(field -> {
            String varName = field.getName();
            try {
                // 获取原来的访问控制权限
                boolean accessFlag = field.isAccessible();
                field.setAccessible(true);
                Object o  = new Object();
                o = field.get(obj);
                if (o != null) {
                    varName = varName.toLowerCase();
                    map.put(varName, o);
                }
                // 获取原来的访问控制权限
                field.setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        });
        Map<String, Object> newMap = new HashMap<>();
        Object[] key = map.keySet().toArray();
        for (int i = 0; i < key.length; i++) {
            newMap.put(String.valueOf(key[i]), map.get(key[i]));
        }
        return newMap;
    }

    public static Map<String, Object> objToMap(Object obj, Class clazz) {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        ArrayUtil.reverse(fields);
        Arrays.stream(fields).forEach(field -> {
            String varName = field.getName();
            try {
                // 获取原来的访问控制权限
                boolean accessFlag = field.isAccessible();
                field.setAccessible(true);
                Object o  = new Object();
                o = field.get(obj);
                if (o != null) {
//                    varName = varName.toLowerCase();
                    map.put(varName, o);
                }
                // 获取原来的访问控制权限
                field.setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        });
        Map<String, Object> newMap = new HashMap<>();
        Object[] key = map.keySet().toArray();
        for (int i = 0; i < key.length; i++) {
            newMap.put(String.valueOf(key[i]), map.get(key[i]));
        }
        return newMap;
    }

}
