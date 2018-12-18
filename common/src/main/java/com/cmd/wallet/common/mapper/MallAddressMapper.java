package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.TMallAddressModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MallAddressMapper {

    @Select("SELECT * FROM t_mall_address WHERE user_id = #{userId} order by is_default DESC, create_time DESC")
    List<TMallAddressModel> getAddressByUserId(@Param("userId") Integer userId);

    @Select("SELECT * FROM t_mall_address WHERE user_id = #{userId} and is_default = 1")
    TMallAddressModel getDefaultAddress(@Param("userId") Integer userId);

    @Insert("insert into t_mall_address(user_id, receiver_name, receiver_mobile, province_id, city_id, area_id, detail_addr,is_default,create_time,update_time) " +
            "values(#{userId},#{receiverName},#{receiverMobile},#{provinceId},#{cityId},#{areaId},#{detailAddr},#{isDefault},NOW(),NOW())")
    int saveMallAddress(TMallAddressModel tMallAddressModel);

    @Delete("delete from t_mall_address where id=#{id} and user_id = #{userId}")
    int delMallAddress(@Param("userId") Integer userId, @Param("id") Integer id);

    @Select("SELECT * FROM t_mall_address WHERE id = #{id}")
    TMallAddressModel getAddressById(@Param("id") Integer id);

    @Update("update t_mall_address set receiver_name = #{receiverName},receiver_mobile = #{receiverMobile},province_id = #{provinceId},city_id = #{cityId},area_id = #{areaId},detail_addr = #{detailAddr},is_default = #{isDefault},update_time = NOW()" +
            "where id = #{id}")
    int updateAddress(TMallAddressModel tMallAddressModel);
    @Update("update t_mall_address set is_default = 0 where user_id = #{userId}")
    int changeDefaultAddress(@Param("userId") Integer userId);
    @Update("update t_mall_address set is_default = 1 where id = #{id}")
    int defaultAddress(@Param("id") Integer id);
}
