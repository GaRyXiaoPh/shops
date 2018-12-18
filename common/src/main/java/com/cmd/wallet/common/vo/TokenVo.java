package com.cmd.wallet.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TokenVo {
    @ApiModelProperty("token")
    private String token;
    @ApiModelProperty("token过期时间")
    private Date expiretTime;
    @ApiModelProperty("是否绑定邮箱")
    private boolean bindEmail;
    @ApiModelProperty("是否绑定手机")
    private boolean bindMobile;
    @ApiModelProperty("是否绑定支付密码")
    private boolean bindPaypwd;
    @ApiModelProperty("是否实名认证")
    private boolean realNameAuth;
    @ApiModelProperty("是否助记词认证")
    private boolean isMnemonic;

}
