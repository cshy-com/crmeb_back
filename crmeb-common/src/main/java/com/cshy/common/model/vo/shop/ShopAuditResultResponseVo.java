package com.cshy.common.model.vo.shop;

import com.cshy.common.model.vo.BaseResultResponseVo;
import lombok.Data;

/**
 * 查询审核结果 Response

 */
@Data
public class ShopAuditResultResponseVo extends BaseResultResponseVo {

    private ItemData data;

    @Data
    class ItemData{
        private Integer status;
        private Integer brand_id;
        private String reject_reason;
    }
}
