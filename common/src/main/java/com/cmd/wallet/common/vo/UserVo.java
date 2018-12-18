package com.cmd.wallet.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class UserVo {
    @ApiModelProperty("用户id")
    private Integer id;
    @ApiModelProperty("用户名")
    private String UserName;
    @ApiModelProperty("注册时间")
    private Date registerTime;
    @ApiModelProperty("engt可用余额")
    private BigDecimal engtBalance;
    @ApiModelProperty("激活engt可用余额")
    private BigDecimal engtFreeze;
    @ApiModelProperty("eng11可用余额")
    private BigDecimal balance;
    @ApiModelProperty("eng11可用余额")
    private BigDecimal freezeBalance;
    @ApiModelProperty("邀请人数")
    private Integer referrerCount;
    @ApiModelProperty("充币数量")
    private BigDecimal receiveCoin;
    @ApiModelProperty("提币数量")
    private BigDecimal sendCoin;
    @ApiModelProperty("eng可用余额")
    private BigDecimal engBalance;
    @ApiModelProperty("eng冻结余额")
    private BigDecimal engFreeze;
    @ApiModelProperty("是否开启销售权限：0未开启，1开启")
    private Integer salesPermit;
    @ApiModelProperty("自品牌专区")
    private Integer brandPermit;    //自品牌专区许可
    @ApiModelProperty("全平台")
    private Integer globalPermit;    //全平台访问许可
    private Integer status;
}

