package com.cmd.wallet.service;

import com.cmd.wallet.common.mapper.ChangeConfigMapper;
import com.cmd.wallet.common.model.ChangeConfig;
import com.cmd.wallet.common.model.Coin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChangeConfigService {
    @Autowired
    private ChangeConfigMapper changeConfigMapper;

    public List<ChangeConfig> getChangeConfigList(){
        return changeConfigMapper.adminGetChangeConfigList();
    }
    public ChangeConfig getChangeConfigDetail(Integer id){
        return changeConfigMapper.getChangeConfigById(id);
    }
    public void updateChangeConfig(ChangeConfig changeConfig){
        changeConfigMapper.updateChangeConfig(changeConfig);
    }
    public int add(ChangeConfig changeConfig){
        return changeConfigMapper.add(changeConfig);
    }
    public int del(Integer id){
        return changeConfigMapper.del(id);
    }

    public ChangeConfig getChangeConfigById(Integer id){
        return changeConfigMapper.getChangeConfigById(id);
    }

    public Map<String, BigDecimal> getChangeConfig(){
        Map<String, BigDecimal> m = new HashMap<>();
        List<ChangeConfig> list = changeConfigMapper.adminGetChangeConfigList();
        list.forEach(config->{
            if (Coin.CNY.equalsIgnoreCase(config.getChangeName())) {
                m.put(config.getCoinName(), config.getRate());
            }
        });
        return m;
    }

    public ChangeConfig getCNYChangeConfig(String coinName){
        return changeConfigMapper.getChangeConfig(coinName, Coin.CNY);
    }
    public BigDecimal getChangeRate(String coinName, String toCoin){
        ChangeConfig config1 = changeConfigMapper.getChangeConfig(coinName, Coin.CNY);
        ChangeConfig config2 = changeConfigMapper.getChangeConfig(toCoin, Coin.CNY);
        if (config1!=null && config2!=null){
            return BigDecimal.valueOf(config1.getRate().doubleValue()/config2.getRate().doubleValue()).setScale(8, RoundingMode.FLOOR);
        }
        return BigDecimal.valueOf(1);
    }
}
