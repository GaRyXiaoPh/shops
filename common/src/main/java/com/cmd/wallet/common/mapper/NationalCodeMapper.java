package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.NationalCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NationalCodeMapper {

    @Select("select * from t_national_code")
    List<NationalCode> getNationalCode();
}
