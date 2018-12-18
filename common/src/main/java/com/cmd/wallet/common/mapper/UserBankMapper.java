package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.Task;
import com.cmd.wallet.common.model.UserBank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserBankMapper {

    int add(UserBank userBank);
    int updateUserBank(UserBank userBank);
    int del(@Param("userId")Integer userId, @Param("id")Integer id);
    UserBank getUserBank(Integer id);
    UserBank getOneUserBankByUserId(Integer userId);
    List<UserBank> getUserBankList(Integer userId);
}
