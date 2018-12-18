package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.EarningsDay;
import com.cmd.wallet.common.model.UserEarnings;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

@Mapper
public interface UserEarningsMapper {

    int add(UserEarnings userEarnings);
    int updateUserEarnings(UserEarnings userEarnings);
    UserEarnings getUserEarningsByUserId(@Param("userId") Integer userId);

    int incrementRewardByUserId(UserEarnings userEarnings);
}
