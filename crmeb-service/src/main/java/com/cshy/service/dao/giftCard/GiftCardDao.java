package com.cshy.service.dao.giftCard;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cshy.common.model.entity.giftCard.GiftCard;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface GiftCardDao extends BaseMapper<GiftCard> {
    @Select(" <script> " +
            "   select * from b_gift_card where id = #{id} " +
            "   <if test=\"isDel == 0\"> " +
            "       AND is_del = 0 " +
            "   </if> " +
            " </script>")
    GiftCard getById(@Param("id") String id, @Param("isDel") Boolean isDel);
}
