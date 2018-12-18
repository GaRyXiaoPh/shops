package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.ConfigKey;
import com.cmd.wallet.common.constants.UserBillReason;
import com.cmd.wallet.common.mapper.ConfigMapper;
import com.cmd.wallet.common.mapper.UserBillMapper;
import com.cmd.wallet.common.mapper.UserCoinMapper;
import com.cmd.wallet.common.model.Config;
import com.cmd.wallet.common.model.UserBill;
import com.cmd.wallet.common.model.UserCoin;
import com.cmd.wallet.common.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
@Slf4j
public class ReleaseAwardService {

    @Autowired
    private UserCoinMapper userCoinMapper;

    @Autowired
    private UserBillMapper userBillMapper;

    @Autowired
    private ConfigMapper configMapper;

    public  static boolean relaseBool =true;
    @Transactional
    public void  releaseAwardBanlance() {
        try {
            relaseBool = false;
            Config config = configMapper.getConfigByName(ConfigKey.USER_BUY_REBATE_RELEASE_RATE);
            String rateStr = config.getConfValue();
            if (rateStr.equals("0")) {
                log.info("this config release is zero,not need realse");
                return;
            }
            BigDecimal releaseRate = new BigDecimal(rateStr).divide(new BigDecimal(100), 8, BigDecimal.ROUND_HALF_UP);
            Config minConfig = configMapper.getConfigByName(ConfigKey.MIN_RELEASE_NUM);
            BigDecimal minReleaseNum = new BigDecimal(minConfig.getConfValue());
            List<UserCoin> userCoinList = userCoinMapper.getUserCoinAwardBalanceAll();
            if (userCoinList == null || userCoinList.size() == 0) {
                Integer currentTime =   (int)(DateUtil.getDateByString("00:00:00").getTime()/1000);
                configMapper.updateConfigValue(ConfigKey.LAST_RELEASE_TIME,currentTime+"");
                log.info("no coin can release");
                return;
            }

            BigDecimal releasNum = new BigDecimal(0);
            for (UserCoin userCoin : userCoinList) {
                if (userCoin.getAwardBalance().compareTo(minReleaseNum) > 0) {
                    releasNum = userCoin.getAwardBalance().multiply(releaseRate).setScale(8, BigDecimal.ROUND_HALF_UP);
                } else {
                    releasNum = userCoin.getAwardBalance().setScale(8,BigDecimal.ROUND_HALF_UP);
                }
                int i = userCoinMapper.changeUserCoin(userCoin.getUserId(), userCoin.getCoinName(), releasNum,
                        BigDecimal.ZERO.setScale(8,BigDecimal.ROUND_HALF_UP), releasNum.multiply(new BigDecimal(-1)).setScale(8,BigDecimal.ROUND_HALF_UP));
                //增加记录
                userBillMapper.insertUserBill(userCoin.getUserId(), userCoin.getCoinName(), UserBill.SUB_TYPE_AWARD, UserBillReason.MALL_RELEASE_REWARD, releasNum, "促销冻结金额释放");
            }
            Integer currentTime =   (int)(DateUtil.getDateByString("00:00:00").getTime()/1000);
            configMapper.updateConfigValue(ConfigKey.LAST_RELEASE_TIME,currentTime+"");
            relaseBool = true;
            log.info(" release  end --------");
        }catch (Exception e){
            relaseBool = true;
            log.error("release exception is :",e);
        }
    }

}
