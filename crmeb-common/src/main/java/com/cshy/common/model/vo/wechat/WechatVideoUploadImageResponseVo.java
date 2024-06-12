package com.cshy.common.model.vo.wechat;

import com.cshy.common.model.vo.BaseResultResponseVo;
import lombok.Data;

/**
 *

 */
@Data
public class WechatVideoUploadImageResponseVo extends BaseResultResponseVo {

    private imageInfo img_info;

    @Data
    class imageInfo{
        private String media_id;
    }
}
