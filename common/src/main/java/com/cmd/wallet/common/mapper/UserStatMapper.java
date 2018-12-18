package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.UserStat;
import com.cmd.wallet.common.model.UserTask;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserStatMapper {

    int add(UserStat userStat);
    int updateUserStat(UserStat userStat);
    UserStat getUserStat(@Param("userId")Integer userId);
    UserStat getNextUserStat(@Param("id") Integer id);
    int incrementByUserId(UserStat userStat);
}
