package com.cmd.wallet.common.task;

import com.cmd.wallet.common.constants.UserBillReason;
import com.cmd.wallet.common.mapper.*;
import com.cmd.wallet.common.model.DispatchJob;
import com.cmd.wallet.common.model.DispatchLog;
import com.cmd.wallet.service.UserCoinService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MyDispatchJob implements Job {
    private static Log log = LogFactory.getLog(MyDispatchJob.class);

    DispatchJobMapper  dispatchJobMapper;
    DispatchLogMapper dispatchLogMapper;
    UserCoinService userCoinService;

    //初始化Bean
    private void initBean(){
        if (dispatchJobMapper==null)
            dispatchJobMapper =  SpringContextUtils.getBean(DispatchJobMapper.class);
        if (dispatchLogMapper==null)
            dispatchLogMapper = SpringContextUtils.getBean(DispatchLogMapper.class);
        if (userCoinService==null)
            userCoinService = SpringContextUtils.getBean(UserCoinService.class);
    }

	@Override
    @Transactional
	public void execute(JobExecutionContext arg0) {
        log.info("MyDispatchJob execute......"+arg0.toString());

        initBean();
	    try {
            JobKey key = arg0.getJobDetail().getKey();
            JobDataMap dataMap = arg0.getJobDetail().getJobDataMap();

            Integer jobid = dataMap.getIntValue("jobid");
            List<DispatchJob> job = dispatchJobMapper.getDispatchJob(jobid);
            log.info("MyDispatchJob execute: jobid"+jobid+"--->"+job);

            if (job!=null && job.size()>0){
                log.info("MyDispatchJob execute:"+job.size());

                for (DispatchJob dispatch : job){
                    BigDecimal amountAll = dispatch.getAmountAll();
                    BigDecimal tmp =  amountAll.multiply(dispatch.getFreeRate());

                    //最后一次全部拨完
                    if (dispatch.getAmount().doubleValue()<tmp.doubleValue())
                        tmp = dispatch.getAmount();

                    log.info("MyDispatchJob execute free:"+tmp);
                    //释放金币，给用户添加钱
                    userCoinService.changeUserCoin(dispatch.getUserId(), dispatch.getCoinName(), tmp, BigDecimal.ZERO, BigDecimal.ZERO,UserBillReason.DISPATCH_RELEASE, "拨币释放");
                    //userCoinMapper.changeUserCoin(dispatch.getUserId(), dispatch.getCoinName(), tmp, BigDecimal.ZERO);
                    //拨币扣减
                    dispatchJobMapper.freeDispatch(dispatch.getId(), tmp);
                    //拨币记录
                    dispatchLogMapper.add(new DispatchLog().setUserId(dispatch.getUserId()).setMobile(dispatch.getMobile())
                            .setCoinName(dispatch.getCoinName()).setAmount(tmp));
                }
            }
        } catch (Exception e){
            log.info("MyDispatchJob:执行定时拨币释放任务异常:"+e.toString());
        }
	}

}
