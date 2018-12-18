package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.Coin;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CoinMapper {

    int add(Coin coin);
    int updateCoin(Coin coin);
    int deleteCoin(@Param("coinName") String coinName);

    Coin getCoinById(@Param("id")Integer id);
    Coin getCoinByName(@Param("coinName") String coinName);
    List<Coin> getCoin();
    List<Coin> getCoinAll();

    List<String> getCoinWallet();
    List<Coin> getCoinByWallet(@Param("displayName") String displayName);

    @Select("select * from t_coin where category='eth' or category='token'")
    List<Coin> getAllEthCoins();

    @Select("select * from t_coin where category=#{category}")
    List<Coin> getCoinsByCategory(String category);
}
