package com.cmd.wallet.service;

import com.cmd.wallet.common.mapper.PlatBankMapper;
import com.cmd.wallet.common.model.PlatBank;
import com.github.pagehelper.Page;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatBankService {
    @Autowired
    PlatBankMapper platBankMapper;

    //添加银行卡
    public void addPlatBank(PlatBank platBank){
        platBankMapper.addPlatBank(platBank);
    }

    //修改银行卡
    public void updatePlatBank(PlatBank platBank){
        platBankMapper.updatePlatBank(platBank);
    }

    //获取随机银行卡
    PlatBank getPlatBankRandom(){
        List<PlatBank> list = platBankMapper.getPlatBankList();
        if (list==null || list.size()==0)
            return null;

        int random = (int)(Math.random()*100);
        random=random%list.size();
        return list.get(random);
    }
    public Page<PlatBank> getList(Integer pageNo, Integer pageSize){
        Page<PlatBank> list = platBankMapper.getPlatBankPage(new RowBounds(pageNo,pageSize));
        return  list;
    }

    public PlatBank getPlatBankById(Integer id){
        return platBankMapper.getPlatBankById(id);
    }

    public void delPlatConfigById(Integer id) {
        platBankMapper.delPlatConfigById(id);
    }

}
