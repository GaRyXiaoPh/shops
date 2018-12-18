package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.UserCoin;
import com.cmd.wallet.common.vo.UserCoinVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.session.RowBounds;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface UserCoinMapper {
    @Select("select * from t_user_coin where user_id=#{userId} and coin_name = #{coinName} for update")
    UserCoin lockUserCoin(@Param("userId") int userId, @Param("coinName") String coinName);

    @Update("update t_user_coin set available_balance=available_balance + #{changeAvailableBalance}, freeze_balance=freeze_balance + #{changeFreezeBalance}," +
            "award_balance = award_balance + #{awardBalance}"
            + " where user_id=#{userId} and coin_name=#{coinName} and available_balance>=-#{changeAvailableBalance} and freeze_balance>=-#{changeFreezeBalance} " +
            "and award_balance >= -#{awardBalance}")
    int changeUserCoin(@Param("userId") int userId, @Param("coinName") String coinName, @Param("changeAvailableBalance") BigDecimal changeAvailableBalance,
                       @Param("changeFreezeBalance") BigDecimal changeFreezeBalance,@Param("awardBalance") BigDecimal awardBalance);


    @Select("select user_id from t_user_coin where coin_name=#{coinName} and bind_address=#{address}")
    Integer getUserIdByCoinNameAndAddress(@Param("coinName") String coinName, @Param("address") String address);


    int add(UserCoin userCoin);

    int updateUserCoinAddress(@Param("userId") Integer userId, @Param("coinName") String coinName, @Param("bindAddress") String bindAddress);

    UserCoinVO getUserCoinByUserIdAndCoinName(@Param("userId") Integer userId, @Param("coinName") String coinName);

    UserCoin getUserCoinByAddressAndCoinName(@Param("address") String address, @Param("coinName") String coinName);

    List<UserCoinVO> getUserCoinByUserId(@Param("userId") Integer userId);
    List<UserCoinVO> getCoinConfigList(@Param("coinNameList") List<String> coinName);

    @Select("select sum(available_balance+freeze_balance) from t_user_coin where coin_name=#{coinName}")
    BigDecimal getSumOfCoin(@Param("coinName") String coinName);

    @Select("select sum(available_balance) from t_user_coin where coin_name=#{coinName}")
    BigDecimal getSumOfAvailableCoin(@Param("coinName") String coinName);

    List<UserCoin> getUserCoinTask(@Param("id")Integer id, @Param("coinName")String coinName);

    @Select("select user_id as userId,award_balance as awardBalance,coin_name as coinName from t_user_coin where award_balance >0")
    List<UserCoin> getUserCoinAwardBalanceAll();
}
