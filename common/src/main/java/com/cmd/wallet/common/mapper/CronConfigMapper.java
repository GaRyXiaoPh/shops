package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.CronConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CronConfigMapper {

    int add(CronConfig cronConfig);
    CronConfig getCronConfig(@Param("id") Integer id);
    List<CronConfig> getCronConfigList();

}


