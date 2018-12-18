package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.mapper.*;
import com.cmd.wallet.common.model.*;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.common.utils.EncryptionUtil;
import com.cmd.wallet.common.vo.*;
import com.github.pagehelper.Page;
import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;
import io.github.novacrypto.hashing.Sha256;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class WalletService {
    private static Logger logger = LoggerFactory.getLogger(WalletService.class);

    @Autowired
    AdapterService adapterService;
    @Autowired
    UserService userService;
    @Autowired
    SmsService smsService;
    @Autowired
    SendCoinService sendCoinService;
    @Autowired
    ConfigService configService;
    @Autowired
    UserCoinService userCoinService;
    @Autowired
    UserCoinMapper userCoinMapper;
    @Autowired
    CoinMapper coinMapper;
    @Autowired
    CoinConfigMapper coinConfigMapper;
    @Autowired
    ConfigLevelMapper configLevelMapper;
    @Autowired
    UserBillMapper userBillMapper;
    @Autowired
    UserRewardLogMapper userRewardLogMapper;
    @Autowired
    ChangeConfigMapper changeConfigMapper;
    @Autowired
    WalletService walletService;
    @Autowired
    UserTaskMapper userTaskMapper;
    @Autowired
    ChangeConfigService changeConfigService;
    @Autowired
    UserWordsMapper userWordsMapper;
    @Autowired
    EarningsDayMapper earningsDayMapper;
    @Autowired
    UserEarningsMapper userEarningsMapper;

    //获取币种地址
    public String getAddressByCoinName(int userId, String coinName) {
        return adapterService.getAccountAddress(userId, coinName);
    }

    //注册获取地址
    public void registerAddress(Integer userId) {
        List<Coin> list = coinMapper.getCoinAll();
        for (Coin coin: list){
            try {
                String address = getAddressByCoinName(userId, coin.getName());
                logger.info("register address:"+userId+","+coin.getName()+","+address);
            }catch (Exception e){
                logger.error("error register address:"+userId+","+coin.getName());
            }
        }
    }

    //获取钱包类型
    public List<WalletVO> getWallet(){
        List<WalletVO> list = new ArrayList<>();
        List<String> ll = coinMapper.getCoinWallet();
        for (String walletName: ll){
            WalletVO vo = new WalletVO().setWalletName(walletName);
            Coin coin = coinMapper.getCoinByName(walletName);
            if (coin!=null){
                vo.setIcon(coin.getIcon());
            }
            list.add(vo);
        }
        return list;
    }

    //获取钱包币
    public List<UserCoinVO> getCoinByWallet(Integer userId, String walletName){
        String platCoin = configService.getPlatformCoinName();
        List<UserCoinVO> list = new ArrayList<>();
        List<Coin> ll = coinMapper.getCoinByWallet(walletName);
        for (Coin coin:ll){
            UserCoinVO vo = userCoinMapper.getUserCoinByUserIdAndCoinName(userId, coin.getName());
            if (vo==null){
                vo = new UserCoinVO().setCoinName(coin.getName()).setIcon(coin.getIcon()).setUserId(userId).setAvailableBalance(BigDecimal.ZERO).setFreezeBalance(BigDecimal.ZERO);
            }
            CoinConfig config = coinConfigMapper.getCoinConfigByName(coin.getName());
            if (config!=null){
                vo.setTransferFeeRate(config.getTransferFeeRate()).setTransferMaxAmount(config.getTransferMaxAmount()).setTransferMinAmount(config.getTransferMinAmount())
                        .setTransferFeeStatic(config.getTransferFeeStatic()).setTransferFeeSelect(config.getTransferFeeSelect()).setTransferEnable(config.getTransferEnable())
                        .setWithdrawFeeRate(config.getWithdrawFeeRate()).setWithdrawMinAmount(config.getWithdrawMinAmount()).setWithdrawMaxAmount(config.getWithdrawMaxAmount());
            }
            vo.setIcon(coin.getIcon());

            //获取对应的汇率
            vo.setChangeRate(changeConfigService.getChangeRate(coin.getName(), platCoin));
            ChangeConfig change = changeConfigMapper.getChangeConfig(coin.getName(), Coin.CNY);
            if (change!=null){
                vo.setMoneyCny(vo.getAvailableBalance().multiply(change.getRate()).setScale(8, RoundingMode.FLOOR));
                vo.setMoneyUsd(vo.getAvailableBalance().multiply(change.getUsdRate()).setScale(8, RoundingMode.FLOOR));
            }
            if (Coin.EOS.equalsIgnoreCase(coin.getName())){
                vo.setBindAddress(coin.getCoinBase());
            }
            vo.setAddressTag(UserCoin.getAddressTag(userId));
            list.add(vo);
        }
        return list;
    }

    //获取钱包余额
    public BigDecimal getCoinBalanceByWallet(Integer userId, String walletName) {
        BigDecimal balanceAll = BigDecimal.ZERO;
        Map<String, BigDecimal> m = changeConfigService.getChangeConfig();
        List<Coin> ll = coinMapper.getCoinByWallet(walletName);
        for (Coin coin:ll){
            UserCoinVO vo = userCoinMapper.getUserCoinByUserIdAndCoinName(userId, coin.getName());
            if (vo==null)
                continue;

            if (m.containsKey(coin.getName())){
                balanceAll = balanceAll.add(vo.getAvailableBalance().multiply(m.get(coin.getName())));
            }
        }
        return balanceAll;
    }

    //获取钱包所有数据
    public WalletCoinVO getWalletCoinAll(Integer userId, String walletName){
        String platCoin = configService.getPlatformCoinName();
        BigDecimal balanceAll = BigDecimal.ZERO;
        String address = null;
        List<UserCoinVO> list = new ArrayList<>();
        Map<String, BigDecimal> m = changeConfigService.getChangeConfig();
        List<Coin> ll = coinMapper.getCoinByWallet(walletName);
        for (Coin coin:ll){
            UserCoinVO vo = userCoinMapper.getUserCoinByUserIdAndCoinName(userId, coin.getName());
            if (vo==null){
                vo = new UserCoinVO().setCoinName(coin.getName()).setIcon(coin.getIcon()).setUserId(userId).setAvailableBalance(BigDecimal.ZERO).setFreezeBalance(BigDecimal.ZERO);
            }
            CoinConfig config = coinConfigMapper.getCoinConfigByName(coin.getName());
            if (config!=null){
                vo.setTransferFeeRate(config.getTransferFeeRate()).setTransferMaxAmount(config.getTransferMaxAmount()).setTransferMinAmount(config.getTransferMinAmount())
                        .setTransferFeeStatic(config.getTransferFeeStatic()).setTransferFeeSelect(config.getTransferFeeSelect()).setTransferEnable(config.getTransferEnable())
                        .setWithdrawFeeRate(config.getWithdrawFeeRate()).setWithdrawMinAmount(config.getWithdrawMinAmount()).setWithdrawMaxAmount(config.getWithdrawMaxAmount());
            }
            vo.setIcon(coin.getIcon());

            //获取对应的汇率
            vo.setChangeRate(changeConfigService.getChangeRate(coin.getName(), platCoin));
            ChangeConfig change = changeConfigMapper.getChangeConfig(coin.getName(), Coin.CNY);
            if (change!=null){
                vo.setMoneyCny(vo.getAvailableBalance().multiply(change.getRate()).setScale(8, RoundingMode.FLOOR));
                vo.setMoneyUsd(vo.getAvailableBalance().multiply(change.getUsdRate()).setScale(8, RoundingMode.FLOOR));
            }
            if (Coin.EOS.equalsIgnoreCase(coin.getName())){
                vo.setBindAddress(coin.getCoinBase());
            }
            vo.setAddressTag(UserCoin.getAddressTag(userId));
            list.add(vo);

            if (m.containsKey(coin.getName())){
                balanceAll = balanceAll.add(vo.getAvailableBalance().multiply(m.get(coin.getName())));
            }
            if (address==null) {
                address = vo.getBindAddress();
            }
        }
        return new WalletCoinVO().setAddress(address).setBalance(balanceAll).setWalletName(walletName).setList(list);
    }

    //获取某个币种
    public UserCoinVO getUserCoin(int userId, String coinName){
        String platCoin = configService.getPlatformCoinName();
        Coin coin = coinMapper.getCoinByName(coinName);
        Assert.check(coin==null, ErrorCode.ERR_NOT_SUPPORT_COIN);

        UserCoinVO vo = userCoinMapper.getUserCoinByUserIdAndCoinName(userId, coin.getName());
        if (vo==null){
            vo = new UserCoinVO().setCoinName(coin.getName()).setUserId(userId).setAvailableBalance(BigDecimal.ZERO).setFreezeBalance(BigDecimal.ZERO);
        }
        CoinConfig config = coinConfigMapper.getCoinConfigByName(coin.getName());
        if (config!=null){
            vo.setTransferFeeRate(config.getTransferFeeRate()).setTransferMaxAmount(config.getTransferMaxAmount()).setTransferMinAmount(config.getTransferMinAmount())
                    .setTransferFeeStatic(config.getTransferFeeStatic()).setTransferFeeSelect(config.getTransferFeeSelect()).setTransferEnable(config.getTransferEnable())
                    .setWithdrawFeeRate(config.getWithdrawFeeRate()).setWithdrawMinAmount(config.getWithdrawMinAmount()).setWithdrawMaxAmount(config.getWithdrawMaxAmount());
        }
        vo.setIcon(coin.getIcon());

        //获取对应的汇率
        vo.setChangeRate(changeConfigService.getChangeRate(coin.getName(), platCoin));
        ChangeConfig change = changeConfigMapper.getChangeConfig(coin.getName(), Coin.CNY);
        if (change!=null){
            vo.setMoneyCny(vo.getAvailableBalance().multiply(change.getRate()).setScale(8, RoundingMode.FLOOR));
            vo.setMoneyUsd(vo.getAvailableBalance().multiply(change.getUsdRate()).setScale(8, RoundingMode.FLOOR));
        }
        if (Coin.EOS.equalsIgnoreCase(coin.getName())){
            vo.setBindAddress(coin.getCoinBase());
        }
        vo.setAddressTag(UserCoin.getAddressTag(userId));
        return vo;
    }

    //获取币种列表
    public List<UserCoinVO> getUserCoinList(int userId){
        String platCoin = configService.getPlatformCoinName();
        List<UserCoinVO> list = new ArrayList<>();
        List<Coin> ll = coinMapper.getCoin();
        for (Coin coin:ll){
            UserCoinVO vo = userCoinMapper.getUserCoinByUserIdAndCoinName(userId, coin.getName());
            if (vo==null){
                vo = new UserCoinVO().setCoinName(coin.getName()).setIcon(coin.getIcon()).setUserId(userId).setAvailableBalance(BigDecimal.ZERO).setFreezeBalance(BigDecimal.ZERO);
            }
            CoinConfig config = coinConfigMapper.getCoinConfigByName(coin.getName());
            if (config!=null){
                vo.setTransferFeeRate(config.getTransferFeeRate()).setTransferMaxAmount(config.getTransferMaxAmount()).setTransferMinAmount(config.getTransferMinAmount())
                        .setTransferFeeStatic(config.getTransferFeeStatic()).setTransferFeeSelect(config.getTransferFeeSelect()).setTransferEnable(config.getTransferEnable())
                        .setWithdrawFeeRate(config.getWithdrawFeeRate()).setWithdrawMinAmount(config.getWithdrawMinAmount()).setWithdrawMaxAmount(config.getWithdrawMaxAmount());
            }
            vo.setIcon(coin.getIcon());

            //获取对应的汇率
            vo.setChangeRate(changeConfigService.getChangeRate(coin.getName(), platCoin));
            ChangeConfig change = changeConfigMapper.getChangeConfig(coin.getName(), Coin.CNY);
            if (change!=null){
                vo.setMoneyCny(vo.getAvailableBalance().multiply(change.getRate()).setScale(8, RoundingMode.FLOOR));
                vo.setMoneyUsd(vo.getAvailableBalance().multiply(change.getUsdRate()).setScale(8, RoundingMode.FLOOR));
            }
            if (Coin.EOS.equalsIgnoreCase(coin.getName())){
                vo.setBindAddress(coin.getCoinBase());
            }
            vo.setAddressTag(UserCoin.getAddressTag(userId));
            list.add(vo);
        }
        return list;
    }

    //是否是内部地址
    public Integer innerAddress(String coinName, String address){
        UserCoin userCoin = userCoinMapper.getUserCoinByAddressAndCoinName(address, coinName);
        if (userCoin==null){
            return 0;
        }else{
           return 1;
        }
    }

    //转账出去
    @Transactional
    public void transferOut(int userId, String coinName, String toAddress, BigDecimal amount, String comment, String validCode, String payPassword) {
        //检查验证码
        Assert.check(!adapterService.isAddressValid(coinName, toAddress), ErrorCode.ERR_INVALID_ADDRESS);

        if (Coin.EOS.equalsIgnoreCase(coinName)){
            Assert.check(comment==null || comment.trim().length()==0, ErrorCode.ERR_INVALID_ADDRESS);
        }

        Coin coin = coinMapper.getCoinByName(coinName);
        Assert.check(coin==null, ErrorCode.ERR_NOT_SUPPORT_COIN);
        Assert.check(coin.getStatus()!=0, ErrorCode.ERR_NOT_SUPPORT_COIN);

        //检查密码
        User user = userService.getUserByUserId(userId);
        Assert.check(StringUtils.isBlank(user.getPayPassword()), ErrorCode.ERR_USER_PAY_PASSWORD_NOT_FOUND);
        Assert.check(!EncryptionUtil.checkMD5(payPassword, user.getPayPassword()), ErrorCode.ERR_USER_PASSWORD_ERROR);
        sendCoinService.transferOut(userId, coinName, toAddress, amount.doubleValue(), comment);
    }

    //内部转账
    @Transactional
    public void transferInner(int userId, String coinName, String toAddress, BigDecimal amount, String comment, String validCode, String payPassword) {
        //检查对手在此平台是否存在, mobile可以是一个地址和手机号
        Assert.check(!adapterService.isAddressValid(coinName, toAddress), ErrorCode.ERR_TRANSFER_OUT_ADDRESS_NOT_EXIST);

        //确定内部用户
        User toUser = null;
        if (Coin.EOS.equalsIgnoreCase(coinName)){
            Assert.check(comment==null || comment.trim().length()==0, ErrorCode.ERR_INVALID_ADDRESS);
            Coin coin=coinMapper.getCoinByName(coinName);
            Assert.check(!toAddress.equalsIgnoreCase(coin.getCoinBase()), ErrorCode.ERR_TRANSFER_OUT_ADDRESS_NOT_EXIST);
            UserCoin toUserCoin=userCoinMapper.getUserCoinByAddressAndCoinName(comment, coinName);
            if (toUserCoin!=null)
                toUser = userService.getUserByUserId(toUserCoin.getUserId());
        }else{
            UserCoin toUserCoin=userCoinMapper.getUserCoinByAddressAndCoinName(toAddress, coinName);
            if (toUserCoin!=null)
                toUser = userService.getUserByUserId(toUserCoin.getUserId());
        }
        Assert.check(toUser==null, ErrorCode.ERR_TRANSFER_OUT_ADDRESS_NOT_EXIST);

        Coin coin = coinMapper.getCoinByName(coinName);
        Assert.check(coin==null, ErrorCode.ERR_NOT_SUPPORT_COIN);
        Assert.check(coin.getStatus()!=0, ErrorCode.ERR_NOT_SUPPORT_COIN);
        Assert.check( userId==toUser.getId().intValue(), ErrorCode.ERR_TRANSFER_TO_OWNER);

        //检查密码,检查验证码
        User user = userService.getUserByUserId(userId);
        Assert.check(StringUtils.isBlank(user.getPayPassword()), ErrorCode.ERR_USER_PAY_PASSWORD_NOT_FOUND);
        Assert.check(!EncryptionUtil.checkMD5(payPassword, user.getPayPassword()), ErrorCode.ERR_USER_PASSWORD_ERROR);
        sendCoinService.transferInner(userId, coinName, toUser.getId(), amount.doubleValue(), comment);
    }



    public CoinConfig getCoinConfig(String coinName){
        return  coinConfigMapper.getCoinConfigByName(coinName);
    }
    public ChangeConfig getChangeConfig(String coinName, String changeName){
        return changeConfigMapper.getChangeConfig(coinName, changeName);
    }
    public CoinLastVO getCoinLast24(String coinName, String changeName){
        ChangeConfig changeConfig = changeConfigMapper.getChangeConfig(coinName, changeName);
        Coin coin = coinMapper.getCoinByName(coinName);
        CoinLastVO vo = new CoinLastVO().setCoinName(changeConfig.getCoinName())
                .setChangeName(changeConfig.getChangeName())
                .setLastPrice(changeConfig.getRate())
                .setLastUsdPrice(changeConfig.getUsdRate())
                .setChangeRate(changeConfig.getChangeRate());
        if (coin!=null){
            vo.setIcon(coin.getIcon());
        }
        return vo;
    }
    public List<CoinLastVO> getAllCoinLast24(){
        List<CoinLastVO> ll = new ArrayList<>();
        List<ChangeConfig> list = changeConfigMapper.getChangeConfigList();
        list.forEach(config->{
            if (!Coin.USD.equals(config.getCoinName()) && Coin.CNY.equals(config.getChangeName())) {
                CoinLastVO vo = new CoinLastVO().setCoinName(config.getCoinName())
                        .setChangeName(config.getChangeName())
                        .setLastPrice(config.getCnyRate())
                        .setLastUsdPrice(config.getUsdRate())
                        .setChangeRate(config.getChangeRate());
                Coin coin = coinMapper.getCoinByName(config.getCoinName());
                if (coin!=null){
                    vo.setIcon(coin.getIcon());
                }
                ll.add(vo);
            }
        });
        return ll;
    }

    public Page<UserBillVO> getUserBillByReason(Integer userId, String coinName, String[] reason, Integer pageNo, Integer pageSize){
        return  userBillMapper.getUserBillByReason(userId, coinName, reason, new RowBounds(pageNo, pageSize));
    }
    public Page<RewardLogVO> getRewardLog(Integer userId, Integer pageNo, Integer pageSize) {
        return userRewardLogMapper.getUserRewardLog(userId, new RowBounds(pageNo, pageSize));
    }


    public UserWords getUserWords(Integer userId){
        UserWords userWords = userWordsMapper.getUserWords(userId);
        if (userWords==null){
            //生成12个单词的助记词
            StringBuilder sb = new StringBuilder();
            byte[] entropy = new byte[Words.TWELVE.byteLength()];
            new SecureRandom().nextBytes(entropy);
            new MnemonicGenerator(English.INSTANCE).createMnemonic(entropy, sb::append);
            String mnemonic = sb.toString();
            userWords = new UserWords().setUserId(userId).setWords(mnemonic).setStatus(0);
            userWordsMapper.add(userWords);
        }
        return userWords;
    }

    public void confirmUserWords(Integer userId, String words){
        UserWords userWords = userWordsMapper.getUserWords(userId);
        if (userWords!=null){
            if (words.equals(userWords.getWords())){
                userWordsMapper.updateUserWords(new UserWords().setId(userWords.getId()).setStatus(1));
                return;
            }
        }
        Assert.check(true, ErrorCode.ERR_MNEMONIC_ERROR);
    }

    public UserEarningsVO getUserEarnings(Integer userId){
        UserEarningsVO vo = new UserEarningsVO();
        UserEarnings earnings = userEarningsMapper.getUserEarningsByUserId(userId);
        if (earnings!=null){
            vo.setGiveReward(earnings.getGiveReward());
            vo.setFreezeReward(earnings.getFreezeReward());
        }
        UserCoinVO coinVO = userCoinService.getUserCoinByUserIdAndCoinName(userId, Coin.BSTS);
        if (coinVO!=null){
            vo.setAvailableBalance(coinVO.getAvailableBalance());
        }
        return vo;
    }
    public Page<EarningsDay> getRewardDay(Integer userId, Integer pageNo, Integer pageSize){
        return earningsDayMapper.getEarningsDayList(userId, new RowBounds(pageNo, pageSize));
    }

}
