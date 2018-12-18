package com.cmd.wallet.service;

import com.cmd.wallet.common.mapper.UserBankMapper;
import com.cmd.wallet.common.model.UserBank;
import com.cmd.wallet.common.utils.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserBankService {
    @Autowired
    UserBankMapper userBankMapper;

    public int addUserBank(UserBank userBank){
        return userBankMapper.add(userBank);
    }
    public int updateUserBank(UserBank userBank){
        return userBankMapper.updateUserBank(userBank);
    }
    public int del(Integer userId, Integer id){
        return userBankMapper.del(userId, id);
    }
    public UserBank getUserBank(Integer id){
        return userBankMapper.getUserBank(id);
    }

    public List<UserBank> getUserBankList(Integer userId){
        return userBankMapper.getUserBankList(userId);
    }
}
