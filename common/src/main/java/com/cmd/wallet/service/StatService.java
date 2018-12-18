package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.UserBillReason;
import com.cmd.wallet.common.mapper.*;
import com.cmd.wallet.common.model.*;
import com.cmd.wallet.common.utils.DateUtil;
import com.cmd.wallet.common.vo.UserCoinVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

//统计服务
@Service
public class StatService {
    private static Logger logger = LoggerFactory.getLogger(StatService.class);

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserCoinService userCoinService;
    @Autowired
    ChangeConfigService changeConfigService;
    @Autowired
    UserStatMapper userStatMapper;
    @Autowired
    UserTaskMapper userTaskMapper;
    @Autowired
    ConfigLevelMapper configLevelMapper;
    @Autowired
    private EarningsDayMapper earningsDayMapper;
    @Autowired
    private UserEarningsMapper userEarningsMapper;
    @Autowired
    ConfigService configService;
    @Autowired
    private RedisTemplate redisTemplate;

    private List<ConfigLevel> ConfigLevelList;


    public static final String KEY_USERSTAT="UserStat";
    public static final String KEY_MINER="DayMiner";
    public static final String KEY_REFERRER="DayReferrer";
    public static final String KEY_COMUNITY="DayComunity";
    public static final String KEY_IS_MINER="IsMiner";

    //备注：结算过程比较复杂依赖redis，不能重启
    //结算入口，这个过程不能中途退出
    public void statReward(){
        String today = DateUtil.getDateTimeString(new Date(), "yyyy-MM-dd");
        clearTempData(today);    //准备结算数据
        todayMoney();       //统计每个用户的总资产

        minering(today);    //挖矿奖励
        departMoney(today); //大小部门计算(依赖挖矿奖励)
        community(today);   //社区奖励
        redisToDB(today);  //回写数据库
    }


    //清理准备叶子节点数据
    private void clearTempData(String today){
        redisTemplate.delete(KEY_USERSTAT);
        redisTemplate.delete(KEY_MINER+today);
        redisTemplate.delete(KEY_REFERRER+today);
        redisTemplate.delete(KEY_COMUNITY+today);
        redisTemplate.delete(KEY_IS_MINER);
        logger.info("StatService::redisTemplate.delete:"+KEY_USERSTAT);
        int count = userMapper.clearLeafNode();
        logger.info("StatService::clearLeafNode:"+count);
        count = userMapper.addLeafNode();
        logger.info("StatService::addLeafNode:"+count);

        ConfigLevelList = configLevelMapper.getConfigList();
    }

    //统计用户当天资产
    private void todayMoney(){
        logger.info("StatService::statUserTodayMoney start......");

        //获取汇率保存
        Map<String, BigDecimal> mRate = changeConfigService.getChangeConfig();

        //开始统计每个用户的总资产
        Integer id = 0;
        do {
            User user = userMapper.getNextUser(id);
            if (user==null) break;

            id = user.getId();

            //获取用户的总资产
            BigDecimal money = BigDecimal.ZERO;
            List<UserCoinVO> list = userCoinService.getUserCoinByUserId(user.getId());
            for (UserCoinVO coin:list){
                if (mRate.containsKey(coin.getCoinName())){
                    BigDecimal rate =  mRate.get(coin.getCoinName());
                    if (rate!=null) {
                        money = money.add(coin.getAvailableBalance().multiply(rate));
                    }
                }else{
                    logger.error("Change config rate not find:"+coin.getCoinName());
                }
            }
            user.setMoneyAll(money).setIsStat(false).setLeftMoneyAll(BigDecimal.ZERO).setRightMoneyAll(BigDecimal.ZERO).setLeftNodes(0).setRightNodes(0);

            //注册后就一定存在这条记录
            userStatMapper.updateUserStat(new UserStat().setUserId(id).setMoneyAll(money));
            userMapper.updateUserByUserId(new User().setId(id).setMoneyAll(money));

            //写入redis
            redisTemplate.opsForHash().put(KEY_USERSTAT, ""+user.getId(), user);
        }while (true);
        logger.info("StatService::statUserTodayMoney money end......");
    }

