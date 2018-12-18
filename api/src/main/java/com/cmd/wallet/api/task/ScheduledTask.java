package com.cmd.wallet.api.task;

import com.cmd.wallet.common.mapper.UserTaskMapper;
import com.cmd.wallet.common.model.PlatOrder;
import com.cmd.wallet.common.model.UserTask;
import com.cmd.wallet.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ScheduledTask {
    private static Log log = LogFactory.getLog(ScheduledTask.class);

    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private StatService statService;
    @Autowired
    private UserTaskMapper userTaskMapper;

    //注册生成地址
    @Scheduled(fixedDelay = 10*1000)
    public void registerAddress(){
        do {
            UserTask userTask = userTaskMapper.getUserTask(UserTask.TASK_REGISTER_ADDRESS);
            if (userTask==null) break;

            try {
                walletService.registerAddress(userTask.getUserId());
            }catch (Exception e){
                log.error("", e);
            }
            userTaskMapper.del(userTask.getId());
        } while (true);
    }

    //统计节点
    @Scheduled(fixedDelay = 60*1000)
    public void statNodes() {
        try{
            statService.statNodes();
        }catch (Exception e){
            log.info("ScheduledTask::statNodes throw exception:"+e.getMessage(), e);
        }
    }

//    @Scheduled(cron = "${cmd.wallet.reward}")
    public void reward(){
        log.info("ScheduledTask::reward start");
        try{
            statService.statReward();
        } catch (Exception e) {
            log.error("ScheduledTask::reward throw exception:"+e.getMessage(), e);
        }
        log.info("ScheduledTask::reward end");
    }

    @Scheduled(fixedDelay = 120*1000)
    public void getMarketTick(){
        log.info("ScheduledTask::getMarketTick start");
        try{
            crawlerService.getMarketTick();
        }catch (Exception e){
            log.error("ScheduledTask::getMarketTick throw exception:"+e.getMessage(), e);
        }
    }

    @Scheduled(fixedDelay = 3600*1000)
    public void getUsdToCny(){
        log.info("ScheduledTask::getUsdToCny start");
        try{
            crawlerService.getUsdToCny();
        }catch (Exception e){
            log.error("ScheduledTask::getUsdToCny throw exception:"+e.getMessage(), e);
        }
    }
}
