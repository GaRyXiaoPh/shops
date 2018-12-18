package com.cmd.wallet.api.task;

import com.cmd.wallet.common.constants.ConfigKey;
import com.cmd.wallet.service.EosService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

//同步柚子币
@Component
public class EosSync {
    private Log log = LogFactory.getLog(this.getClass());
    // 第一次等待时间，6秒
    private final int firstWaitTime = 1000 * 7;
    // 平时等待时间，60秒
    private final int defaultWaitTime = 1000 * 120;

    @Autowired
    private EosService eosService;

    @PostConstruct
    public void init() {
        log.info("init EosSync");
        EosSync task = this;
        new Thread("EosSync") {
            public void run() {
                task.run();
            }
        }.start();
    }

    public void run() {
        int waitTime = firstWaitTime;
        log.warn("Begin sync eos thread");
        while(true) {
            try{
                Thread.sleep(waitTime);
                syncEos();
                waitTime = defaultWaitTime;
            }catch(Throwable th) {
                log.error("", th);
            }
        }
    }

    // 开始同步
    public void syncEos() {
        log.debug("Begin sync eos New Transactions");
        eosService.eosJob();
        log.debug("End sync eos New Transactions");
    }
}
