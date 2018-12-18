package com.cmd.wallet.api.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TaskThread {
    private Log log = LogFactory.getLog(this.getClass());

    // 第一次等待时间，6秒
    private final int firstWaitTime = 1000 * 7;
    // 平时等待时间，60秒
    private final int defaultWaitTime = 1000 * 120;


    @PostConstruct
    public void init() {
        TaskThread task = this;
        new Thread("TaskThread") {
            public void run() {
                task.run();
            }
        }.start();
    }

    public void run() {
        int waitTime = firstWaitTime;
        log.warn("Begin task thread");
        while(true) {
            try{
                Thread.sleep(waitTime);
                runTask();
                waitTime = defaultWaitTime;
            }catch(Throwable th) {
                log.error("", th);
            }
        }
    }

    public void runTask() {
        log.info("Begin task start.......");
        log.info("End task end......");
    }
}
