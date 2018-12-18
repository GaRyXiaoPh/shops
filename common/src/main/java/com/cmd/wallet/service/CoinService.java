package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.mapper.CoinConfigMapper;
import com.cmd.wallet.common.mapper.CoinMapper;
import com.cmd.wallet.common.model.Coin;
import com.cmd.wallet.common.model.CoinConfig;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.common.vo.CoinVO;
import com.github.pagehelper.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CoinService {
    @Autowired
    private CoinMapper coinMapper;
    @Autowired
    private CoinConfigMapper coinConfigMapper;

    @Transactional
    public int add(CoinVO coinVO) {
        Coin tmp = coinMapper.getCoinByName(coinVO.getName());
        Assert.check(tmp != null, ErrorCode.ERR_RECORD_EXIST, "");
        CoinConfig coinConfig = new CoinConfig();
        coinConfig.setCoinName(coinVO.getName()).setTransferFeeRate(coinVO.getTransferFeeRate())
                .setTransferMinAmount(coinVO.getTransferMinAmount())
                .setTransferFeeSelect(coinVO.getTransferFeeSelect())
                .setTransferFeeStatic(coinVO.getTransferFeeStatic())
                .setTransferMaxAmount(coinVO.getTransferMaxAmount()).setTransferEnable(coinVO.getTransferEnable())
                .setWithdrawMaxAmount(coinVO.getWithdrawMaxAmount()).setWithdrawMinAmount(coinVO.getWithdrawMinAmount())
                .setGatherEnable(coinVO.getGatherEnable()).setGatherMin(coinVO.getGatherMin()).setWithdrawFeeRate(coinVO.getWithdrawFeeRate());
        coinConfigMapper.add(coinConfig);
        Coin coin = new Coin();
        BeanUtils.copyProperties(coinVO, coin);
        return coinMapper.add(coin);
    }

    @Transactional
    public int updateCoin(CoinVO coinVO) {
        Coin tmp = coinMapper.getCoinByName(coinVO.getName());
        Assert.check(tmp == null, ErrorCode.ERR_RECORD_NOT_EXIST, "");

        CoinConfig coinConfig = new CoinConfig();
        coinConfig.setCoinName(coinVO.getName()).setTransferFeeRate(coinVO.getTransferFeeRate())
                .setTransferMinAmount(coinVO.getTransferMinAmount())
                .setTransferMaxAmount(coinVO.getTransferMaxAmount())
                .setTransferFeeSelect(coinVO.getTransferFeeSelect())
                .setTransferFeeStatic(coinVO.getTransferFeeStatic())
                .setTransferEnable(coinVO.getTransferEnable())
                .setWithdrawMinAmount(coinVO.getWithdrawMinAmount())
                .setWithdrawMaxAmount(coinVO.getWithdrawMaxAmount())
                .setGatherEnable(coinVO.getGatherEnable()).setGatherMin(coinVO.getGatherMin()).setTransferEnable(coinVO.getTransferEnable())
                .setWithdrawFeeRate(coinVO.getWithdrawFeeRate());
        coinConfigMapper.updateCoinConfig(coinConfig);
        Coin coin = new Coin();
        BeanUtils.copyProperties(coinVO, coin);
        coin.setId(tmp.getId()).setName(tmp.getName());
        return coinMapper.updateCoin(coin);
    }

    public Coin getCoinById(Integer id){
        Coin coin = coinMapper.getCoinById(id);
        return coin;
    }

    public void updateCoinStatus(String name, int status) {
        Coin coin = coinMapper.getCoinByName(name);
        Assert.check(coin == null, ErrorCode.ERR_RECORD_NOT_EXIST, "");

        if (status == Coin.COIN_NORMAL) {
            coinMapper.updateCoin(new Coin().setId(coin.getId()).setStatus(status));
        } else if (status == Coin.COIN_DISABLE) {
            coinMapper.updateCoin(new Coin().setId(coin.getId()).setStatus(status));
        } else {
            Assert.check(true, ErrorCode.ERR_PARAM_ERROR, "");
        }
    }

    public void deleteCoin(String name) {
        Coin coin = coinMapper.getCoinByName(name);
        Assert.check(coin == null, ErrorCode.ERR_RECORD_NOT_EXIST, "");

        coinMapper.deleteCoin(name);
        coinConfigMapper.delCoinConfig(coin.getName());
    }

    public Coin getCoinByName(String coinName) {
        return coinMapper.getCoinByName(coinName);
    }

    public List<Coin> getCoin() {
        return coinMapper.getCoin();
    }
    public List<Coin> getCoinAll() {
        return coinMapper.getCoinAll();
    }

    public CoinVO getCoinInfo(String coinName) {
        Coin coin = coinMapper.getCoinByName(coinName);
        if (coin == null) {
            return null;
        }

        CoinVO vo = new CoinVO();
        BeanUtils.copyProperties(coin, vo);
        CoinConfig coinConfig = coinConfigMapper.getCoinConfigByName(coinName);
        if (coinConfig != null) {
            BeanUtils.copyProperties(coinConfig, vo);
        }

        return vo;
    }


    //获取某个列表的所有币
    public List<Coin> getCoinsByCategory(String category) {
        return coinMapper.getCoinsByCategory(category);
    }

    //获取所有以太坊和以太坊代币
    public List<Coin> getAllEthCoins() {
        return coinMapper.getAllEthCoins();
    }

    //获取钱包类型
    public List<String> getCoinWallet() { return coinMapper.getCoinWallet(); }
    public List<Coin> getCoinByWallet(String displayName) { return coinMapper.getCoinByWallet(displayName); }
}
