package com.cmd.wallet.common.vo;

import com.cmd.wallet.common.constants.UserBillReason;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class UserBillVO implements Serializable {
    private Integer id;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "用户账号")
    private String userName;
    @ApiModelProperty(value = "币种名称")
    private String coinName;
    @ApiModelProperty(value = "账户特性， true:冻结余额变化 false:代表可用余额变化")
    private Boolean subType;
    @ApiModelProperty(value = "资金变化原因")
    private String reason;
    @ApiModelProperty(value = "交易数量，负数表示支出")
    private BigDecimal changeAmount;
    @ApiModelProperty(value = "交易详情")
    private String comment;
    @ApiModelProperty(value = "交易时间")
    private Date lastTime;
    @ApiModelProperty(value = "资金变化原因转换")
    private String reasonStr;

    public String getReasonStr(){
       return UserBillReason.getReasonStr(this.reason);

    }



}
