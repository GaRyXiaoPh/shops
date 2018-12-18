package com.cmd.wallet.api.task;

import com.cmd.wallet.service.EthService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

//同步ETH
@Component
public class EthSync {
    private Log log = LogFactory.getLog(this.getClass());

    // 第一次等待时间，6秒
    private final int firstWaitTime = 1000 * 7;
    // 平时等待时间，60秒
    private final int defaultWaitTime = 1000 * 120;

    @Autowired
    private EthService etcService;

    @PostConstruct
    public void init() {
        EthSync task = this;
        new Thread("EthSync") {
            public void run() {
                task.run();
            }
        }.start();
    }

    public void run() {
        int waitTime = firstWaitTime;
        log.warn("Begin sync eth thread");
        while(true) {
            try{
                Thread.sleep(waitTime);
                syncEth();
                waitTime = defaultWaitTime;
            }catch(Throwable th) {
                log.error("", th);
            }
        }
    }

    // 开始同步
    public void syncEth() {
        log.debug("Begin sync eth New Transactions");
        etcService.syncNewTransactions();
        log.debug("End sync eth New Transactions");
    }
}
