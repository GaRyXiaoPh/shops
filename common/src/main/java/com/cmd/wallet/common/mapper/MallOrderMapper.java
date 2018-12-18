package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.TMallGoodModel;
import com.cmd.wallet.common.model.TMallOrderModel;
import com.cmd.wallet.common.vo.MallOrderListAdminVO;
import com.cmd.wallet.common.vo.MallOrderListVO;
import com.cmd.wallet.common.vo.MallOrderVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface MallOrderMapper {

    int addMallOrder(TMallOrderModel mallOrderModel);
    @Select("select * from t_mall_order where id = #{id}")
    TMallOrderModel getMallOrder(@Param("id") Integer id);

    @Update("update t_mall_order set status = #{status} where id = #{id}")
    int updateMallOrderStatus(@Param("id") Integer id, @Param("status") Integer status);

    @Update("update t_mall_order set return_status = #{returnStatus},return_reason= #{returnReason} where id = #{id}")
    int updateMallOrderReturnStatus(@Param("id") Integer id, @Param("returnStatus") Integer returnStatus, @Param("returnReason") String returnReason);

    Page<MallOrderListVO> getReturnOrdersByUserId(@Param("userId") Integer userId, RowBounds rowBounds);

    Page<MallOrderListVO> getOrdersByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") Integer status, RowBounds rowBounds);

    Page<MallOrderListVO> getReturnSellerOrdersByUserId(@Param("userId") Integer userId, RowBounds rowBounds);

    Page<MallOrderListVO> getSellerOrdersByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") Integer status, RowBounds rowBounds);

    List<MallOrderListVO> getAllSellerOrdersByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") Integer status);

    MallOrderVO getOrderVOById(@Param("id") Integer id);

    @Select("select * from t_mall_order where seller_id = #{sellerId} and return_status=#{returnStatus}")
    List<TMallOrderModel> getReturnSellerOrders(@Param("sellerId") Integer sellerId, @Param("returnStatus") Integer returnStatus);

    Page<MallOrderListAdminVO> getOrdersByAdmin(@Param("buyer") String buyer, @Param("seller") String seller, @Param("status") Integer status, @Param("returnStatus") Integer returnStatus, @Param("goodName") String goodName, RowBounds rowBounds);

    MallOrderListAdminVO getOrderByAdmin(@Param("id") Integer id);

    @Select("select count(1) from t_mall_order where good_id = #{goodId}\n" +
            "and ((return_status = 0 and status <> 3 and status <> 4)\n" +
            "or (return_status <> 0 and return_status <> 2))")
    Integer getUnfinishedCount(@Param("goodId") Integer goodId);

    @Update("update t_mall_order set reputation = #{reputation} where id = #{id}")
    int updateMallOrderReputation(@Param("id") Integer id, @Param("reputation") Integer reputation);


    @Select(" select * from t_mall_order where id = #{id} and user_id = #{userId}")
    TMallGoodModel getTMallGoodModelById(@Param("id") Integer id,@Param("userId") Integer userId);
//    @Update("update t_mall_order set status = 4 where seller_id = #{id} and status = 1")
//    int cancelOrdersBySellerId(@Param("sellerId")Integer sellerId);
//    @Update("update t_mall_order set return_status = 2 where seller_id = #{sellerId} and return_status = 1")
//    int confirmReturnOrdersBySellerId(@Param("sellerId") Integer sellerId);
}
