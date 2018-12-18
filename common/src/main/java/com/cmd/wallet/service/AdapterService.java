package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.CoinCategory;
import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.model.Coin;
import com.cmd.wallet.common.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdapterService {
    private static Logger logger = LoggerFactory.getLogger(AdapterService.class);

    @Autowired
    BitcoinService bitcoinService;
    @Autowired
    EthService ethService;
    @Autowired
    CoinService coinService;
    @Autowired
    PointsService pointsService;
    @Autowired
    EosService eosService;
    @Autowired
    UsdtService usdtService;


    @Transactional
    public String getAccountAddress(int userId, String coinName){
        Coin coin = coinService.getCoinByName(coinName);
        try {
            switch (coin.getCategory()){
                case CoinCategory.BTC: return bitcoinService.getAccountAddress(userId, coinName);
                case CoinCategory.ETH: return ethService.getAccountAddress(userId, coinName);
                case CoinCategory.TOKEN: return ethService.getAccountAddress(userId, coinName);
                case CoinCategory.POINTS: return pointsService.getAccountAddress(userId, coinName);
                case CoinCategory.EOS: return eosService.getAccountAddress(userId, coinName);
                case CoinCategory.USDT: return usdtService.getAccountAddress(userId, coinName);
                default: Assert.check(true, ErrorCode.ERR_NOT_SUPPORT_COIN);
            }
        } catch (Exception e) {
            logger.error("failed to get coin: {} address for user: {}",coinName, userId,e);
            Assert.check(true, ErrorCode.ERR_WALLET_ERROR);
        }

        return null;
    }

    public String sendToAddress(int userId, String coinName, String toAddress, double amount, String comment){
        Coin coin = coinService.getCoinByName(coinName);
        switch (coin.getCategory()) {
            case CoinCategory.BTC: return bitcoinService.sendToAddress(userId, coinName, toAddress, amount);
            case CoinCategory.ETH: return ethService.sendToAddress(userId, coinName, toAddress, amount);
            case CoinCategory.TOKEN: return ethService.sendToAddress(userId, coinName, toAddress, amount);
            case CoinCategory.POINTS: return pointsService.sendToAddress(userId, coinName, toAddress, amount);
            case CoinCategory.EOS: return eosService.sendToAddress(userId, coinName, toAddress, amount, comment);
            case CoinCategory.USDT: return usdtService.sendToAddress(userId, coinName, toAddress, amount);
            default: Assert.check(true, ErrorCode.ERR_NOT_SUPPORT_COIN);
        }
        return null;
    }

    public int getTxConfirmCount(String coinName, String txid){
        Coin coin = coinService.getCoinByName(coinName);
        switch (coin.getCategory()){
            case CoinCategory.BTC: return bitcoinService.getTxConfirmCount(coinName, txid);
            case CoinCategory.ETH: return ethService.getTxConfirmCount(coinName, txid);
            case CoinCategory.TOKEN: return ethService.getTxConfirmCount(coinName, txid);
            case CoinCategory.POINTS: return 0;
            case CoinCategory.EOS: return 0;//eosService.getTxConfirmCount(coinName, txid);
            case CoinCategory.USDT: return usdtService.getTxConfirmCount(coinName, txid);
            default: Assert.check(true, ErrorCode.ERR_NOT_SUPPORT_COIN);
        }
        return 0;
    }

    public boolean isAddressValid(String coinName, String address) {
        Coin coin = coinService.getCoinByName(coinName);
        switch (coin.getCategory()){
            case CoinCategory.BTC: return bitcoinService.isAddressValid(coinName, address);
            case CoinCategory.ETH: return ethService.isAddressValid(coinName, address);
            case CoinCategory.TOKEN: return ethService.isAddressValid(coinName, address);
            case CoinCategory.POINTS: return false;
            case CoinCategory.EOS: return eosService.isAddressValid(coinName, address);
            case CoinCategory.USDT: return usdtService.isAddressValid(coinName, address);
            default: Assert.check(true, ErrorCode.ERR_NOT_SUPPORT_COIN);
        }
        return false;
    }

}
