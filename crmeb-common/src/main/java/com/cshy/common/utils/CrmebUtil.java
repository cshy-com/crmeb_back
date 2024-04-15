package com.cshy.common.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cshy.common.constants.NumConstants;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Crmeb工具类

 */
public class CrmebUtil {

    public static String encryptPassword(String pwd, String key) {
        DES des = new DES(getDESSercretKey(key));
        byte[] result = des.encrypt(pwd);
        return Base64.encode(result);
    }

    /**
     * 解密密码
     */
    public static String decryptPassowrd(String pwd, String key) {
        DES des = new DES(getDESSercretKey(key));
        return des.decryptStr(pwd);
    }

    /**
     * 获得DES加密秘钥
     * @param key
     * @return
     */
    public static byte[] getDESSercretKey(String key) {
        byte[] result = new byte[8];
        byte[] keys = null;
        keys = key.getBytes(StandardCharsets.UTF_8);
        for(int i = 0; i<8;i++){
            if(i < keys.length){
                result[i] = keys[i];
            }else{
                result[i] = 0x01;
            }
        }
        return result;
    }

    public static Map objectToMap(Object object){
        return JSONObject.parseObject(JSONObject.toJSONString(object), Map.class);
    }

    public static Map StringToMap(String strValue){
        return JSONObject.parseObject(strValue, HashMap.class);
    }

    public static <T> T mapToObj(HashMap<String,Object> map, Class<T> clz){
        if (map == null) return null;
        return JSONObject.parseObject(JSONObject.toJSONString(map), clz);
    }
    public static <T> T mapStringToObj(HashMap<String,String> map, Class<T> clz){
        if (map == null) return null;
        return JSONObject.parseObject(JSONObject.toJSONString(map), clz);
    }


    /**
     * 密码工具
     * @param args String[] 字符串数组
     */
    public static void main(String[] args) throws Exception {
//        System.out.println(encryptPassword("123456", "admin"));
//		System.out.println(decryptPassowrd("", ""));

        String key = "123456";
        String data = "中国123ABCabc";
        System.out.println("原始数据：" + data);
        String encryptPassword = encryptPassword(data, key);
        System.out.println("加密结果：" + encryptPassword);
        String decryptPassowrd = decryptPassowrd(encryptPassword, key);
        System.out.println("解密结果：" + decryptPassowrd);
        // 执行结果如下：
        // 原始数据：中国123ABCabc
        // 加密结果：5JNGj04iE/XUuTZM75zMrA==
        // 解密结果：中国123ABCabc

        System.out.println(encryptPassword("crmeb@123456", "18292417675"));
        // 执行结果：f6mcpGQ8NEmwbab2TlkpUg==
        // 与 SQL 中的数据一致
    }

    public static Map<String, Object> mergeMap(Map<String, Object> map, Map<String, Object> map1){
        HashMap<String, Object> map2 = new HashMap<>();
        map2.putAll(map);
        map2.putAll(map1);
        return map2;
    }

    public static List<Integer> stringToArray(String str){
        return stringToArrayByRegex(str, ",");
    }

    public static List<Integer> stringToArrayByRegex(String str, String regex ){
        List<Integer> list = new ArrayList<>();
        if (str.contains(regex)){

            String[] split = str.split(regex);

            for (String value : split) {
                if(!StringUtils.isBlank(value)){
                    list.add(Integer.parseInt(value.trim()));
                }
            }
        }else {
            list.add(Integer.parseInt(str));
        }
        return list;
    }


    public static List<String> stringToArrayStr(String str){
        return stringToArrayStrRegex(str, ",");
    }

    /**
     * 数字字符数据转int格式数据
     * @param str 待转换的数字字符串
     * @return int数组
     */
    public static List<Integer> stringToArrayInt(String str){
        if (com.cshy.common.utils.StringUtils.isNotBlank(str)){
            List<String> strings = stringToArrayStrRegex(str, ",");
            List<Integer> ids = new ArrayList<>();
            for (String string : strings) {
                ids.add(Integer.parseInt(string.trim()));
            }
            return ids;
        }
        return Lists.newArrayList();
    }

    public static List<String> stringToArrayStrRegex(String str, String regex ){
        List<String> list = new ArrayList<>();
        if (str.contains(regex)){

            String[] split = str.split(regex);

            for (String value : split) {
                if(!StringUtils.isBlank(value)){
                    list.add(value);
                }
            }
        }else {
            list.add(str);
        }
        return list;
    }

