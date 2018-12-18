package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.MallShop;
import org.apache.ibatis.annotations.*;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2018-10-25
 */
@Mapper
public interface MallShopMapper  {

    @Select("select * from t_mall_shop where user_id = #{userId}")
    MallShop getMallShopByUserId(@Param("userId") Integer userId);
    @Insert("insert into t_mall_shop(user_id,shop_avatar,shop_name,shop_intro) values (#{userId},#{shopAvatar},#{shopName},#{shopIntro})")
    int addMallShop(MallShop mallShop);

    int updateMallShop(MallShop mallShop);
    @Update("update t_mall_shop set shop_good_rept = shop_good_rept+1 where user_id = #{userId}")
    int addGoodRept(@Param("userId") Integer userId);
    @Update("update t_mall_shop set shop_good_rept = shop_good_rept-1 where user_id = #{userId}")
    int cutGoodRept(@Param("userId") Integer userId);

    @Update("update t_mall_shop set shop_middle_rept = shop_middle_rept+1 where user_id = #{userId}")
    int addMiddleRept(@Param("userId") Integer userId);
    @Update("update t_mall_shop set shop_middle_rept = shop_middle_rept-1 where user_id = #{userId}")
    int cutMiddleRept(@Param("userId") Integer userId);

    @Update("update t_mall_shop set shop_bad_rept = shop_bad_rept+1 where user_id = #{userId}")
    int addBadRept(@Param("userId") Integer userId);
    @Update("update t_mall_shop set shop_bad_rept = shop_bad_rept-1 where user_id = #{userId}")
    int cutBadRept(@Param("userId") Integer userId);
}
