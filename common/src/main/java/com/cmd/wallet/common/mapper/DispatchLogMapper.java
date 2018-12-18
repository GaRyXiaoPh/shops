package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.DispatchLog;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

@Mapper
public interface DispatchLogMapper {
    int add(DispatchLog dispatchLog);

    Page<DispatchLog> getDispatchLog(@Param("userId") Integer userId, RowBounds rowBounds);
}
