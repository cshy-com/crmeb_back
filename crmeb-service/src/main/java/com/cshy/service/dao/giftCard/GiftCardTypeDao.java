package com.cshy.service.dao.giftCard;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cshy.common.model.entity.giftCard.GiftCardType;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface GiftCardTypeDao extends BaseMapper<GiftCardType> {
    @Select(" <script> " +
            "   select * from b_gift_card_type where id = #{id} " +
            "   <if test=\"isDel == 0\"> " +
            "       AND is_del = 0 " +
            "   </if> " +
            " </script>")
    GiftCardType getById(@Param("id") String id, @Param("isDel") Boolean isDel);
}
