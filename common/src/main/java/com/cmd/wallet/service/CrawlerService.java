package com.cmd.wallet.service;

import com.cmd.wallet.common.mapper.ChangeConfigMapper;
import com.cmd.wallet.common.mapper.CoinMapper;
import com.cmd.wallet.common.model.ChangeConfig;
import com.cmd.wallet.common.model.Coin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class CrawlerService {
    private static Logger logger = LoggerFactory.getLogger(CrawlerService.class);

    @Autowired
    CoinMapper coinMapper;
    @Autowired
    ChangeConfigMapper changeConfigMapper;

    public void getMarketTick(){
        ChangeConfig changeUSD = changeConfigMapper.getChangeConfig(Coin.USD, Coin.CNY);
        if (changeUSD==null){
            logger.error("don't config USD rate");
            return;
        }

        Map<String, ExternalService.MarketDetail> market = ExternalService.getCoinnewAll();
        if (market==null){
            logger.error("ExternalService.getCoinnewAll error");
            return;
        }

        List<Coin> list = coinMapper.getCoinAll();
        for (Coin coin:list){
            ChangeConfig changeConfig = changeConfigMapper.getChangeConfig(coin.getName(), Coin.CNY);
            if (changeConfig!=null) {
                if (coin.getName() != null && coin.getName().trim().length() > 0) {
                    ExternalService.MarketDetail md = market.get(coin.getName());
                    if (md!=null) {
                        BigDecimal cnyRate = md.getClosePrice();
                        BigDecimal changeRate = md.getChangeRate();
                        if (cnyRate==null)
                            cnyRate = BigDecimal.ZERO;
                        if (changeRate == null)
                            changeRate = BigDecimal.ZERO;
                        changeConfigMapper.updateChangeConfig(new ChangeConfig().setId(changeConfig.getId()).setChangeRate(changeRate).setCnyRate(cnyRate));
                    }
                }
            }
        }
    }

    public void getUsdToCny(){
        ChangeConfig changeUSD = changeConfigMapper.getChangeConfig(Coin.USD, Coin.CNY);
        if (changeUSD==null){
            logger.error("don't config USD rate");
            return;
        }
        BigDecimal usdcny =  ExternalService.getUsd2Cny();
        changeConfigMapper.updateChangeConfig(new ChangeConfig().setId(changeUSD.getId()).setRate(usdcny));
    }
}
