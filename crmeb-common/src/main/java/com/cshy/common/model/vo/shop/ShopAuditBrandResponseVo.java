package com.cshy.common.model.vo.shop;

import com.baomidou.mybatisplus.annotation.TableField;
import com.cshy.common.model.vo.BaseResultResponseVo;
import lombok.Data;

/**
 *

 */
@Data
public class ShopAuditBrandResponseVo extends BaseResultResponseVo {
    // 审核单id
    @TableField(value = "audit_id")
    private String auditId;
}
