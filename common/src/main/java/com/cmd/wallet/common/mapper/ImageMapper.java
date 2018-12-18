package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.TImageModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ImageMapper {

    int addImages(@Param("list") List<TImageModel> images);

    int deleteImgByRefIdAndType(@Param("refrenceId") Integer refrenceId, @Param("type") Integer type);

    @Select("SELECT * FROM t_image t where t.refrence_id = #{refrenceId} AND t.type = #{type}")
    List<TImageModel> getImgByRefIdAndType(@Param("refrenceId") Integer refrenceId, @Param("type") Integer type);

}
