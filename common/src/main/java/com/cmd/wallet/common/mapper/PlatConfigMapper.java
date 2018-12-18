package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.PlatConfig;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

@Mapper
public interface PlatConfigMapper {
    int addPlatConfig(PlatConfig platConfig);
    int updatePlatConfig(PlatConfig platConfig);
    PlatConfig getPlatConfig(@Param("coinName") String coinName);
    Page<PlatConfig> getPlat(RowBounds rowBounds);
}

