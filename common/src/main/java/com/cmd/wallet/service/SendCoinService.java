package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.constants.UserBillReason;
import com.cmd.wallet.common.enums.SendCoinStatus;
import com.cmd.wallet.common.mapper.*;
import com.cmd.wallet.common.model.*;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.common.utils.RandomUtil;
import com.cmd.wallet.common.mapper.CoinMapper;
import com.cmd.wallet.common.vo.UserCoinVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class SendCoinService {
    private static final Logger logger = LoggerFactory.getLogger(SendCoinService.class);

    @Autowired
    private CoinMapper coinMapper;
    @Autowired
    private UserCoinService userCoinService;
    @Autowired
    private SendCoinMapper sendCoinMapper;
    @Autowired
    CoinConfigMapper coinConfigMapper;
    @Autowired
    private ReceivedCoinMapper receivedCoinMapper;
    @Autowired
    AdapterService adapterService;
    @Autowired
    SmsService smsService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserCoinMapper userCoinMapper;
    @Autowired
    ConfigService configService;
    @Autowired
    ChangeConfigService changeConfigService;


    private void transferCoin(int userId, String coinName, String address, double amount, String comment){
        Coin coin = coinMapper.getCoinByName(coinName);
        Assert.check(coin==null, ErrorCode.ERR_RECORD_NOT_EXIST);

        UserCoinVO userCoin =userCoinMapper.getUserCoinByUserIdAndCoinName(userId, coinName);
        Assert.check(userCoin==null, ErrorCode.ERR_RECORD_NOT_EXIST);

        UserCoin toUserCoin=userCoinMapper.getUserCoinByAddressAndCoinName(address, coinName);
        if (toUserCoin!=null){
            //内部转账
            this.transferInner(userId, coinName, toUserCoin.getUserId(), amount, comment);
        } else {
            //转账到外部，提现
            this.transferOut(userId, coinName, address, amount, comment);
        }
    }

    //内部转账
    @Transactional
    public void transferInner(int userId, String coinName, int toUserId, double amount, String comment){
        Assert.check(amount<=0, ErrorCode.ERR_PARAM_ERROR);

        CoinConfig config = coinConfigMapper.getCoinConfigByName(coinName);
        Assert.check(config==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
        Assert.check(config.getTransferEnable().intValue()!=1, ErrorCode.ERR_PARAM_ERROR);
        Assert.check(amount<config.getTransferMinAmount().doubleValue(), ErrorCode.ERR_TRANSFER_AMOUNT_TO_LOW);
        Assert.check(amount>config.getTransferMaxAmount().doubleValue(), ErrorCode.ERR_TRANSFER_AMOUNT_TO_HIGH);

        String toAddress=null;
        if (toAddress==null){
            User toUser = userMapper.getUserByUserId(toUserId);
            toAddress = toUser.getMobile()!=null ? toUser.getMobile():toUser.getEmail();
        }
        String address =null;
        if (address==null) {
            User user = userMapper.getUserByUserId(userId);
            address = user.getMobile()!=null ? user.getMobile():user.getEmail();
        }

        //冻结金额
        String platCoin = configService.getPlatformCoinName();
        BigDecimal feeAmount = BigDecimal.ZERO;
        if (config.getTransferFeeSelect().intValue()==0){
            feeAmount = new BigDecimal(amount).multiply(config.getTransferFeeRate());
        }else if (config.getTransferFeeSelect().intValue()==1){
            Assert.check(true, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
            //feeAmount = config.getTransferFeeStatic();
            //Assert.check(amount<= feeAmount.doubleValue(), ErrorCode.ERR_TRANSFER_AMOUNT_TO_LOW);
        }
        feeAmount = feeAmount.multiply(changeConfigService.getChangeRate(coinName, platCoin));

        //转出方减少金额
        userCoinService.changeUserCoin(userId, coinName, BigDecimal.valueOf(amount).negate(), BigDecimal.ZERO,BigDecimal.ZERO, UserBillReason.TRANSFER, ""+toAddress);
        userCoinService.changeUserCoin(userId, platCoin, feeAmount.negate(), BigDecimal.ZERO, BigDecimal.ZERO,UserBillReason.TRANSFER_FEE, "内部转账手续费");

        //转入方增加金额
        userCoinService.changeUserCoin(toUserId, coinName, BigDecimal.valueOf(amount), BigDecimal.ZERO, BigDecimal.ZERO,UserBillReason.TRANSFER, ""+address);

        //添加记录
        String txid = "inner-" + System.currentTimeMillis() + "-" + userId + "-" + (new Random()).nextInt();
        sendCoinMapper.add(new SendCoin().setUserId(userId).setCoinName(coinName).setAddress(toAddress).setAmount(BigDecimal.valueOf(amount)).setFee(feeAmount)
                .setTxid(txid).setReceivedUserId(toUserId).setStatus(SendCoinStatus.PASSED).setType(0).setComment(comment));

        //添加收款记录
        receivedCoinMapper.add(new ReceivedCoin().setUserId(toUserId).setAddress(toAddress).setCoinName(coinName)
                .setTxid(txid).setAmount(BigDecimal.valueOf(amount)).setFee(feeAmount)
                .setTxTime(Long.valueOf(System.currentTimeMillis() / 1000l).intValue()).setType(0).setFromAddress(address));
    }

    //外部转账
    @Transactional
    public void transferOut(int userId, String coinName, String toAddress, double amount, String comment){
        Assert.check(amount<=0, ErrorCode.ERR_PARAM_ERROR);

        UserCoinVO userCoin =userCoinMapper.getUserCoinByUserIdAndCoinName(userId, coinName);
        Assert.check(userCoin==null, ErrorCode.ERR_RECORD_NOT_EXIST);

        UserCoin toUserCoin=userCoinMapper.getUserCoinByAddressAndCoinName(toAddress, coinName);
        Assert.check(toUserCoin!=null, ErrorCode.ERR_TRANSFER_OUT_ADDRESS_EXIST);

        CoinConfig config = coinConfigMapper.getCoinConfigByName(coinName);
        Assert.check(config==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
        Assert.check(amount<config.getWithdrawMinAmount().doubleValue(), ErrorCode.ERR_TRANSFER_AMOUNT_TO_LOW);
        Assert.check(amount>config.getWithdrawMaxAmount().doubleValue(), ErrorCode.ERR_TRANSFER_AMOUNT_TO_HIGH);

        //冻结金额
        String platCoin = configService.getPlatformCoinName();
        BigDecimal feeAmount = BigDecimal.ZERO;
        if (config.getTransferFeeSelect().intValue()==0){
            feeAmount = new BigDecimal(amount).multiply(config.getWithdrawFeeRate());
        }else if (config.getTransferFeeSelect().intValue()==1){
            Assert.check(true, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
            //feeAmount = config.getTransferFeeStatic();
            //Assert.check(amount<= feeAmount.doubleValue(), ErrorCode.ERR_TRANSFER_AMOUNT_TO_LOW);
        }
        feeAmount = feeAmount.multiply(changeConfigService.getChangeRate(coinName, platCoin));

        if (feeAmount.doubleValue()>0) {
            userCoinService.changeUserCoin(userId, Coin.BSTS, feeAmount.negate(), feeAmount, BigDecimal.ZERO,UserBillReason.FREEZE, "手续费冻结");
        }
        userCoinService.changeUserCoin(userId, coinName, new BigDecimal(amount).negate(), new BigDecimal(amount),BigDecimal.ZERO, UserBillReason.FREEZE, "提现冻结" );
        sendCoinMapper.add(new SendCoin().setUserId(userId).setCoinName(coinName).setAmount(new BigDecimal(amount)).setFee(feeAmount)
                .setStatus(SendCoinStatus.APPLYING).setAddress(toAddress).setType(1).setComment(comment));
    }

    //审核失败
    @Transactional
    public void transferCheckFail(int id, int userId){
        SendCoin sendCoin = sendCoinMapper.lockTransferById(id);
        Assert.check(sendCoin==null, ErrorCode.ERR_RECORD_NOT_EXIST);
        Assert.check(sendCoin.getStatus()!= SendCoinStatus.APPLYING, ErrorCode.ERR_RECORD_DATA_ERROR);

        if (sendCoin.getFee().doubleValue()>0) {
            userCoinService.changeUserCoin(sendCoin.getUserId(), Coin.BSTS, sendCoin.getFee(), sendCoin.getFee().negate(),BigDecimal.ZERO,
                    UserBillReason.UNFREEZE, "提现手续费返回");
        }
        userCoinService.changeUserCoin(sendCoin.getUserId(), sendCoin.getCoinName(), sendCoin.getAmount(), sendCoin.getAmount().negate(),BigDecimal.ZERO,
                UserBillReason.UNFREEZE, "提现失败返回");

        sendCoinMapper.updateTransferStatusById(id, SendCoinStatus.FAILED.getValue(), null);
    }

    //审核通过
    @Transactional
    public void transferCheckPass(int id, int userId){
        SendCoin sendCoin = sendCoinMapper.lockTransferById(id);
        Assert.check(sendCoin==null, ErrorCode.ERR_RECORD_NOT_EXIST);
        Assert.check(sendCoin.getStatus()!= SendCoinStatus.APPLYING, ErrorCode.ERR_RECORD_DATA_ERROR);

        BigDecimal amount = sendCoin.getAmount();
        BigDecimal toAmount = sendCoin.getAmount();//.subtract(sendCoin.getFee());

        //冻结扣除，审核通过
        if (sendCoin.getFee().doubleValue()>0){
            userCoinService.changeUserCoin(sendCoin.getUserId(), Coin.BSTS, BigDecimal.ZERO, sendCoin.getFee().negate(), BigDecimal.ZERO,UserBillReason.WITHDRAW_FEE, "手续费扣除");
        }
        userCoinService.changeUserCoin(sendCoin.getUserId(), sendCoin.getCoinName(), BigDecimal.ZERO, amount.negate(), BigDecimal.ZERO,UserBillReason.WITHDRAW, "提现成功扣除");

        //区块链主账户转账
        String txid = null;
        if (Coin.EOS.equalsIgnoreCase(sendCoin.getCoinName())){
            Coin coin = coinMapper.getCoinByName(sendCoin.getCoinName());
            txid = adapterService.sendToAddress(0, sendCoin.getCoinName(), sendCoin.getAddress(), toAmount.doubleValue(), sendCoin.getComment());
        }else {
            txid = adapterService.sendToAddress(0, sendCoin.getCoinName(), sendCoin.getAddress(), toAmount.doubleValue(), "");
        }
        Assert.check(txid==null || txid.length()<=0, ErrorCode.ERR_TRANSFER_FAIL);
        if (txid.equalsIgnoreCase("timeout")){
            sendCoinMapper.updateTransferStatusById(id, SendCoinStatus.OTHER.getValue(), txid+"-"+sendCoin.getUserId()+"-"+new Date().getTime());
        }else {
            sendCoinMapper.updateTransferStatusById(id, SendCoinStatus.PASSED.getValue(), txid);
        }
    }

    //获取列表
    public Page<SendCoin> getTransferList(Integer userId, String coinName, SendCoinStatus status, int pageNo, int pageSize){
        return sendCoinMapper.getTransfer(userId, coinName, status != SendCoinStatus.ALL ? status.getValue():null, null, new RowBounds(pageNo, pageSize));
    }
    public Page<SendCoin> getTransferList(Integer userId, String coinName, Integer[]status, int pageNo, int pageSize){
        return sendCoinMapper.getSendCoin(userId, coinName, status,null, new RowBounds(pageNo, pageSize));
    }

    public Page<SendCoin> getTransferList2(Integer userId, String coinName, Integer[]status, String address, int pageNo, int pageSize){
        return sendCoinMapper.getSendCoin(userId, coinName, status,address, new RowBounds(pageNo, pageSize));
    }

    //获取节点未确认的列表
    public List<SendCoin> getTransferUnconfirm(Integer id){ return sendCoinMapper.getTransferUnconfirm(id); }

    @Transactional
    public void nodeConfirm(int id){
        SendCoin sendCoin = sendCoinMapper.lockTransferById(id);
        CoinConfig coinConfig = coinConfigMapper.getCoinConfigByName(sendCoin.getCoinName());
        Assert.check(coinConfig==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);

        //int confirm = adapterService.getTxConfirmCount(sendCoin.getCoinName(), sendCoin.getTxid());
        //if (coinConfig.getNodeConfirmCount()==null){
        //    logger.error("NodeConfirmCount error!!!");
        //}
        //if (confirm>=coinConfig.getNodeConfirmCount().intValue()){
        //    sendCoinMapper.updateTransferStatusById(id, SendCoinStatus.CONFIRM.getValue(), null);
        //}
    }
}
