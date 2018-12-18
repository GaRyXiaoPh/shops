package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.common.vo.UserCoinVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//积分服务和EthService对等
@Service
public class PointsService {
    private static Logger logger = LoggerFactory.getLogger(PointsService.class);

    @Autowired
    UserCoinService userCoinService;

    public String getAccountAddress(int userId, String coinName){
        UserCoinVO userCoin = userCoinService.getUserCoinByUserIdAndCoinName(userId, coinName);
        if (userCoin==null){
            userCoinService.addUserCoin(userId, coinName);
        }
        return "";
    }

    public String sendToAddress(int userId, String coinName, String toAddress, double amount){
        Assert.check(true, ErrorCode.ERR_NOT_SUPPORT_COIN);
        return "";
    }
}
