package com.cmd.wallet.api.task;

import com.cmd.wallet.common.constants.ConfigKey;
import com.cmd.wallet.common.mapper.ConfigMapper;
import com.cmd.wallet.common.model.Config;
import com.cmd.wallet.common.utils.DateUtil;
import com.cmd.wallet.service.ReleaseAwardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class ReleaseTask {

    @Autowired
    private ReleaseAwardService releaseAwardService;

    @Autowired
    private ConfigMapper configMapper;

    @Scheduled(cron = "0 0 0 * * ?")
    public void releaseAwardStart(){
        startRelease();
    }
    //每半个小时执行便利一次，防止凌晨未执行释放
    @Scheduled(cron = "0 */30 * * * ?")
    public void retrieveRelease(){
        startRelease();
    }

    public void startRelease(){
        int currentTime =   (int)(DateUtil.getDateByString("00:00:00").getTime()/1000);
        Config config = configMapper.getConfigByName(ConfigKey.LAST_RELEASE_TIME);
        int configTime = Integer.valueOf(config.getConfValue());
        if(currentTime>configTime) {
            if (releaseAwardService.relaseBool) {
                long startTime = System.currentTimeMillis();
                log.info("start release award banlance");

                releaseAwardService.releaseAwardBanlance();
                long endTime = System.currentTimeMillis();
                //修改配置文件配置信息
                log.info(" release award banlance end need time i", (endTime - startTime) / 1000 + "s");
            }
        }
    }

}
