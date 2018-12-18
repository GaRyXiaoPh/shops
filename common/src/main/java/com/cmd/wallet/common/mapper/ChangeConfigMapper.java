package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.ChangeConfig;
import com.cmd.wallet.common.model.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChangeConfigMapper {

    int add(ChangeConfig changeConfig);
    int del(@Param("id")Integer id);
    int updateChangeConfig(ChangeConfig changeConfig);

    ChangeConfig getChangeConfig(@Param("coinName")String coinName, @Param("changeName")String changeName);
    List<ChangeConfig> adminGetChangeConfigList();
    List<ChangeConfig> getChangeConfigList();

    ChangeConfig getChangeConfigById(@Param("id") Integer id);
}
