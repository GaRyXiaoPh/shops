package com.cmd.wallet.common.mapper;


import com.cmd.wallet.common.model.SendCoin;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface SendCoinMapper {

    int add(SendCoin sendCoin);

    int updateTransferStatusById(@Param("id") Integer id, @Param("status") Integer status, @Param("txid") String txid);

    Page<SendCoin> getTransfer(@Param("userId") Integer userId, @Param("coinName") String coinName, @Param("status") Integer status, @Param("address")String address, RowBounds rowBounds);

    Page<SendCoin> getSendCoin(@Param("userId") Integer userId, @Param("coinName") String coinName, @Param("arr") Integer[] arr, @Param("address")String address, RowBounds rowBounds);

    List<SendCoin> getTransferUnconfirm(@Param("id") Integer id);

    SendCoin getTransferById(@Param("id") Integer id);

    SendCoin lockTransferById(@Param("id") Integer id);

}
