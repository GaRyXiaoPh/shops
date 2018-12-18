package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.PlatOrder;
import com.cmd.wallet.common.vo.PlatOrderVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.Date;
import java.util.List;

@Mapper
public interface PlatOrderMapper {

    int add(PlatOrder platOrder);
    int updatePlatOrder(PlatOrder platOrder);
    PlatOrder getPlatOrder(@Param("id") Integer id);
    PlatOrder lockPlatOrder(@Param("id") Integer id);

    Page<PlatOrder> getPlatOrderList(@Param("userId") Integer userId, @Param("coinName") String coinName, @Param("status") Integer[] status, RowBounds rowBounds);
    Page<PlatOrderVO> getPlatOrderVOList(@Param("userId") Integer userId, @Param("coinName") String coinName, @Param("status") Integer status, RowBounds rowBounds);

    List<PlatOrder> getPlatOrderExpire(@Param("now") Date now);

}