    //开始统计业绩
    private void departMoney(String today){
        logger.info("StatService::departMoney start......");

        Integer id = 0;
        do {
            Integer userId = userMapper.getNextLeafNode(id);
            if (userId==null) break;
            User user = (User) redisTemplate.opsForHash().get(KEY_USERSTAT, ""+userId);
            if (user==null) break;

            id = user.getId();
            Integer referrer = user.getReferrer();
            Integer invite = user.getInvite();
            String inviteCode = user.getInviteCode();

            Double minerMoney = (Double)redisTemplate.opsForHash().increment(KEY_MINER+today, ""+userId, 0.0);
            if (minerMoney==null)
                minerMoney=0.0;

            Integer nodes = 1;
            BigDecimal moneys = BigDecimal.valueOf(minerMoney.doubleValue());

            do {
                if (referrer==null)  break;

                User next = (User) redisTemplate.opsForHash().get(KEY_USERSTAT, ""+referrer);
                if (next==null) break;

                //logger.info("referrer:"+referrer+",monerys:"+moneys);

                if (referrer.intValue() == invite.intValue() && next.getRightInvite().equalsIgnoreCase(inviteCode)) {
                    //右区
                    next.setRightNodes(next.getRightNodes().intValue()+nodes.intValue());
                    next.setRightMoneyAll(next.getRightMoneyAll().add(moneys));
                } else {
                    //左区
                    next.setLeftNodes(next.getLeftNodes().intValue()+nodes.intValue());
                    next.setLeftMoneyAll(next.getLeftMoneyAll().add(moneys));
                }
                if (next.getIsStat()==false){
                    Double nextmoney = (Double)redisTemplate.opsForHash().increment(KEY_MINER+today, ""+next.getId(), 0.0);
                    if (nextmoney==null)
                        nextmoney=0.0;

                    nodes=nodes+1;
                    moneys = moneys.add(BigDecimal.valueOf(nextmoney.doubleValue()));
                    next.setIsStat(true);
                }
                redisTemplate.opsForHash().put(KEY_USERSTAT, ""+next.getId(), next);
                //logger.info("user:"+next.getId()+",left:"+next.getLeftMoneyAll()+",right:"+next.getRightMoneyAll());

                referrer = next.getReferrer();
                invite = next.getInvite();
                inviteCode = next.getInviteCode();
            } while (true);
            userMapper.delLeafNode(id);
        }while (true);
        logger.info("StatService::departMoney end......");
    }

    //redis中的数据回写数据库
    private void redisToDB(String today){
        String platCoin = configService.getPlatformCoinName();
        ChangeConfig changeConfig = changeConfigService.getCNYChangeConfig(platCoin);
        logger.info("StatService::redisToDB start......");

        if (changeConfig==null || changeConfig.getRate().doubleValue()<=0)
            return;

        //遍历回写数据库，大小区数据
        Set<String> set = (Set<String>)redisTemplate.opsForHash().keys(KEY_USERSTAT);
        for (String uid:set){
            Integer userId = Integer.parseInt(uid);
            User tmp = (User) redisTemplate.opsForHash().get(KEY_USERSTAT, ""+userId);
            if (tmp!=null) {
                userStatMapper.updateUserStat(new UserStat().setUserId(tmp.getId()).setMoneyAll(tmp.getMoneyAll())
                        .setLeftMoneyAll(tmp.getLeftMoneyAll()).setRightMoneyAll(tmp.getRightMoneyAll()));
            }
        }

        //其他数据落地
        Integer id = 0;
        do {
            UserStat userStat = userStatMapper.getNextUserStat(id);
            if (userStat==null) break;

            id = userStat.getId();

            Double miner = (Double) redisTemplate.opsForHash().increment(KEY_MINER+today, ""+userStat.getUserId(), 0.0);
            Double referrer = (Double) redisTemplate.opsForHash().increment(KEY_REFERRER+today, ""+userStat.getUserId(), 0.0);
            Double community = (Double) redisTemplate.opsForHash().increment(KEY_COMUNITY+today, ""+userStat.getUserId(),0.0);

            //转换成豆芽币
            BigDecimal miner_=null, referrer_=null, community_=null;
            if (miner!=null) miner_=BigDecimal.valueOf(miner.doubleValue()).divide(changeConfig.getRate(), 8, RoundingMode.HALF_UP);
            if (referrer!=null) referrer_=BigDecimal.valueOf(referrer.doubleValue()).divide(changeConfig.getRate(), 8, RoundingMode.HALF_UP);
            if (community!=null) community_=BigDecimal.valueOf(community.doubleValue()).divide(changeConfig.getRate(), 8, RoundingMode.HALF_UP);

            if (earningsDayMapper.getEarningsDayByUserIdAndDay(userStat.getUserId(), today)==null){
                earningsDayMapper.add(new EarningsDay().setUserId(userStat.getUserId()).setStatDay(today).setRewardCommunity(BigDecimal.ZERO)
                        .setRewardMiner(BigDecimal.ZERO).setRewardReferrer(BigDecimal.ZERO));
            }
            EarningsDay earningsDay = new EarningsDay().setUserId(userStat.getUserId()).setStatDay(today);
            if (miner_!=null) earningsDay.setRewardMiner(miner_);
            if (referrer_!=null) earningsDay.setRewardReferrer(referrer_);
            if (community_!=null) earningsDay.setRewardCommunity(community_);
            earningsDayMapper.incrementRewardByUserIdAndDay(earningsDay);
        } while (true);

        redisTemplate.expire(KEY_MINER+today, 2, TimeUnit.MINUTES);
        redisTemplate.expire(KEY_REFERRER+today, 2, TimeUnit.MINUTES);
        redisTemplate.expire(KEY_COMUNITY+today, 2, TimeUnit.MINUTES);
        logger.info("StatService::redisToDB end......");
    }


