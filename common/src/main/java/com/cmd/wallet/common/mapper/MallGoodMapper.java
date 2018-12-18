package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.TMallGoodModel;
import com.cmd.wallet.common.vo.MallGoodListVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.session.RowBounds;

@Mapper
public interface MallGoodMapper {

    int addMallGood(TMallGoodModel good);

    int updateMallGoodById(TMallGoodModel good);

    @Select("select count(1) from t_mall_good where user_id=#{userId} AND status=1")
    int getMallGoodCountByUserId(@Param("userId") Integer userId);

    Page<MallGoodListVO> getOnSaleGoodsLikeName(@Param("name") String name, @Param("categoryId") Integer categoryId,
                                                @Param("priceOrderStr") String priceOrderStr, @Param("saleNumStr") String saleNumStr,
                                                RowBounds rowBounds);

    @Select("SELECT tmg.*,tms.shop_name,tms.shop_avatar FROM t_mall_good tmg inner join t_mall_shop tms on tmg.user_id=tms.user_id WHERE tmg.id = #{id}")
    TMallGoodModel getGoodById(@Param("id") Integer id);

    Page<MallGoodListVO> getGoodsByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") Integer status, RowBounds rowBounds);

    Page<TMallGoodModel> getGoodsList(@Param("userName") String userName, @Param("goodName") String goodName, @Param("status") Integer status, @Param("isDelete") Integer isDelete, RowBounds rowBounds);
    @Update("update t_mall_good set status = #{status} where user_id = #{userId}")
    int changeGoodsStatusByUserId(@Param("userId") Integer userId, @Param("status") Integer status);


}