    public static List<Object> stringToArrayObject(String str){
        return stringToArrayObjectRegex(str, ",");
    }

    public static List<Object> stringToArrayObjectRegex(String str, String regex ){
        List<Object> list = new ArrayList<>();
        if (str.contains(regex)){

            String[] split = str.split(regex);

            for (String value : split) {
                if(!StringUtils.isBlank(value)){
                    list.add(value);
                }
            }
        }else {
            list.add(str);
        }
        return list;
    }

    public static List<String> jsonToListString(String str){
        try{
            return JSONObject.parseArray(str).toJavaList(String.class);
        }catch (Exception e){
            ArrayList<String> list = new ArrayList<>();
            list.add(str);
            return list;
        }
    }

    public static List<Integer> jsonToListInteger(String str){
        try{
            return JSONObject.parseArray(str).toJavaList(Integer.class);
        }catch (Exception e){
            ArrayList<Integer> list = new ArrayList<>();
            list.add(Integer.parseInt(str));
            return list;
        }
    }

    public static List<Object> jsonToListObject(String str){
        try{
            return JSONObject.parseArray(str).toJavaList(Object.class);
        }catch (Exception e){
            ArrayList<Object> list = new ArrayList<>();
            list.add(str);
            return list;
        }
    }

    public static <T> List<T> jsonToListClass(String str, Class<T> cls){
        try{
            return JSONObject.parseArray(str, cls);
        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    public static String getCleanLowerDomain(String domain){
        domain = domain.toLowerCase();
        domain = domain.replaceAll("http://", "");
        domain = domain.replaceAll("https://", "");
        domain = domain.replaceAll("www.", "");
        domain = domain.replaceAll("/", "");
        return domain;
    }

    public static String getBaseLowerDomain(String domain){
        if(!domain.contains(".")){
            domain  += ".com";
        }
        domain = getCleanLowerDomain(domain);
        String[] split = domain.split("\\.");
        int len = split.length;
        if(len == 0){
            return domain;
        }
        return split[len - 2] + "." + split[len - 1];
    }

    public static String getClientIp(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");
        if(checkIsIp(ip)){
            return ip;
        }

        ip = request.getHeader("X-Real-IP");
        if(checkIsIp(ip)){
            return ip;
        }

        ip = request.getRemoteAddr();
        if("0:0:0:0:0:0:0:1".equals(ip)){
            //本地 localhost访问 ipv6
            ip = "127.0.0.1";
        }
        if(checkIsIp(ip)){
            return ip;
        }

        return "";
    }

    public static boolean checkIsIp(String ip){
        if(StringUtils.isBlank(ip)){
            return false;
        }

        if("unKnown".equals(ip)){
            return false;
        }

        if("unknown".equals(ip)){
            return false;
        }

        return ip.split("\\.").length == 4;
    }

    public static String getFindInSetSql(String field, Integer value ){
        return "find_in_set(" + value +", " + field + ")";
    }

    public static String getFindInSetSql(String field, ArrayList<Integer> list ){
        ArrayList<String> sqlList = new ArrayList<>();
        for (Integer value: list) {
            sqlList.add(getFindInSetSql(field, value));
        }
        return "( " + StringUtils.join(sqlList, " or ") + ")";
    }

    public static String getFindInSetSql(String field, String idStr ){
        List<Integer> list = stringToArray(idStr);
        ArrayList<String> sqlList = new ArrayList<>();
        for (Integer value: list) {
            sqlList.add(getFindInSetSql(field, value));
        }
        return "( " + StringUtils.join(sqlList, " or ") + ")";
    }

    public static String getValueByIndex(HashMap<Integer, String> list, String categoryIdStr){
        if(list.size() < 1 || StringUtils.isBlank(categoryIdStr)){
            return "";
        }
        ArrayList<String> name = new ArrayList<>();
        List<Integer> idList = CrmebUtil.stringToArray(categoryIdStr);

        String str = "";
        for (Integer id : idList) {
             str = getStrValueByIndex(list, id);
            if(!StringUtils.isBlank(str)){
                name.add(getStrValueByIndex(list, id));
            }
        }
        if(name.size() < 1){
            return "";
        }
        return StringUtils.join(name, ",");
    }

    public static String getStrValueByIndex(HashMap<Integer, String> list, Integer key){
        if(list.size() < 1){
            return "";
        }
        return list.getOrDefault(key, "");
    }

    public static Integer getIntValueByIndex(HashMap<Integer, Integer> list, Integer key){
        if(null == list ||list.size() < 1){
            return 0;
        }
        return list.getOrDefault(key, 0);
    }

    public static String getUuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static int getRate(Integer i1, Integer i2){
        BigDecimal b1 = new BigDecimal(i1);
        BigDecimal b2 = new BigDecimal(i2);
        return getRate(b1, b2);
    }

    public static int getRate(BigDecimal b1, BigDecimal b2){
        //计算差值

        if(b2.equals(b1)){
            //数值一样，说明没有增长
            return NumConstants.NUM_ZERO;
        }

        if(b2.equals(BigDecimal.ZERO)){
            //b2是0
            return NumConstants.NUM_ONE_HUNDRED;
        }

        return (b1.subtract(b2)).divide(b2, 2, BigDecimal.ROUND_UP).multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue();


//        BigDecimal.setScale();//用于格式化小数点
//        setScale(1);//表示保留以为小数，默认用四舍五入方式
//        setScale(1,BigDecimal.ROUND_DOWN);//直接删除多余的小数位，如2.35会变成2.3
//        setScale(1,BigDecimal.ROUND_UP);//进位处理，2.35变成2.4
//        setScale(1,BigDecimal.ROUND_HALF_UP);//四舍五入，2.35变成2.4
//        setScaler(1,BigDecimal.ROUND_HALF_DOWN);//四舍五入，2.35变成2.3，如果是5则向下舍
    }

    public static BigDecimal getRateBig(Integer i1, Integer i2){
        BigDecimal b1 = new BigDecimal(i1);
        BigDecimal b2 = new BigDecimal(i2);
        return getRateBig(b1, b2);
    }

    public static BigDecimal getRateBig(BigDecimal b1, BigDecimal b2){
        //计算差值

        if(b2.compareTo(b1) == 0){
            //数值一样，说明没有增长
            return BigDecimal.ZERO;
        }

        if(b2.compareTo(BigDecimal.ZERO) == 0){
            //b2是0
            return b1.setScale(2, BigDecimal.ROUND_UP);
        }

        return (b1.subtract(b2)).multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).divide(b2, BigDecimal.ROUND_UP);
    }

    public static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static String getBase64Image(String base64) {
        return "data:image/png;base64," + base64;
    }

    /**
     * 去掉字符串第一位和最后一位
     * @param param String 参数
     * @return String
     */
    public static String trimSingeQua(String param){
        return param.substring(1,param.length()-1);
    }


    /**
     * 根据长度生成随机数字
     * @param start 起始数字
     * @param end 结束数字
     * @return 生成的随机码
     */
    public static Integer randomCount(Integer start, Integer end){
        return (int)(Math.random()*(end - start +1) + start);
    }

    /**
     * 订单号生成
     * @param payType String 支付类型
     * @return 生成的随机码
     */
    public static String getOrderNo(String payType){
        return payType + randomCount(11111, 99999) + System.currentTimeMillis() + randomCount(11111, 99999);
    }

    /**
     * map排序
     * @param map Map<String, Object> 支付类型
     * @return 生成的随机码
     */
    public static Map<String, Object> mapSort(Map<String, Object> map) {
        return new TreeMap<>(map);
    }

    /**
     * map排序然后按照url模式拼接
     * @param map Map<String, Object> 支付类型
     * @return 生成的随机码
     */
    public static String mapToStringUrl(Map<String, Object> map){
        map = CrmebUtil.mapSort(map);
        StringBuilder sb = new StringBuilder();       // 多线程访问的情况下需要用StringBuffer
        Set es = map.keySet();                 // 所有参与传参的key按照accsii排序（升序）
        for (Object set : es) {
            String k = set.toString();
            Object v = map.get(k);
            sb.append(k).append("=").append(v.toString()).append("&");
        }
        String str = sb.toString();
        return str.substring(0, str.length() - 1);
    }

    public static BigDecimal getBigDecimalRate(String rate) {
        return new BigDecimal(rate).divide(BigDecimal.TEN.multiply(BigDecimal.TEN));
    }

    /**
     * unicode编码转换为汉字
     * @param unicodeStr 待转化的编码
     * @return 返回转化后的汉子
     */
    public static String UnicodeToCN(String unicodeStr) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(unicodeStr);
        char ch;
        while (matcher.find()) {
            //group
            String group = matcher.group(2);
            //ch:'李四'
            ch = (char) Integer.parseInt(group, 16);
            //group1
            String group1 = matcher.group(1);
            unicodeStr = unicodeStr.replace(group1, ch + "");
        }

        return unicodeStr.replace("\\", "").trim();
    }

