package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.PlatBank;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface PlatBankMapper {

    int addPlatBank(PlatBank platBank);
    List<PlatBank> getPlatBankList();
    int updatePlatBank(PlatBank platBank);
    PlatBank getPlatBankById(@Param("id") Integer id);
    Page<PlatBank> getPlatBankPage(RowBounds rowBounds);
    void delPlatConfigById(@Param("id") Integer id);
}
