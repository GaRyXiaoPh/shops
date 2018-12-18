package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.UserBillReason;
import com.cmd.wallet.common.mapper.ReceivedCoinMapper;
import com.cmd.wallet.common.model.ReceivedCoin;
import com.cmd.wallet.common.model.UserStat;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class ReceivedCoinService {
    private static final Logger logger = LoggerFactory.getLogger(SendCoinService.class);

    @Autowired
    private UserCoinService userCoinService;
    @Autowired
    ReceivedCoinMapper receivedCoinMapper;

    public int addReceivedCoin(ReceivedCoin receivedCoin){
        return receivedCoinMapper.add(receivedCoin);
    }

    @Transactional
    public void addTransaction(Integer userId, String coinName, String address, String txid, BigDecimal amount, BigDecimal fee, Integer txTime, Integer type, String fromAddress){
        int count = receivedCoinMapper.add(new ReceivedCoin().setUserId(userId).setAddress(address).setCoinName(coinName)
                .setTxid(txid).setAmount(amount).setFee(fee).setTxTime(txTime).setReceivedTime(new Date()).setStatus(1).setType(type).setFromAddress(fromAddress));
        if (count==0){
            throw new RuntimeException("addReceivedCoin failed,userId=" + userId + ",address=" + address);
        }
        userCoinService.changeUserCoin(userId, coinName, amount.subtract(fee), UserCoinService.ZERO,BigDecimal.ZERO, UserBillReason.BC_RECEIVED_COIN, "区块转账");
    }

    public List<String> getAllNotConfirmCoinTxids(String coinName) {
        return receivedCoinMapper.getAllNotConfirmCoinTxids(coinName);
    }

    public boolean isTransactionExist(String coinName, String txid){
        return  receivedCoinMapper.getReceivedCoinByTxid(coinName, txid)!=null;
    }

    public Page<ReceivedCoin> getTransferList(Integer userId, String coinName, int pageNo, int pageSize){
        return receivedCoinMapper.getReceivedCoin(userId, coinName, new RowBounds(pageNo, pageSize));
    }

    public ReceivedCoin getNextReceiveFromExternal(Integer id, String coinName){
        return receivedCoinMapper.getNextReceiveFromExternal(id, coinName);
    }

}
