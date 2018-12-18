package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.constants.UserBillReason;
import com.cmd.wallet.common.enums.BankType;
import com.cmd.wallet.common.mapper.PlatBankMapper;
import com.cmd.wallet.common.mapper.PlatConfigMapper;
import com.cmd.wallet.common.mapper.PlatOrderMapper;
import com.cmd.wallet.common.mapper.UserBankMapper;
import com.cmd.wallet.common.model.PlatBank;
import com.cmd.wallet.common.model.PlatConfig;
import com.cmd.wallet.common.model.PlatOrder;
import com.cmd.wallet.common.model.UserBank;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.common.utils.CageUtil;
import com.cmd.wallet.common.utils.DateUtil;
import com.cmd.wallet.common.vo.PlatOrderVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import java.util.List;

@Service
public class PlatOrderService {

    @Autowired
    PlatOrderMapper platOrderMapper;
    @Autowired
    PlatBankMapper platBankMapper;
    @Autowired
    PlatBankService platBankService;
    @Autowired
    UserBankMapper userBankMapper;
    @Autowired
    UserService userService;
    @Autowired
    UserCoinService userCoinService;
    @Autowired
    ConfigService configService;
    @Autowired
    PlatConfigMapper platConfigMapper;


    //充值
    @Transactional
    public Integer deposit(Integer userId, String coinName, BigDecimal amount, BigDecimal price){
        Assert.check(amount.doubleValue()<=0, ErrorCode.ERR_PARAM_ERROR);

        //获取随机银行
        PlatBank platBank = platBankService.getPlatBankRandom();
        Assert.check(platBank==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);

        PlatConfig platConfig = platConfigMapper.getPlatConfig(coinName);
        Assert.check(platConfig==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
        Assert.check(price.doubleValue()<platConfig.getBuyPrice().doubleValue(), ErrorCode.ERR_C2C_MIN_APPLICATION_PRICE);
        //Assert.check(amount.doubleValue()<platConfig.getOrderAmountMin().doubleValue(), ErrorCode.ERR_C2C_ORDER_AMOUNT_TOO_LOW);
        //Assert.check(amount.doubleValue()>platConfig.getOrderAmountMax().doubleValue(), ErrorCode.ERR_C2C_ORDER_AMOUNT_TOO_HIGH);
        String nowTime = DateUtil.getTimeString(new Date());
        Assert.check(nowTime.compareTo(platConfig.getStartTime())<0, ErrorCode.ERR_MARKET_TIME_NOT_OPEN);
        Assert.check(nowTime.compareTo(platConfig.getEndTime())>0, ErrorCode.ERR_MARKET_TIME_CLOSED);

        String payCode = CageUtil.getWordsNumber(6);

        PlatOrder platOrder = new PlatOrder()
                .setUserId(userId).setAmount(amount).setCoinName(coinName).setFee(BigDecimal.ZERO).setStatus(PlatOrder.STATUS_ACCEPTED)
                .setType(PlatOrder.TYPE_BUY).setPrice(platConfig.getBuyPrice()).setPayCode(payCode)
                .setBankType(BankType.BANK.getValue()).setBankName(platBank.getBankName()).setBankUser(platBank.getBankUser()).setBankNo(platBank.getBankNo()).setBankNameChild(platBank.getBankNameChild())
                .setCreateTime(new Date()).setExpireTime(new Date(new Date().getTime()+platConfig.getExpireMinute().intValue()*60*1000));

        platOrderMapper.add(platOrder);
        return platOrder.getId();
    }

    //提现
    @Transactional
    public Integer withdraw(Integer userId, String coinName, BigDecimal amount, BigDecimal price, Integer bankId){
        Assert.check(amount.doubleValue()<=0, ErrorCode.ERR_PARAM_ERROR);

        UserBank userBank = userBankMapper.getUserBank(bankId);
        Assert.check(userBank==null, ErrorCode.ERR_RECORD_NOT_EXIST);
        Assert.check(userBank.getUserId().intValue()!=userId.intValue(), ErrorCode.ERR_USER_NOT_BIND_BANK);

        PlatConfig platConfig = platConfigMapper.getPlatConfig(coinName);
        Assert.check(platConfig==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
        Assert.check(price.doubleValue()>platConfig.getSellPrice().doubleValue(), ErrorCode.ERR_C2C_MAX_APPLICATION_PRICE);
        //Assert.check(amount.doubleValue()<platConfig.getOrderAmountMin().doubleValue(), ErrorCode.ERR_C2C_ORDER_AMOUNT_TOO_LOW);
        //Assert.check(amount.doubleValue()>platConfig.getOrderAmountMax().doubleValue(), ErrorCode.ERR_C2C_ORDER_AMOUNT_TOO_HIGH);
        String nowTime = DateUtil.getTimeString(new Date());
        Assert.check(nowTime.compareTo(platConfig.getStartTime())<0, ErrorCode.ERR_MARKET_TIME_NOT_OPEN);
        Assert.check(nowTime.compareTo(platConfig.getEndTime())>0, ErrorCode.ERR_MARKET_TIME_CLOSED);

        String payCode = CageUtil.getWordsNumber(6);

        PlatOrder platOrder = new PlatOrder()
                .setUserId(userId).setAmount(amount).setCoinName(coinName).setFee(BigDecimal.ZERO).setStatus(PlatOrder.STATUS_ACCEPTED)
                .setType(PlatOrder.TYPE_SELL).setPrice(price).setPayCode(payCode)
                .setBankType(BankType.BANK.getValue()).setBankName(userBank.getBankName()).setBankUser(userBank.getBankUser()).setBankNo(userBank.getBankNo()).setBankNameChild(userBank.getBankNameChild())
                .setCreateTime(new Date()).setExpireTime(new Date(new Date().getTime()+platConfig.getExpireMinute().intValue()*60*1000));

        platOrderMapper.add(platOrder);

        //冻结提现
        userCoinService.changeUserCoin(userId, coinName, amount.negate(), amount,BigDecimal.ZERO, UserBillReason.PLAT_SELL_COIN, "提现卖出冻结:"+platOrder.getId());
        return platOrder.getId();
    }

    //用户确定付款
    @Transactional
    public void confirm(Integer userId, Integer id) {
        PlatOrder platOrder = platOrderMapper.lockPlatOrder(id);
        Assert.check(platOrder==null, ErrorCode.ERR_RECORD_NOT_EXIST);
        Assert.check(platOrder.getStatus()!=PlatOrder.STATUS_ACCEPTED, ErrorCode.ERR_C2C_ORDER_STATUS_ERROR);
        Assert.check(platOrder.getUserId().intValue()!=userId.intValue(), ErrorCode.ERR_C2C_ORDER_INVALID);

        platOrderMapper.updatePlatOrder(new PlatOrder().setId(platOrder.getId()).setStatus(PlatOrder.STATUS_PAID));
    }

    //取消订单
    @Transactional
    public void cancel(Integer userId, Integer id, String commnet){
        PlatOrder platOrder = platOrderMapper.lockPlatOrder(id);
        Assert.check(platOrder==null, ErrorCode.ERR_C2C_ORDER_NOT_EXIST);
        Assert.check(platOrder.getStatus()!=PlatOrder.STATUS_ACCEPTED, ErrorCode.ERR_C2C_ORDER_STATUS_ERROR);
        Assert.check(platOrder.getUserId().intValue()!=userId.intValue(), ErrorCode.ERR_C2C_ORDER_INVALID);

        if (platOrder.getType()==PlatOrder.TYPE_SELL){
            userCoinService.changeUserCoin(platOrder.getUserId(), platOrder.getCoinName(), platOrder.getAmount(), platOrder.getAmount().negate(),
                    BigDecimal.ZERO,UserBillReason.PLAT_SELL_COIN, "提现卖出取消:"+platOrder.getId());
        }
        platOrderMapper.updatePlatOrder(new PlatOrder().setId(platOrder.getId()).setStatus(PlatOrder.STATUS_CANCELED).setCancelComment(commnet));
    }


    //平台确认成功
    @Transactional
    public void adminConfirm(Integer id) {
        PlatOrder platOrder = platOrderMapper.lockPlatOrder(id);
        Assert.check(platOrder==null, ErrorCode.ERR_RECORD_NOT_EXIST);

        //管理端只有已经接单和申诉中的订单可以确认取消
        if (platOrder.getType()==PlatOrder.TYPE_SELL){
            Assert.check(platOrder.getStatus()!=PlatOrder.STATUS_ACCEPTED
                    && platOrder.getStatus()!=PlatOrder.STATUS_COMPLAINT, ErrorCode.ERR_C2C_ORDER_STATUS_ERROR);
            userCoinService.changeUserCoin(platOrder.getUserId(), platOrder.getCoinName(), BigDecimal.ZERO, platOrder.getAmount().negate(),
                    BigDecimal.ZERO,UserBillReason.PLAT_SELL_COIN, "提现卖出成功扣除:"+platOrder.getId());
        } else {
            Assert.check(platOrder.getStatus()!=PlatOrder.STATUS_PAID
                    && platOrder.getStatus() != PlatOrder.STATUS_ACCEPTED
                    && platOrder.getStatus()!=PlatOrder.STATUS_COMPLAINT, ErrorCode.ERR_C2C_ORDER_STATUS_ERROR);
            userCoinService.changeUserCoin(platOrder.getUserId(), platOrder.getCoinName(), platOrder.getAmount(), BigDecimal.ZERO,
                    BigDecimal.ZERO, UserBillReason.PLAT_BUY_COIN, "购买到账:"+platOrder.getId());
        }

        platOrderMapper.updatePlatOrder(new PlatOrder().setId(platOrder.getId()).setStatus(PlatOrder.STATUS_DONE));
    }

    //系统取消
    @Transactional
    public void adminCancel(Integer id, String commnet){
        PlatOrder platOrder = platOrderMapper.lockPlatOrder(id);
        Assert.check(platOrder==null, ErrorCode.ERR_C2C_ORDER_NOT_EXIST);
        Assert.check(platOrder.getStatus()!=PlatOrder.STATUS_ACCEPTED
                && platOrder.getStatus()!=PlatOrder.STATUS_COMPLAINT
                && platOrder.getStatus()!=PlatOrder.STATUS_PAID,ErrorCode.ERR_C2C_ORDER_STATUS_ERROR);

        if (platOrder.getType()==PlatOrder.TYPE_SELL){
            userCoinService.changeUserCoin(platOrder.getUserId(), platOrder.getCoinName(), platOrder.getAmount(), platOrder.getAmount().negate(),
                    BigDecimal.ZERO,UserBillReason.PLAT_SELL_COIN, "提现卖出取消:"+platOrder.getId());
        }
        platOrderMapper.updatePlatOrder(new PlatOrder().setId(platOrder.getId()).setStatus(PlatOrder.STATUS_CANCELED).setCancelComment(commnet));
    }

    //申诉订单
    @Transactional
    public void complaint(Integer userId, Integer id, String comment){
        PlatOrder order = platOrderMapper.lockPlatOrder(id);
        Assert.check(order==null, ErrorCode.ERR_C2C_ORDER_NOT_EXIST);
        Assert.check(order.getStatus()!=PlatOrder.STATUS_PAID, ErrorCode.ERR_C2C_ORDER_STATUS_ERROR);
        Assert.check(userId.intValue()!=order.getUserId().intValue(), ErrorCode.ERR_C2C_ORDER_INVALID);

        //用户没有收到钱或者币
        platOrderMapper.updatePlatOrder(new PlatOrder().setId(order.getId()).setStatus(PlatOrder.STATUS_COMPLAINT).setComment(comment));
    }


    //获取我的订单
    public Page<PlatOrder> getMyPlatOrderList(Integer userId, String coinName, Integer[]status, int pageNo, int pageSize){
        return platOrderMapper.getPlatOrderList(userId, coinName, status, new RowBounds(pageNo, pageSize));
    }
    public Page<PlatOrderVO> getPlatOrderVOList(Integer userId, String coinName, Integer status, int pageNo, int pageSize){
        return platOrderMapper.getPlatOrderVOList(userId, coinName, status, new RowBounds(pageNo, pageSize));
    }
    public PlatOrder getPlatOrderDetail(Integer orderId){
        return platOrderMapper.getPlatOrder(orderId);
    }


    //获取超时订单(定时任务扫码使用)
    public List<PlatOrder> getPlatOrderExpire(){
        return platOrderMapper.getPlatOrderExpire(new Date());
    }

}
