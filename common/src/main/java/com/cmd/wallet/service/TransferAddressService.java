package com.cmd.wallet.service;

import com.cmd.wallet.blockchain.bitcoin.BitcoindRpcClient;
import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.mapper.CoinMapper;
import com.cmd.wallet.common.mapper.TransferAddressMapper;
import com.cmd.wallet.common.model.Coin;
import com.cmd.wallet.common.model.TransferAddress;
import com.cmd.wallet.common.utils.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransferAddressService {
    @Autowired
    TransferAddressMapper transferAddressMapper;
    @Autowired
    CoinMapper coinMapper;

    public void addTransferAddress(Integer userId, TransferAddress transferAddress){
        transferAddress.setUserId(userId);
        transferAddressMapper.add(transferAddress);
    }

    public void delTransferAddress(Integer userId, Integer id){
        TransferAddress transferAddress = transferAddressMapper.getTransferAddressById(id);
        Assert.check(transferAddress.getUserId().intValue()!=userId.intValue(), ErrorCode.ERR_PARAM_ERROR);
        transferAddressMapper.del(id);
    }

    public void updateTransferAddress(Integer userId, TransferAddress transferAddress){
        Assert.check(userId.intValue()!=transferAddress.getId().intValue(), ErrorCode.ERR_PARAM_ERROR);
        transferAddressMapper.updateTransferAddress(transferAddress);
    }

    public List<TransferAddress> getTransferAddressList(Integer userId, String coinName){
        List<TransferAddress> list = transferAddressMapper.getTransferAddressList(userId, coinName);
        for (TransferAddress address:list){
            Coin coin = coinMapper.getCoinByName(address.getCoinName());
            if (coin!=null)
                address.setIcon(coin.getIcon());
        }
        return list;
    }
}
