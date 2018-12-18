package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.Config;
import com.cmd.wallet.common.model.ConfigLevel;
import org.apache.ibatis.annotations.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ConfigLevelMapper {

    @Select("select * from t_config_level where id=#{id}")
    ConfigLevel getConfigByName(@Param("id") Integer id);

    @Select("select * from t_config_level")
    List<ConfigLevel> getConfigList();

    @Update("update t_config_level set rate=#{rate},consume=#{consume} where level=#{level}")
    void updateLevelConfig(@Param("level") String level, @Param("rate") BigDecimal rate, @Param("consume")BigDecimal consume);

    @Select("select * from t_config_level where #{amount}>=min_amount AND max_amount>#{amount}")
    ConfigLevel getConfigLevelByAmount(@Param("amount")BigDecimal amount);
}
