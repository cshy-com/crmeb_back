package com.cshy.service.dao.giftCard;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cshy.common.model.entity.giftCard.GiftCardProduct;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface GiftCardProductDao extends BaseMapper<GiftCardProduct> {
    @Delete("<script>" +
            "   delete from b_gift_card_product " +
            "   where 1=1 " +
            "   <if test=\"null != idList and idList.size > 0\"> " +
            "       AND id in  " +
            "       <foreach collection=\"idList\" item=\"id\" open=\"(\" separator=\",\"  close=\")\"> " +
            "           #{id} " +
            "       </foreach> " +
            "   </if> " +
            " </script>")
    void batchDeleteByIds(@Param("idList") List<String> idList);

    @Update("<script>" +
            "   UPDATE b_gift_card_product " +
            "   SET is_del = 0 " +
            "   where product_id = #{id} " +
            " </script>")
    void restore(@Param("id") Integer id);
}
