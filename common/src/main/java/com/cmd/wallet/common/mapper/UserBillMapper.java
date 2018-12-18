package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.vo.UserBillVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;

import java.math.BigDecimal;

@Mapper
public interface UserBillMapper {
    @Insert("insert into t_user_bill(user_id,coin_name,sub_type,reason,change_amount,last_time,comment)"
            +"values(#{userId},#{coinName},#{subType},#{reason},#{changeAmount},NOW(),#{comment})")
    int insertUserBill(@Param("userId") int userId, @Param("coinName") String coinName, @Param("subType") int subType,
                       @Param("reason") String reason, @Param("changeAmount") BigDecimal changeAmount, @Param("comment") String comment);

    Page<UserBillVO> getUserBill(@Param("userId") Integer userId,RowBounds rowBounds);

    Page<UserBillVO> getUserBillByReason(@Param("userId")Integer userId, @Param("coinName")String coinName, @Param("array")String[]array, RowBounds rowBounds);
    Page<UserBillVO> getUserBillByReason2(@Param("userId")Integer userId, @Param("coinName")String coinName, @Param("array")String[]array, @Param("userName")String userName,RowBounds rowBounds);
}
