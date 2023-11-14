package com.cshy.service.dao;

import com.cshy.common.model.entity.user.UserAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户地址表 Mapper 接口

 */
public interface UserAddressDao extends BaseMapper<UserAddress> {
    @Select(" <script> " +
            "   select * from u_address where id = #{id} " +
            "   <if test=\"isDel == 0\"> " +
            "       AND is_del = 0 " +
            "   </if> " +
            " </script>")
    UserAddress getById(@Param("id") Integer addressId, @Param("isDel") Boolean isDel);
}
