package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.MallCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2018-10-25
 */
@Mapper
public interface MallCategoryMapper {

    @Select("select * from t_mall_category order by `order`")
    List<MallCategory> getAll();
}
