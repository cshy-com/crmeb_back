package com.cshy.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cshy.common.model.entity.product.StoreProductAttrValue;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 商品属性值表 Mapper 接口

 */
public interface StoreProductAttrValueDao extends BaseMapper<StoreProductAttrValue> {
    @Select(" <script> " +
            "   select * from s_product_attr_value where id = #{id} " +
            "   <if test=\"isDel == 0\"> " +
            "       AND is_del = 0 " +
            "   </if> " +
            " </script>")
    StoreProductAttrValue getById(@Param("id") Integer id, @Param("isDel") Boolean isDel);
}
