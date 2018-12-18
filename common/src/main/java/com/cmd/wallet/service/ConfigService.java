package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.ConfigKey;
import com.cmd.wallet.common.mapper.ConfigLevelMapper;
import com.cmd.wallet.common.mapper.ConfigMapper;
import com.cmd.wallet.common.model.Config;
import com.cmd.wallet.common.model.ConfigLevel;
import com.cmd.wallet.common.vo.ConfigVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class ConfigService {
    private static Log log = LogFactory.getLog(ConfigService.class);

    @Autowired
    private ConfigMapper configMapper;
    @Autowired
    private ConfigLevelMapper configLevelMapper;

    @Transactional
    public void updateConfigValue(Map<String, String> values) {
        for (String key : values.keySet()) {
            setConfigValue(key, values.get(key));
        }
    }

    public void setConfigValue(String name, String value) {
        int row = configMapper.updateConfigValue(name, value);
        if (row == 0) {
            configMapper.insertConfig(new Config().setConfName(name).setConfValue(value).setComment(""));
        }
    }


    public Config getConfigById(Integer id){
        return configMapper.getConfigById(id);
    }

    /**
     * 获取配置值，如果值不存在，返回默认值
     *
     * @param name         属性名
     * @param defaultValue 默认值
     * @return 配置值/默认值
     */
    public String getConfigValue(String name, String defaultValue) {
        Config conf = configMapper.getConfigByName(name);
        if (conf == null) {
            return defaultValue;
        }
        return conf.getConfValue();
    }

    /**
     * 获取配置值，如果值不存在，返回默认值
     *
     * @param name         属性名
     * @param defaultValue 默认值
     * @param isPercent    是否是百分比，如果是百分比，那么小于1的时候，会除以100返回
     * @return 配置值/默认值
     */
    public BigDecimal getConfigValue(String name, BigDecimal defaultValue, boolean isPercent) {
        BigDecimal value;
        Config conf = configMapper.getConfigByName(name);
        if (conf == null) {
            value = defaultValue;
        } else {
            value = new BigDecimal(conf.getConfValue());
        }
        if (isPercent) {
            if (value.compareTo(BigDecimal.ONE) >= 0) {
                value = value.divide(new BigDecimal(100), 8, RoundingMode.HALF_UP);
            }
        }
        return value;
    }

    public int getConfigValue(String name, int defaultValue) {
        Config conf = configMapper.getConfigByName(name);
        if (conf == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(conf.getConfValue());
        } catch (NumberFormatException ex) {
            log.error("", ex);
            return defaultValue;
        }
    }

    public BigInteger getConfigValue(String name, BigInteger defaultValue) {
        Config conf = configMapper.getConfigByName(name);
        if (conf == null) {
            return defaultValue;
        }
        try {
            return new BigInteger(conf.getConfValue());
        } catch (NumberFormatException ex) {
            log.error("", ex);
            return defaultValue;
        }
    }

    //获取配置值，如果值不存在，返回null
    public String getConfigValue(String name) {
        return getConfigValue(name, (String) null);
    }

    public List<Config> getConfigList() {
        return configMapper.getConfigList();
    }

    ///////////////////////////////////////////////////////////
    public String getPlatformCoinName() { return getConfigValue(ConfigKey.PLATFORM_COIN_NAME, "BSTS"); }
    public Integer getExpireMinute() { return getConfigValue(ConfigKey.EXPIRE_MINUTE, 5*24*60); }
    public Integer getRegisterReward() { return getConfigValue(ConfigKey.REGISTER_REWARD, 5); }
    public double getReferrerReward() { return  getConfigValue(ConfigKey.REFERRER_REWARD, BigDecimal.valueOf(1), false).doubleValue(); }
    public double getMinerReward() { return getConfigValue(ConfigKey.MINER_REWARD, BigDecimal.valueOf(1), false).doubleValue(); }
    public Integer getCommunityDelay() {return getConfigValue(ConfigKey.COMMUNITY_DELAY, 180);}
    public double getInvestMax() { return getConfigValue(ConfigKey.USER_INVEST_MAX, BigDecimal.valueOf(2000), false).doubleValue(); }
    public double getInvestMin() { return getConfigValue(ConfigKey.USER_INVEST_MIN, BigDecimal.valueOf(1), false).doubleValue(); }
    public Integer getMallGoodMax() { return  getConfigValue(ConfigKey.MALL_GOOD_MAX, 8); }
    public String getCommunityConsumeCoinName() { return getConfigValue(ConfigKey.COMMUNTIY_CONSUME_COIN_NAME, "ENG11"); }
    public double getDisconveryReward() { return getConfigValue(ConfigKey.DISCOVERY_REWARD, BigDecimal.valueOf(3),false).doubleValue(); }
    public double getRewardFreezeRate() { return getConfigValue(ConfigKey.REWARD_FREEZE_RATE, BigDecimal.valueOf(0.9), false).doubleValue(); }
    public String getDefaultHeadImage() { return getConfigValue(ConfigKey.DEFAULT_HEAD_IMAGE, "http://kuanggongzhijia.oss-ap-southeast-1.aliyuncs.com/1541673037921_contact_head_icon.png"); }

    public ConfigVo getSystemConfig() {
        ConfigVo configVo = new ConfigVo();
        configVo.setReferrerReward(this.getConfigValue(ConfigKey.REFERRER_REWARD));
        configVo.setRegisterReward(this.getConfigValue(ConfigKey.REGISTER_REWARD));
        configVo.setExpireTime(this.getConfigValue(ConfigKey.EXPIRE_MINUTE));
        configVo.setMinerReward(this.getConfigValue(ConfigKey.MINER_REWARD));
        configVo.setConfigLevels(configLevelMapper.getConfigList());
        configVo.setUserMaxInvest(this.getConfigValue(ConfigKey.USER_INVEST_MAX));
        configVo.setUserMinInvest(this.getConfigValue(ConfigKey.USER_INVEST_MIN));
        return configVo;
    }

    public void setSystemConfig(ConfigVo config) {
        this.setConfigValue(ConfigKey.REFERRER_REWARD, config.getReferrerReward());
        this.setConfigValue(ConfigKey.REGISTER_REWARD, config.getRegisterReward());
        this.setConfigValue(ConfigKey.MINER_REWARD,config.getMinerReward());
        this.setConfigValue(ConfigKey.EXPIRE_MINUTE,config.getExpireTime());
        this.setConfigValue(ConfigKey.USER_INVEST_MAX,config.getUserMaxInvest());
        this.setConfigValue(ConfigKey.USER_INVEST_MIN,config.getUserMinInvest());
        List<ConfigLevel> configLevels = config.getConfigLevels();
        if (configLevels != null) {
            for (ConfigLevel configLevel : configLevels) {
                ;//configLevelMapper.updateLevelConfig(configLevel.getLevel(), configLevel.getRate());
            }
        }
    }
}
