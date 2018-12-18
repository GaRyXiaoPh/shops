package com.cmd.wallet.api.task;

import com.cmd.wallet.service.UsdtService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

//同步比特币
@Component
public class UsdtSync {
    private Log log = LogFactory.getLog(this.getClass());
    // 第一次等待时间，5秒
    private final int firstWaitTime = 1000 * 5;
    // 平时等待时间，120秒
    @Value("${task.sync.usdt.time:120000}")
    private final int defaultWaitTime = 1000 * 120;

    @Autowired
    private UsdtService usdtService;

    @PostConstruct
    public void init() {
        log.info("init UsdtSync");
        UsdtSync task = this;
        new Thread("UsdtSync") {
            public void run() {
                task.run();
            }
        }.start();
    }

    public void run() {
        int waitTime = firstWaitTime;
        log.warn("Begin sync usdt thread");
        while(true) {
            try{
                Thread.sleep(waitTime);
                syncUsdt();
                waitTime = defaultWaitTime;
            }catch(Throwable th) {
                log.error("", th);
            }
        }
    }

    // 开始同步
    public void syncUsdt() {
        log.debug("Begin sync usdt New Transactions");
        usdtService.syncNewTransactions();
        log.debug("End sync usdt New Transactions");
    }
}
