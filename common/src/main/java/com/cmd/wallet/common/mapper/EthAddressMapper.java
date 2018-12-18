package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.EthAddress;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EthAddressMapper {
    @Insert("insert into t_eth_address(user_id, address, `password`, fileName, credentials)"+
            "values(#{userId},#{address},#{password},#{fileName},#{credentials})")
    int add(EthAddress ethAddress);

    @Select("select * from t_eth_address where user_id=#{userId}")
    EthAddress getEthAddressByUserId(@Param("userId") Integer userId);

    @Select("select * from t_eth_address where address=#{address}")
    EthAddress getEthAddressByAddress(@Param("address")String address);
}
