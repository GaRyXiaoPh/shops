package com.cmd.wallet.common.constants;

/**
 * 定义各种配置Key的值
 */
public class ConfigKey {

    // 在交易平台撮合交易的任务名称
    public static final String  TASK_NAME_TRADE_MATCH        = "TradeBgMatchTask";

    // 后台分红的任务名称
    public static final String  TASK_NAME_SHARE_OUT_BONUS    = "ShareOutBonusTask";

    // 上次更新市场昨天最后成交价的时间
    public static final String UPDATE_MARKET_LAST_DAY_PRICE = "UpdateMarketLastDayPrice";

    // 上次撮合到的trade id
    public static final String LAST_MATCH_TRADE_ID            = "LastMatchTradeId";

    // 上次取消过期交易的任务
    public static final String LAST_CANCEL_TRADE_TASK        = "LastCancelTradeTask";

    // 没有完成的交易有效期，单位是秒
    public static final String TRADE_VALID_TIME               = "TradeValidTime";

    // 上次同步到的区块高度前缀，后面接币种名称才是真正的key名称
    public static final String BC_LAST_SYNC_BLOCK_PRE        = "LastSyncBlock_";

    // 上次同步到的eth区块高度前缀，后面接币种名称才是真正的key名称
    public static final String BC_LAST_SYNC_ETH_PRE          = "LastSyncEth_";

    // 最后一个已经挖矿的交易id，避免重复挖矿
    public static final String LAST_MINE_TRADE_ID            = "trade.mining.last_id";
    // 挖矿后释放相对挖矿而外释放平台币的比例数
    public static final String MINE_EXT_RELEASE_RATE         = "mining.ext.release.rate";
    // 挖矿后释放相对挖矿而外释放平台币的ID
    public static final String MINE_EXT_RELEASE_ID            = "mining.ext.release.id";

    public static final String REGISTER_REWARD          = "user.register.reward";
    public static final String REFERRER_REWARD          = "user.referrer.reward";
    public static final String REFERRER_URL          = "user.referrer.url";
    public static final String PLATFORM_COIN_NAME          = "platform.coin.name";
    public static final String MINER_REWARD             = "user.miner.reward";
    public static final String COMMUNITY_DELAY          ="user.community.delay";
    public static final String USER_INVEST_MAX           ="user.invest.max";
    public static final String USER_INVEST_MIN          ="user.invest.min";
    public static final String MALL_GOOD_MAX            ="mall.good.max";
    public static final String MALL_SEARCH_MAX            ="mall.search.max";

    public static final String COMMUNTIY_CONSUME_COIN_NAME ="community.consume.coin.name";
    public static final String DISCOVERY_REWARD         ="discovery.reward";
    public static final String REWARD_FREEZE_RATE          ="reward.freeze.rate";
    public static final String DEFAULT_HEAD_IMAGE          ="default.head.image";


