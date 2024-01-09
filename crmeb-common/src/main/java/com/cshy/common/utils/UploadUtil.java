package com.cshy.common.utils;

import cn.hutool.core.util.RandomUtil;
import com.cshy.common.constants.DateConstants;
import com.cshy.common.constants.UploadConstants;
import com.cshy.common.exception.CrmebException;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

/**
 * 上传工具类

 */
public class UploadUtil {

    //服务器存储地址
//    private static String rootPath  = "/www/wwwroot/upload";
    private static String rootPath  = "";

    //类型
    private static String type = "/" + UploadConstants.UPLOAD_TYPE_IMAGE;


    //模块
//    private static String modelPath = "/store";
    private static String modelPath = "/public";

    //扩展名
    private static String extStr = "png,jpg";

    //文件大小上限
    private static int size = 2;

    public static String getRootPath() {
        return rootPath;
    }

    public static void setRootPath(String rootPath) {
        UploadUtil.rootPath = (rootPath + "/").replace(" ", "").replace("//", "/");
    }

    public static String getType() {
        return type;
    }

    public static void setType(String type) {
        UploadUtil.type = type + "/";
    }

    public static String getModelPath() {
        return modelPath;
    }

    public static void setModelPath(String modelPath) {
        UploadUtil.modelPath = modelPath + "/";
    }

    public static String getExtStr() {
        return extStr;
    }

    public static void setExtStr(String extStr) {
        UploadUtil.extStr = extStr;
    }

    public static int getSize() {
        return size;
    }

    public static void setSize(int size) {
        UploadUtil.size = size;
    }

    public static File createFile(String filePath) throws IOException {
        // 获取文件的完整目录
        String fileDir = FilenameUtils.getFullPath(filePath);
        // 判断目录是否存在，不存在就创建一个目录
        File file = new File(fileDir);
        if (!file.isDirectory()) {
            //创建失败返回null
            if (!file.mkdirs()) {
                throw new CrmebException("文件目录创建失败...");
            }
        }
        // 判断这个文件是否存在，不存在就创建
        file = new File(filePath);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new CrmebException("目标文件创建失败...");
            }
        }
        return file;
    }

    public static String getDestPath(String fileName) {
        //规则：  子目录/年/月/日.后缀名
        return getServerPath() + fileName;
    }

    public static String fileName(String extName){
        return CrmebUtil.getUuid() + RandomUtil.randomString(10) + "." + extName;
    }
    public static String getServerPath() {
        // 文件分隔符转化为当前系统的格式
        return FilenameUtils.separatorsToSystem( getRootPath() + getWebPath());
    }

    public static String getWebPath() {
        // 文件分隔符转化为当前系统的格式
        return getModelPath() + DateUtil.nowDate(DateConstants.DATE_FORMAT_DATE).replace("-", "/") + "/";
//        return getType() + getModelPath() + DateUtil.nowDate(DateFormatters.DATE_FORMAT_DATE).replace("-", "/") + "/";
    }
}