    /**
     * 汉字转化为Unicode编码
     * @param CN 待转化的中文
     * @return 返回转化之后的unicode编码
     */
    public static String CNToUnicode(String CN) {

        try {
            StringBuffer out = new StringBuffer("");
            //直接获取字符串的unicode二进制
            byte[] bytes = CN.getBytes("unicode");
            //然后将其byte转换成对应的16进制表示即可
            for (int i = 0; i < bytes.length - 1; i += 2) {
                out.append("\\u");
                String str = Integer.toHexString(bytes[i + 1] & 0xff);
                for (int j = str.length(); j < 2; j++) {
                    out.append("0");
                }
                String str1 = Integer.toHexString(bytes[i] & 0xff);
                out.append(str1);
                out.append(str);
            }
            return out.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getSign(Map<String, Object> map, String signKey){
        String result = CrmebUtil.mapToStringUrl(map) + "&key=" + signKey;
//        return DigestUtils.md5Hex(result).toUpperCase();
        String sign = SecureUtil.md5(result).toUpperCase();
        System.out.println("sign ========== " + sign);
        return sign;
    }

    /**
     * 检查是否可以转换int
     * @param str
     * @return
     */
    public static boolean isString2Num(String str){
        Pattern pattern = Pattern.compile("^[0-9]*$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    //数组去重
    public static List<Integer> arrayUnique(Integer[] arr){
        List<Integer> list = new ArrayList<>();
        for (Integer integer : arr) {
            if (!list.contains(integer)) {
                list.add(integer);
            }
        }

        return list;
    }


    /**
     * 百分比计算
     * @param detailTotalNumber  销售量
     * @param totalNumber  限量库存
     * @return  百分比
     */
    public static String percentInstance(Integer detailTotalNumber, Integer totalNumber) {
        Double bfTotalNumber = Double.valueOf(detailTotalNumber);
        Double zcTotalNumber = Double.valueOf(totalNumber);
        double percent = bfTotalNumber/zcTotalNumber;
        //获取格式化对象
        NumberFormat nt = NumberFormat.getPercentInstance();
        //设置百分数精确度2即保留两位小数
        nt.setMinimumFractionDigits(2);
        return nt.format(percent);
    }

    /**
     * 百分比计算
     * @param detailTotalNumber  销售量
     * @param totalNumber  限量库存
     * @return  百分比
     */
    public static int percentInstanceIntVal(Integer detailTotalNumber, Integer totalNumber) {
        BigDecimal sales = new BigDecimal(detailTotalNumber);
        BigDecimal total = new BigDecimal(totalNumber);
        int percentage = sales.divide(total, 2, BigDecimal.ROUND_UP).multiply(new BigDecimal(100)).intValue();
        return Math.min(percentage, 100);
    }

    /**
     * 百分比计算
     * @param detailTotalNumber  销售量
     * @param totalNumber  限量库存
     * @return  百分比
     */
    public static int percentInstanceIntVal(BigDecimal detailTotalNumber, BigDecimal totalNumber) {
        int percentage = detailTotalNumber.divide(totalNumber, 2, BigDecimal.ROUND_UP).multiply(new BigDecimal(100)).intValue();
        return Math.min(percentage, 100);
    }

    /**
     * Object转List
     */
    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if(obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    public static List<JSONObject> jsonArrayToJsonObjectList(JSONArray jsonArray) {
        List<JSONObject> list = CollUtil.newArrayList();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject arrayJSONObject = jsonArray.getJSONObject(i);
            list.add(arrayJSONObject);
        }
        return list;
    }

    /**
     * 手机号脱敏处理
     * @param phone 手机号
     */
    public static String maskMobile(String phone) {
        if (StrUtil.isBlank(phone)) {
            return "";
        }
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }
}
