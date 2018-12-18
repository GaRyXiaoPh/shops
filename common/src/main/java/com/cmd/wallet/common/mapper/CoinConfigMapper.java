package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.CoinConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CoinConfigMapper {
    int add(CoinConfig coinConfig);
    int updateCoinConfig(CoinConfig coinConfig);
    int delCoinConfig(@Param("coinName")String coinName);

    CoinConfig getCoinConfigByName(@Param("coinName") String coinName);
    List<CoinConfig> getCoinConfigList();
}
