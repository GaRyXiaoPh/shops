package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.mapper.PlatConfigMapper;
import com.cmd.wallet.common.model.PlatConfig;
import com.cmd.wallet.common.utils.Assert;
import com.github.pagehelper.Page;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlatConfigService {
    @Autowired
    PlatConfigMapper platConfigMapper;

    public Page<PlatConfig> getPlat(Integer pageNo,Integer pageSize)
    {
        return  platConfigMapper.getPlat(new RowBounds(pageNo,pageSize));
    }
    public PlatConfig getPlatDetailByCoinName(String coinName){
        return  platConfigMapper.getPlatConfig(coinName);
    }
    public void updatePlatConfig(PlatConfig platConfig){
        platConfigMapper.updatePlatConfig(platConfig);
    }
    public void addPlatConfig(PlatConfig platConfig){
        PlatConfig tmp = platConfigMapper.getPlatConfig(platConfig.getCoinName());
        Assert.check(tmp != null, ErrorCode.ERR_RECORD_EXIST, "币种名称已存在");
        platConfigMapper.addPlatConfig(platConfig);
    }
}