    // 交易市场的基本币种
    public static final String MARKET_BASE_COIN_NAME          = "market.base.coin";
    public static final String BCN_PRICE          = "bcn.price";
    // 交易挖矿返佣（返平台币）百分比， 20代表20%
    public static final String TRADE_MINING_REWARD = "trade.mining.reward";
    // 交易挖矿推荐返佣（返平台币）百分比， 20代表20%，就是一个交易的后有奖励，他的推荐人也有奖励
    public static final String TRADE_MINING_REC_REWARD = "trade.mining.recommend.reward";
    // 交易(分红)返还手续费用（交易币种）百分比， 20代表20%
    public static final String TRADE_FEE_BONUS          = "trade.fee.reward";
    // 已经分红的截止时间
    public static final String TRADE_SHARE_OUT_END_TIME = "trade.share.out.end.time";
    // 交易(分红)返还手续费用（交易币种）百分比， 20代表20%
    public static final String TRADE_MIN_SHOUT_COIN          = "trade.min.shout.coin";
    //推荐奖励排名 -- 后台配置排名数据
    public static final String REFERRER_REWARD_RANKING          = "referrer.reward.ranking";
    // 以太坊某个账号余额大于等于这个金额的时候将会回收这个账号的以太币到基础账号
    public static final String ETH_GATHER_MIN_BALANCE           = "eth.gather.min.balance";
    // 回收账号以太币的时候，剩余这个数量，主要用于做交易费用的，避免交易失败
    public static final String ETH_GATHER_REMAIN                 = "eth.gather.remain";
    // 以太坊某个账号余额大于等于这个金额的时候将会回收这个账号的以太币到基础账号
    public static final String ETC_GATHER_MIN_BALANCE           = "etc.gather.min.balance";
    // 回收账号以太币的时候，剩余这个数量，主要用于做交易费用的，避免交易失败
    public static final String ETC_GATHER_REMAIN                 = "etc.gather.remain";
    // 以太坊某个账号余额大于等于这个金额的时候将会回收这个账号的以太币到基础账号
    public static final String USDT_GATHER_MIN_BALANCE           = "usdt.gather.min.balance";
    //官方微信名片图片链接
    public static final String OFFICIAL_WX_LINK                 = "official_group.wx_image_link";
    //官方qq群链接
    public static final String OFFICAL_QQ_LINK                 = "official_group.qq_link";
    // 1个ETH兑换成平台币（BON）的数量（属性非通用）
    public static final String ETH_TO_PLAT_NUM                  = "convert.eth.to.plat";
    // 1个USDT兑换成平台币（BON）的数量（属性非通用）
    public static final String USDT_TO_PLAT_NUM                 = "convert.usdt.to.plat";
    // 兑换成平台币的时候冻结的百分比，20表示20%，小于1的话直接当做百分比使用
    public static final String CONVERT_FREEZE_PERCENT          = "convert.freeze.percent";
    // 支持兑换的时间
    public static final String CONVERT_BON_TIME                 = "convert.plat.time";
    // 支持释放的开始时间
    public static final String CONVERT_BON_RELEASE_TIME        = "convert.release.time";
    // 收集各种币所扫描到的最后一个id前缀
    public static final String GATHER_LAST_RECV_ID_PRE         = "gather.last.id.";
    // 收到币的时候发送短信，0表示不发送
    public static final String SEND_MSG_ON_RECV_COIN           = "send.msg.recv.coin";
    // 收到币的时候发送短信格式，例如“平台已经收到你的币:%f”
    public static final String SEND_MSG_ON_RECV_COIN_FORMAT   = "send.msg.recv.coin.format";
    // ETH转账指定gas price
    public static final String ETH_GAS_PRICE                    = "eth.gas.price";
    public static final String ETH_GAS_LIMIT                    = "eth.gas.limit";
    // 兑换商充值提币超时时间（秒）
    public static final String MERCHANT_ORDER_TIMEOUT          = "merchant.order.timeout";
    public static final String EXPIRE_MINUTE                    = "expire.minute";
    public static final String ENGT_TO_ENG11                   = "engt.to.eng11";
    public static final String ENG_TO_ENG11                    = "eng.to.eng11";
    public static final String ENG11_TO_ENGT                   = "eng11.to.engt";

    public static final String USER_BUY_REBATE                  = "user.buy.rebate"; //开启促销返利

    public static final String USER_REFERRER_LEVEL_ONE         = "user.referrer.level.one"; //一级推荐奖励

    public static final String USER_REFERRER_LEVEL_TWO         = "user.referrer.level.two"; //二级推荐奖励

    public static final String USER_BUY_REBATE_RELEASE_RATE       = "user.buy.rebate.release.rate"; //促销奖励每日释放率

    public static final String MIN_RELEASE_NUM                   ="min.release.num";//促销释放最小释放数目

    public static final String LAST_RELEASE_TIME                  ="last.release.time";//最新释放的时间，用于标记今日是否释放过，因为定时器为单线程，其他定时器导致此释放冻结的未释放预备份。


}
