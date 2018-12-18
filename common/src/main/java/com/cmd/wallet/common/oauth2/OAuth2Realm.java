package com.cmd.wallet.common.oauth2;

import com.cmd.wallet.common.constants.AdminStatus;
import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.exception.ServerException;
import com.cmd.wallet.common.mapper.UserMapper;
import com.cmd.wallet.common.model.User;
import com.cmd.wallet.common.model.UserToken;
import com.cmd.wallet.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lwx 2017/09/01
 */
@Component
public class OAuth2Realm extends AuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    @Autowired
    private UserMapper userMapper;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //TODO
        return null;
    }


    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        OAuth2Token oAuth2Token = (OAuth2Token) token ;
        String _token = (String) token.getPrincipal();
        UserToken userToken = userMapper.getUserTokenByToken(_token);
        if (userToken == null) {
            throw new ServerException(ErrorCode.ERR_TOKEN_NOT_EXIST, "Token 失效");
        }
        if(userToken.getExpireTime().getTime() < System.currentTimeMillis()) {
            throw new ServerException(ErrorCode.ERR_TOKEN_EXPIRE_TIME, "Token 失效");
        }
        User user=userMapper.getUserByUserId(userToken.getUserId());
        if(user.getStatus() == AdminStatus.DISABLE.getValue()){
            throw new ServerException(ErrorCode.ERR_USER_DISABLE, "用户被禁用");
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user,_token,getName());
        return info;
    }

}
