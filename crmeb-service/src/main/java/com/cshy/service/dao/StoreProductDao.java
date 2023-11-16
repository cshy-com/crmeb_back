package com.cshy.service.dao;

import com.cshy.common.model.entity.product.StoreProduct;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 商品表 Mapper 接口

 */
public interface StoreProductDao extends BaseMapper<StoreProduct> {
    @Select(" <script> " +
            "   SELECT id,image,store_name,slider_image,ot_price,stock,sales,price,activity,ficti,is_sub,store_info,browse,unit_name " +
            "   FROM s_product " +
            "   WHERE id = #{id} " +
            " </script>")
    StoreProduct getOne(@Param("id") Integer id);
}
