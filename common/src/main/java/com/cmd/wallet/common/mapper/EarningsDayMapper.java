package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.EarningsDay;
import com.cmd.wallet.common.model.UserWords;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

@Mapper
public interface EarningsDayMapper {

    int add(EarningsDay earningsDay);
    EarningsDay getEarningsDayByUserIdAndDay(@Param("userId")Integer userId, @Param("statDay")String statDay);
    int incrementRewardByUserIdAndDay(EarningsDay earningsDay);

    Page<EarningsDay> getEarningsDayList(@Param("userId")Integer userId, RowBounds rowBounds);
    int initEarningsDay();
}
