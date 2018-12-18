package com.cmd.wallet.service;


import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.constants.UserBillReason;
import com.cmd.wallet.common.mapper.DiscoveryMapper;
import com.cmd.wallet.common.model.Coin;
import com.cmd.wallet.common.model.Discovery;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.common.vo.DiscoveryVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class DiscoveryService {

    @Autowired
    DiscoveryMapper discoveryMapper;
    @Autowired
    UserCoinService userCoinService;
    @Autowired
    ConfigService configService;




    public void addDiscovery(Integer userId, Discovery discovery){
        discovery.setUserId(userId);
        discoveryMapper.add(discovery);
    }

    public void delDiscovery(Integer userId, Integer id){
        Discovery discovery = discoveryMapper.getDiscoveryById(id);
        Assert.check(discovery!=null && discovery.getUserId().intValue()!=userId.intValue(), ErrorCode.ERR_PARAM_ERROR);
        discoveryMapper.del(id);
    }

    public DiscoveryVO getDiscoveryById(Integer id){
        return discoveryMapper.getDiscoveryVOById(id);
    }

    public Page<DiscoveryVO> getDiscovery(Integer userId, Integer status, Integer pageNo, Integer pageSize){
        return discoveryMapper.getDiscovery(userId, status, new RowBounds(pageNo, pageSize));
    }



    @Transactional
    public void adminCheckDiscoveryPass(Integer id){
        Discovery discovery = discoveryMapper.lockDiscoveryById(id);
        Assert.check(discovery==null, ErrorCode.ERR_PARAM_ERROR);
        Assert.check(discovery.getStatus().intValue()!=Discovery.STATUS_PUBLISH, ErrorCode.ERR_PARAM_ERROR);

        double reward = configService.getDisconveryReward();
        if (reward>0) {
            int base = Math.round(new Date().getTime()) % (BigDecimal.valueOf(reward).intValue()) + 1;
            userCoinService.changeUserCoin(discovery.getUserId(), Coin.ENG11, BigDecimal.valueOf(base), BigDecimal.ZERO, BigDecimal.ZERO,UserBillReason.DISCOVERY_REWARD, "发布文章奖励:" + base);
        }
        discoveryMapper.updateDiscovery(new Discovery().setId(id).setStatus(Discovery.STATUS_PASS));
    }
    @Transactional
    public void adminCheckDiscoveryFail(Integer id){
        Discovery discovery = discoveryMapper.lockDiscoveryById(id);
        Assert.check(discovery==null, ErrorCode.ERR_PARAM_ERROR);
        Assert.check(discovery.getStatus().intValue()!=Discovery.STATUS_PUBLISH, ErrorCode.ERR_PARAM_ERROR);

        discoveryMapper.updateDiscovery(new Discovery().setId(id).setStatus(Discovery.STATUS_FAIL));
    }
    public void adminDelDiscovery(Integer id){
        Discovery discovery = discoveryMapper.getDiscoveryById(id);
        Assert.check(discovery==null , ErrorCode.ERR_PARAM_ERROR);
        discoveryMapper.del(id);
    }
}
