package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.UserWords;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserWordsMapper {

    int add(UserWords userWords);
    int updateUserWords(UserWords userWords);
    UserWords getUserWords(@Param("userId") Integer userId);
}
