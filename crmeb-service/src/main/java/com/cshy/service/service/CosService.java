package com.cshy.service.service;

import com.cshy.common.model.vo.CloudVo;
import com.qcloud.cos.COSClient;
import java.io.File;

/**
 * CosService 接口

 */
public interface CosService {

    void uploadFile(CloudVo cloudVo, String webPth, String localFile, Integer id, COSClient cosClient);

    void uploadFile(CloudVo cloudVo, String webPth, String localFile, File file, COSClient cosClient);
}
