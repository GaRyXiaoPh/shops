package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.ReceivedCoin;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface ReceivedCoinMapper {

    @Insert("insert into t_received_coin(user_id, address, coin_name, txid, amount, fee, tx_time, received_time, status, type, from_address)"+
            "values(#{userId}, #{address},#{coinName},#{txid},#{amount},#{fee},#{txTime},NOW(),1, #{type}, #{fromAddress})")
    int add(ReceivedCoin receivedCoin);

    @Select("select * from t_received_coin where coin_name=#{coinName} and txid=#{txid} limit 0,1")
    ReceivedCoin getReceivedCoinByTxid(@Param("coinName")String coinName, @Param("txid")String txid);

    @Select("select txid from t_received_coin where coin_name=#{coinName} and status < 0 and left(txid, 6) != 'inner-'")
    List<String> getAllNotConfirmCoinTxids(@Param("coinName") String coinName);

    Page<ReceivedCoin> getReceivedCoin(@Param("userId")Integer userId, @Param("coinName")String coinName, RowBounds rowBounds);

    ReceivedCoin getNextReceiveFromExternal(@Param("id")Integer id, @Param("coinName")String coinName);
}
