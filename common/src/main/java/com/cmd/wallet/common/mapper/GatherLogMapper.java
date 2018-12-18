package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.GatherLog;
import com.cmd.wallet.common.model.Task;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GatherLogMapper {
    @Insert("insert into t_gather_log(from_address, to_address, coin_name, txid, amount, type, create_time) " +
            "values(#{fromAddress},#{toAddress},#{coinName},#{txid},#{amount},#{type},NOW())")
    int add(GatherLog gatherLog);
}
