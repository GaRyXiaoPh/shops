package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.mapper.EthAddressMapper;
import com.cmd.wallet.common.mapper.UserBillMapper;
import com.cmd.wallet.common.mapper.UserCoinMapper;
import com.cmd.wallet.common.model.EthAddress;
import com.cmd.wallet.common.model.UserBill;
import com.cmd.wallet.common.model.UserCoin;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.common.vo.UserCoinVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserCoinService {
    public static final BigDecimal ZERO  = BigDecimal.ZERO;

    @Autowired
    private UserCoinMapper userCoinMapper;
    @Autowired
    private EthAddressMapper ethAddressMapper;
    @Autowired
    private UserBillMapper userBillMapper;

    /**
     * 改变用户金额
     * @param userId                       用户id
     * @param coinName                     币种
     * @param changeAvailableBalance     可用币变化数，可以是负数或0
     * @param changeFreezeBalance        冻结币变化数，可以是负数或0
     * @param awardBalance                 促销奖励的变化数目 可以是负数或0
     * @param reason                        变化原因，在UserBillReason总定义
     * @param logComment                   账单日志
     */
    @Transactional
    public void changeUserCoin(int userId, String coinName, BigDecimal changeAvailableBalance, BigDecimal changeFreezeBalance,BigDecimal awardBalance, String reason, String logComment) {
        int changeAvailable = changeAvailableBalance.compareTo(ZERO);
        int changeFreeze = changeFreezeBalance.compareTo(ZERO);
        int awardNum = awardBalance.compareTo(ZERO);
        if(changeAvailable == 0 && changeFreeze == 0 && awardNum ==0){
            return;
        }
        // 调整精度
        if(changeAvailableBalance.scale() > 8) changeAvailableBalance = changeAvailableBalance.setScale( 8, BigDecimal.ROUND_HALF_UP);
        if(changeFreezeBalance.scale() > 8) changeFreezeBalance = changeFreezeBalance.setScale( 8, BigDecimal.ROUND_HALF_UP);
        if(awardBalance.scale()>8) awardBalance = awardBalance.setScale(8,BigDecimal.ROUND_HALF_UP);
        int changeCount = userCoinMapper.changeUserCoin(userId, coinName, changeAvailableBalance, changeFreezeBalance,awardBalance);
        if(changeCount == 0) {
            if (userCoinMapper.getUserCoinByUserIdAndCoinName(userId, coinName)==null) {
                // 没有钱包，手工创建一个新的钱包
                UserCoin coin = new UserCoin();
                coin.setCoinName(coinName);
                coin.setUserId(userId);
                coin.setAvailableBalance(BigDecimal.ZERO);
                coin.setFreezeBalance(BigDecimal.ZERO);
                userCoinMapper.add(coin);
            } else {
                Assert.check(changeCount != 1, ErrorCode.ERR_BALANCE_INSUFFICIENT);
            }
            // 重新修改钱包
            changeCount = userCoinMapper.changeUserCoin(userId, coinName, changeAvailableBalance, changeFreezeBalance,awardBalance);
        }
        Assert.check(changeCount != 1, ErrorCode.ERR_BALANCE_INSUFFICIENT);

        // 增加修改日志
        if(changeAvailable != 0) {
            userBillMapper.insertUserBill(userId, coinName, UserBill.SUB_TYPE_AVAILABLE, reason, changeAvailableBalance, logComment);
        }
        // 增加修改日志
        if(changeFreeze != 0) {
            userBillMapper.insertUserBill(userId, coinName, UserBill.SUB_TYPE_FREEZE, reason, changeFreezeBalance, logComment);
        }
        //增加促销奖励的信息
        if(awardNum !=0){
            userBillMapper.insertUserBill(userId,coinName,UserBill.SUB_TYPE_AWARD,reason,awardBalance,logComment);
        }
    }






    //添加新的币钱包
    @Transactional
    public void addUserCoin(int userId, String coinName){
        UserCoinVO userCoin = userCoinMapper.getUserCoinByUserIdAndCoinName(userId, coinName);
        if (userCoin==null){
            userCoinMapper.add(new UserCoin().setUserId(userId).setCoinName(coinName));
        }
    }

    public void addUserCoin(int userId, String coinName, String address) {
        UserCoinVO userCoin = userCoinMapper.getUserCoinByUserIdAndCoinName(userId, coinName);
        if (userCoin==null){
            userCoinMapper.add(new UserCoin().setUserId(userId).setCoinName(coinName).setBindAddress(address));
        }
    }

    public void updateUserCoinAddress(int userId, String coinName, String address) {
        UserCoinVO userCoin = userCoinMapper.getUserCoinByUserIdAndCoinName(userId, coinName);
        if (userCoin!=null) {
            userCoinMapper.updateUserCoinAddress(userId, coinName, address);
        }
    }

    public Integer getUserIdByCoinNameAndAddress(String coinName, String address) {
        return userCoinMapper.getUserIdByCoinNameAndAddress(coinName, address);
    }

    public UserCoin getUserCoinByCoinNameAndAddress(String coinName, String address){
        return userCoinMapper.getUserCoinByAddressAndCoinName(address, coinName);
    }

    public UserCoinVO getUserCoinByUserIdAndCoinName(int userId, String coinName){
        return userCoinMapper.getUserCoinByUserIdAndCoinName(userId, coinName);
    }

    public List<UserCoinVO> getUserCoinByUserId(Integer userId) {
        return userCoinMapper.getUserCoinByUserId(userId);
    }

    public int addEthAddress(EthAddress ethAddress){
        return ethAddressMapper.add(ethAddress);
    }

    public EthAddress getEthAddressByUserId(int userId){
        return ethAddressMapper.getEthAddressByUserId(userId);
    }

    public EthAddress getEthAddressByAddress(String address){
        return  ethAddressMapper.getEthAddressByAddress(address);
    }

    public BigDecimal getSumOfCoin(String coinName) {
        return userCoinMapper.getSumOfCoin(coinName);
    }

    public BigDecimal getSumOfAvailableCoin(String coinName) {
        return userCoinMapper.getSumOfAvailableCoin(coinName);
    }
}
