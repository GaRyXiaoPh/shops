package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.Discovery;
import com.cmd.wallet.common.model.UserWords;
import com.cmd.wallet.common.vo.DiscoveryVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface DiscoveryMapper {

    int add(Discovery discovery);
    int del(@Param("id")Integer id);
    int updateDiscovery(Discovery discovery);
    Discovery getDiscoveryById(@Param("id") Integer id);
    Discovery lockDiscoveryById(@Param("id") Integer id);

    DiscoveryVO getDiscoveryVOById(@Param("id")Integer id);
    Page<DiscoveryVO> getDiscovery(@Param("userId")Integer userId, @Param("status")Integer status, RowBounds rowBounds);
}