    ////////////////////////////////////////////////////////////////////////
    //统计节点任务
    public void statNodes(){
        do {
            UserTask userTask = userTaskMapper.getUserTask(UserTask.TASK_STAT_NODES);
            if (userTask==null)  break;

            User user = userMapper.getUserByUserId(userTask.getUserId());
            Integer referrer = user.getReferrer();
            Integer invite = user.getInvite();
            String inviteCode = user.getInviteCode();
            logger.info("stat nodes:"+userTask.getUserId()+":"+userTask.getParams());

            //对应的上线一层层处理
            do {
                if (referrer==null)  break;

                //直推人处理，需要判断是否是右节点
                User next = userMapper.getUserByUserId(referrer);
                if (referrer.intValue()==invite.intValue() && next.getRightInvite().equalsIgnoreCase(inviteCode)){
                    userStatMapper.incrementByUserId(new UserStat().setUserId(next.getId()).setRightNodes(Integer.valueOf(1)));
                    logger.info("stat nodes right:"+next.getId());
                }else {
                    userStatMapper.incrementByUserId(new UserStat().setUserId(next.getId()).setLeftNodes(Integer.valueOf(1)));
                    logger.info("stat nodes left:"+next.getId());
                }
                referrer = next.getReferrer();
                invite = next.getInvite();
                inviteCode = next.getInviteCode();
            }while (true);
            userTaskMapper.del(userTask.getId());
        }while (true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //挖矿奖励计算(定时任务)
    private void minering(String today){
        String platCoin = configService.getPlatformCoinName();
        String consumeCoin = configService.getCommunityConsumeCoinName();
        double rate = configService.getMinerReward();
        double freezeRate = configService.getRewardFreezeRate();
        double referrerRate = configService.getReferrerReward();
        ChangeConfig changeConfig = changeConfigService.getCNYChangeConfig(platCoin);
        logger.info("minering start ...... and rate=:"+rate+",freezeRate:"+freezeRate+",paltcoin:"+platCoin+",rate:"+changeConfig.getRate()+",referrerRate:"+referrerRate);

        if (freezeRate<0 || freezeRate>1)
            return;
        if (changeConfig==null || changeConfig.getRate().doubleValue()<=0)
            return;
        if (referrerRate<0 || referrerRate>1)
            return;


        Integer id = 0;
        do {
            UserStat userStat = userStatMapper.getNextUserStat(id);
            if (userStat==null) break;

            id = userStat.getId();

            //判断用户激活等级
            ConfigLevel lev = getUserLevel(userStat.getMoneyAll());
            if (lev==null)
                continue;

            BigDecimal rewardConsume = lev.getConsume();


            //获取冻结部分
            UserEarnings earnings = userEarningsMapper.getUserEarningsByUserId(userStat.getUserId());
            if (earnings==null) {
                logger.error("user earnings don't exist:"+userStat.getUserId());
                continue;
            }

            BigDecimal amountCny=BigDecimal.ZERO;
            BigDecimal amount = BigDecimal.ZERO;
            try {
                if (rewardConsume.doubleValue() > 0) {
                    userCoinService.changeUserCoin(userStat.getUserId(), consumeCoin, rewardConsume.negate(), BigDecimal.ZERO,BigDecimal.ZERO, UserBillReason.COMMUNITY_CONSUME, "挖矿奖励消耗" + consumeCoin);
                    BigDecimal money = userStat.getMoneyAll();
                    amount = money.multiply(BigDecimal.valueOf(rate));
                    amount = amount.divide(changeConfig.getRate(), 9, RoundingMode.HALF_UP);

                    amountCny = money.multiply(BigDecimal.valueOf(rate));
                }
            }catch (Exception e){
                ;//没有ENG11，直接释放冻结部分，amount=0
                amount = BigDecimal.ZERO;
                amountCny = BigDecimal.ZERO;
            }

            BigDecimal amountAll = amount.add(earnings.getFreezeReward());  //加上冻结的一起按照比例释放
            if (amountAll.doubleValue()<=0)
                continue;

            BigDecimal reward, giveAll, freezeAll;
            if (amountAll.doubleValue()<0.01){
                reward = amountAll;
            }else {
                reward = amountAll.multiply(BigDecimal.valueOf(1 - freezeRate));
            }
            reward = BigDecimal.valueOf(reward.doubleValue());
            giveAll= reward.add(earnings.getGiveReward());
            freezeAll = amountAll.subtract(reward);
            freezeAll = freezeAll.setScale(8, RoundingMode.HALF_UP);

            //换算成对应的平台币BSTS
            userCoinService.changeUserCoin(userStat.getUserId(), platCoin, BigDecimal.valueOf(reward.doubleValue()), BigDecimal.ZERO,BigDecimal.ZERO, UserBillReason.MINER_REWARD, "挖矿奖励");
            userEarningsMapper.updateUserEarnings(new UserEarnings().setUserId(userStat.getUserId()).setGiveReward(giveAll).setFreezeReward(freezeAll));
            redisTemplate.opsForHash().increment(KEY_MINER+today, ""+userStat.getUserId(), amountCny.doubleValue());
            if (amount.doubleValue()>0){
                redisTemplate.opsForHash().put(KEY_IS_MINER, ""+userStat.getUserId(), Integer.valueOf(1));
            }

            //分享奖励//分享奖励有比例
            amount = amount.multiply(BigDecimal.valueOf(referrerRate));
            amountCny = amountCny.multiply(BigDecimal.valueOf(referrerRate));
            User user = userMapper.getUserByUserId(userStat.getUserId());
            if (user!=null && user.getInvite()!=null && amount.doubleValue()>0){
                UserEarnings inviteEarnings = userEarningsMapper.getUserEarningsByUserId(user.getInvite());
                if (inviteEarnings==null) {
                    logger.error("user invite earnings don't exist:"+user.getId()+","+user.getInvite());
                    continue;
                }
                User userInvite=(User) redisTemplate.opsForHash().get(KEY_USERSTAT, ""+user.getInvite());
                if (userInvite==null || !isMiner(userInvite))
                    continue;

                amountAll = amount.add(inviteEarnings.getFreezeReward());
                if (amountAll.doubleValue()<=0)
                    continue;

                if (amountAll.doubleValue()<0.01){
                    reward = amountAll;
                }else {
                    reward = amountAll.multiply(BigDecimal.valueOf(1 - freezeRate));
                }
                reward = BigDecimal.valueOf(reward.doubleValue());
                giveAll = reward.add(inviteEarnings.getGiveReward());
                freezeAll = amountAll.subtract(reward);
                freezeAll = freezeAll.setScale(8, RoundingMode.HALF_UP);

                //换算成对应的平台币BSTS
                userCoinService.changeUserCoin(user.getInvite(), platCoin, reward, BigDecimal.ZERO,BigDecimal.ZERO, UserBillReason.REFERRER_REWARD, "分享奖励");
                userEarningsMapper.updateUserEarnings(new UserEarnings().setUserId(user.getInvite()).setGiveReward(giveAll).setFreezeReward(freezeAll));
                redisTemplate.opsForHash().increment(KEY_REFERRER+today, ""+user.getInvite(), amountCny.doubleValue());
            }

        } while (true);
        logger.info("minering end ......");
    }

    //定时计算社区奖励
    private void community(String today) {
        String platCoin = configService.getPlatformCoinName();
        double freezeRate = configService.getRewardFreezeRate();
        ChangeConfig changeConfig = changeConfigService.getCNYChangeConfig(platCoin);
        logger.info("statCommunity start ......freezeRate:"+freezeRate+",platCoin:"+platCoin+",rate:"+changeConfig.getRate());

        if (freezeRate<0 || freezeRate>1)
            return;
        if (changeConfig==null || changeConfig.getRate().doubleValue()<=0)
            return;

        Integer id = 0;
        do {
            UserStat userStat = userStatMapper.getNextUserStat(id);
            if (userStat==null) break;

            id=userStat.getId();

            User user = (User) redisTemplate.opsForHash().get(KEY_USERSTAT, ""+userStat.getUserId());
            if (user==null) continue;

            Integer isMiner = (Integer) redisTemplate.opsForHash().get(KEY_IS_MINER, ""+userStat.getUserId());
            if (isMiner==null || isMiner.intValue()!=1) continue;

            //判断用户激活等级
            ConfigLevel lev = getUserLevel(userStat.getMoneyAll());
            if (lev==null)
                continue;

            BigDecimal rewardRate = lev.getRate();
            if (rewardRate.doubleValue()<=0)
                continue;

            //小部门，社区奖励
            BigDecimal minDepart = user.getLeftMoneyAll().compareTo(user.getRightMoneyAll())>0 ? user.getRightMoneyAll():user.getLeftMoneyAll();
            BigDecimal reward = minDepart.multiply(rewardRate);
            if (reward.doubleValue()<=0)
                continue;

            //换算成对应的平台币BSTS
            BigDecimal reward_tmp = reward.divide(changeConfig.getRate(),8,RoundingMode.HALF_UP);
            userCoinService.changeUserCoin(userStat.getUserId(), platCoin, reward_tmp, BigDecimal.ZERO, BigDecimal.ZERO,UserBillReason.COMMUNITY_REWARD, "社区奖励");
            userEarningsMapper.incrementRewardByUserId(new UserEarnings().setUserId(userStat.getUserId()).setGiveReward(reward_tmp));
            redisTemplate.opsForHash().increment(KEY_COMUNITY+today, ""+userStat.getUserId(), reward.doubleValue());
        } while (true);
        logger.info("statCommunity end ......");
    }

    //获取用户等级
    private ConfigLevel getUserLevel(BigDecimal moneyAll){
        ConfigLevel levlast = null;
        for (ConfigLevel lev:ConfigLevelList){
            if (moneyAll.doubleValue()>=lev.getMinAmount() && moneyAll.doubleValue()<lev.getMaxAmount()){
                return lev;
            }
            levlast=lev;
        }
        return levlast;
    }

    //获取用户挖矿权限
    private boolean isMiner(User user){
        //如果当天有挖矿说明有权限
        Integer isMiner = (Integer) redisTemplate.opsForHash().get(KEY_IS_MINER, ""+user.getId());
        if (isMiner!=null && isMiner.intValue()==1)
            return true;

        ConfigLevel lev = getUserLevel(user.getMoneyAll());
        if (lev==null || lev.getId()<2)
            return false;

        //获取用户的挖矿权限
        UserCoinVO vo = userCoinService.getUserCoinByUserIdAndCoinName(user.getId(), Coin.ENG11);
        if (vo==null)
            return false;
        if (vo.getAvailableBalance().doubleValue()<lev.getConsume().doubleValue())
            return false;
        return true;
    }

}
