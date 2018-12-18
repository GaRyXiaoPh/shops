package com.cmd.wallet.common.constants;

public class UserBillReason {
    public static final String BC_RECEIVED_COIN    = "BcRev";      // 从区块链中收到转账（外部进入）
    public static final String REGISTER_REWARD     = "Register";    //注册赠送
    public static final String REFERRER_REWARD     = "Referrer";    //推荐奖励
    public static final String FREEZE               = "Freeze";       //冻结
    public static final String UNFREEZE             = "UnFreeze";     //解冻
    public static final String TRANSFER             = "Transfer";     //内部转账
    public static final String WITHDRAW             = "Withdraw";     //外部提现
    public static final String MINER_REWARD         = "Minward";        //挖矿奖励
    public static final String COMMUNITY_REWARD     ="Comward";         //社区奖励
    public static final String DISPATCH_RELEASE     = "DisRel";         //拨币释放
    public static final String PLAT_SELL_COIN       ="PlSell";          //向平台卖出
    public static final String PLAT_BUY_COIN        ="PlBuy";           //向平台购买
    public static final String COMMUNITY_CONSUME    ="ComCon";      //社区奖励消耗币
    public static final String TRANSFER_FEE         ="TranFee";     //转账手续费
    public static final String WITHDRAW_FEE         ="WithFee";     //外部提现手续费
    public static final String DISCOVERY_REWARD     ="Discover";    //发现奖励

    public static final String MALL_BUY            ="MallBuy";           //商城购物冻结
    public static final String MALL_RETURN          ="MallRetu";     //商城退货解冻
    public static final String MALL_SALE            ="MallSale";        //商城销售盈利
    public static final String MALL_REBATE            ="MallRaba";        //平台促销
    public static final String MALL_ONE            ="MallOne";        //消费一级推介奖励
    public static final String MALL_TWO            ="MallTwo";        //消费一级推介奖励

    public static final String MALL_BUY_REWARD            ="MallBuyReward"; //促销冻结奖励
    public static final String MALL_RELEASE_REWARD        ="MallReleaseReward"; //促销冻结金额释放

    public static final String SHOP_ONE            ="ShopOne";        //商家一级推介奖励
    public static final String SHOP_TWO            ="ShopTwo";        //商家二级推介奖励



    public static String getReasonStr(String reason){
        if(reason.equals(BC_RECEIVED_COIN)) return "外部进入";
        if(reason.equals(REGISTER_REWARD)) return "注册赠送";
        if(reason.equals(REFERRER_REWARD)) return "推荐奖励";
        if(reason.equals(FREEZE)) return "冻结";
        if(reason.equals(UNFREEZE)) return  "解冻";
        if(reason.equals(TRANSFER)) return "内部转账";
        if(reason.equals(WITHDRAW)) return "外部提现";
        if(reason.equals(MINER_REWARD)) return "挖矿奖励";
        if(reason.equals(COMMUNITY_REWARD))  return "社区奖励";
        if(reason.equals(DISPATCH_RELEASE))  return "拨币释放";
        if(reason.equals(PLAT_SELL_COIN)) return "向平台卖出";
        if(reason.equals(PLAT_BUY_COIN)) return "向平台购买";
        if(reason.equals(COMMUNITY_CONSUME)) return "社区奖励消耗币";
        if(reason.equals(TRANSFER_FEE))  return "转账手续费";
        if(reason.equals(WITHDRAW_FEE))  return "外部提现手续费";
        if(reason.equals(DISCOVERY_REWARD))  return "发现奖励";
        if(reason.equals(MALL_BUY)) return "商城购物冻结";
        if(reason.equals(MALL_RETURN))  return "商城退货解冻";
        if(reason.equals(MALL_SALE))  return "商城销售盈利";
        if(reason.equals(MALL_REBATE)) return "平台促销";
        if(reason.equals(MALL_ONE)) return "消费一级推介奖励";
        if(reason.equals(MALL_TWO))  return "消费一级推介奖励";
        if(reason.equals(MALL_BUY_REWARD))  return "促销冻结奖励";
        if(reason.equals(MALL_RELEASE_REWARD))   return "促销冻结金额释放";
        if(reason.equals(SHOP_ONE))    return "商家一级推荐奖励";
        if(reason.equals(SHOP_TWO))   return "商家二级推荐奖励";

        return "其他";

    }


}
