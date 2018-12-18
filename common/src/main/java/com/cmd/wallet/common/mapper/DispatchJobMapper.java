package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.DispatchJob;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface DispatchJobMapper {
    int add(DispatchJob dispatchJob);

    List<DispatchJob> getDispatchJob(@Param("jobId") Integer jobId);
    int freeDispatch(@Param("id") Integer id, @Param("amount") BigDecimal amount);

    Page<DispatchJob> getDispatchJobList(RowBounds rowBounds);
}
