package com.cmd.wallet.common.mapper;

import com.cmd.wallet.common.model.User;
import com.cmd.wallet.common.model.UserToken;
import com.cmd.wallet.common.vo.UserInfoVO;
import com.cmd.wallet.common.vo.UserVo;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.session.RowBounds;

import java.util.Date;
import java.util.List;

@Mapper
public interface UserMapper {

    int addUserToken(UserToken userToken);
    int updateUserToken(UserToken userToken);
    UserToken getUserTokenByUserId(@Param("userId")int userId);
    UserToken getUserTokenByToken(@Param("token")String token);
    int disableUserToken(@Param("userId")int userId, @Param("expireTime")Date expireTime);

    int addUser(User user);

    //TODO: 电话号码可能不是唯一的，后续应该都改成根据区号+号码查询
    User getUserByMobile(@Param("mobile") String mobile);
    User getUserByEmail(@Param("email")String email);
    User getUserByUserId(@Param("userId")int userId);
    User getUserByUserName(@Param("userName")String userName);
    User getUserByInviteCode(@Param("inviteCode") String inviteCode);
    User getUserByNickName(@Param("nickName")String nickName);

    int updateUserByUserId(User user);

    List<User> adminGetUserByMobile(@Param("mobile") List<String> mobile);
    Page<UserInfoVO> getUserList(@Param("mobile") String mobile, @Param("email")String email, @Param("userId")Integer userId, RowBounds rowBounds);
    Integer getUserReferrerCount(@Param("userId") Integer userId);
    User userExits(@Param("id") Integer id,@Param("userName") String userName,@Param("mobile") String mobile,@Param("email") String email);

    @Update("update t_user set sales_permit = #{salesPermit} where id = #{id}")
    int updateUserSalesPermit(@Param("id") Integer id,@Param("salesPermit") Integer salesPermit);

    User getNextUser(@Param("userId") Integer userId);

    int clearLeafNode();
    int addLeafNode();
    int delLeafNode(@Param("userId")Integer userId);
    Integer getNextLeafNode(@Param("userId")Integer userId);
    @Select("select count(*) from  t_user")
    Integer countUser();
}
