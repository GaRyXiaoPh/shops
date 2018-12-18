package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.MallApply;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.session.RowBounds;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2018-10-25
 */
@Mapper
public interface MallApplyMapper {


    @Select("select * from t_mall_apply t where t.user_id = #{userId}")
    MallApply findByUserId(@Param("userId") Integer userId);

    @Select("select * from t_mall_apply t where t.id = #{id}")
    MallApply findById(@Param("id") Integer id);


    @Insert("insert into t_mall_apply(user_id,contacts,phone,status)values(#{userId},#{contacts},#{phone},#{status})")
    @Options(useGeneratedKeys=true, keyProperty="id")
    int addMallApply(MallApply mallApply);

    int update(MallApply mallApply);

    Page<MallApply> findAll(@Param("status") Integer status, RowBounds rowBounds);
}
