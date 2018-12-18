package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.CartGoodModel;
import com.cmd.wallet.common.model.CartModel;
import org.apache.ibatis.annotations.*;

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
public interface MallCartMapper {

    @Select("select * from t_mall_cart where user_id = #{userId} order by id")
    List<CartModel> getMallCartByUserId(@Param("userId") Integer userId);

    @Insert("insert into t_mall_cart(user_id,good_id,good_name,number,cny,list_pic_url,shop_id,shop_name) values(#{userId},#{goodId},#{goodName},#{number},#{cny},#{listPicUrl},#{shopId},#{shopName})")
    int save(CartModel cartModel);

    int update(CartModel cartModel);

    @Select("select * from t_mall_cart where id = #{id} order by id limit 0,1")
    CartModel getMallCartById(@Param("id")Integer id);

    @Select("select * from t_mall_cart where user_id = #{userId} and good_id = #{goodId} order by id limit 0,1")
    CartModel getMallCartByUserIdAndGoodId(@Param("userId")Integer userId, @Param("goodId")Integer goodId);

    @Delete("delete from t_mall_cart where id = #{id}")
    int delete(@Param("id") Integer id);


}
