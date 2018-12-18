package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.UserTask;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;

@Mapper
public interface UserTaskMapper {
    @Insert("insert into t_user_task(type, user_id, params, status)values(#{type}, #{userId}, #{params}, #{status})")
    int add(@Param("type") Integer type, @Param("userId")Integer userId, @Param("params") String params, @Param("status") Integer status);

    @Delete("delete from t_user_task where id=#{id}")
    int del(@Param("id") Integer id);

    @Select("select * from t_user_task where `type`=#{type} order by id asc limit 0, 1")
    UserTask getUserTask(@Param("type") Integer type);
}
