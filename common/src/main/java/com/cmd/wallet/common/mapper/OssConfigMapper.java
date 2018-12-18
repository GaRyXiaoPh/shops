package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.OssConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OssConfigMapper {

    @Select("select * from t_oss_config where name=#{name}")
    OssConfig getOssConfigByName(@Param("name") String name);

    @Update("update t_oss_config set token=#{token} where name=#{name}")
    int updateOssConfig(@Param("name") String name, @Param("token") String token);

    @Select("select * from t_oss_config")
    List<OssConfig> getOssConfigList();
}
