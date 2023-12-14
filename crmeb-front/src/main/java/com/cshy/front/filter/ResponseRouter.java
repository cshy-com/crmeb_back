package com.cshy.front.filter;

import com.cshy.common.constants.Constants;
import com.cshy.common.utils.SpringUtil;
import com.cshy.service.service.system.SystemAttachmentService;

/**
 * response路径处理

 */
public class ResponseRouter {

    public String filter(String data, String path) {
        boolean result = un().contains(path);
        if (result) {
            return data;
        }

        if (!path.contains("api/admin/") && !path.contains("api/front/")) {
            return data;
        }

        //根据需要处理返回值
        if (data.contains(Constants.UPLOAD_TYPE_IMAGE+"/") && !data.contains("data:image/png;base64")) {
            data = SpringUtil.getBean(SystemAttachmentService.class).prefixImage(data);
        }

        if (data.contains("file/") && !data.contains("https://cshy.store/file/")) {
            data = SpringUtil.getBean(SystemAttachmentService.class).prefixFile(data);
        }

        return data;
    }

    public static String un() {
        return "";
    }
}
