package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.TransferAddress;
import com.cmd.wallet.common.model.UserWords;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Mapper
public interface TransferAddressMapper {

    int add(TransferAddress transferAddress);
    int del(@Param("id")Integer id);
    int updateTransferAddress(TransferAddress transferAddress);
    TransferAddress getTransferAddressById(@Param("id")Integer id);
    List<TransferAddress> getTransferAddressList(@Param("userId") Integer userId, @Param("coinName")String coinName);
}
