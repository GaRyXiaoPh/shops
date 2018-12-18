package com.cmd.wallet.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UserCoinVO implements Serializable {
    private Integer id;
    private Integer userId;
    @ApiModelProperty(value = "币种名称")
    private String coinName;
    private String icon;
    private String bindAddress;
    @ApiModelProperty(value = "可用余额")
    private BigDecimal availableBalance;
    @ApiModelProperty(value = "冻结金额")
    private BigDecimal freezeBalance;
    @ApiModelProperty(value = "转换为指定货币后的可用余额")
    private BigDecimal availableConvert;
    @ApiModelProperty(value = "转换为指定货币后的冻结金额")
    private BigDecimal freezeConvert;

    //用户币种状态，0：正常，1：冻结使用
    private Boolean status;

    @ApiModelProperty(value = "是否开启转账：1开启，0关闭")
    private Integer transferEnable;
    @ApiModelProperty(value = "转出手续费")
    private BigDecimal  transferFeeRate;
    @ApiModelProperty(value = "转出固定手续费")
    private BigDecimal  transferFeeStatic;
    @ApiModelProperty(value = "手续费方式")
    private Integer transferFeeSelect;
    @ApiModelProperty(value = "转出最小数量")
    private BigDecimal transferMinAmount;
    @ApiModelProperty(value = "转出最大数量")
    private BigDecimal transferMaxAmount;
    @ApiModelProperty(value = "提现手续费率")
    private BigDecimal withdrawFeeRate;
    @ApiModelProperty(value = "提现最小数量")
    private BigDecimal withdrawMinAmount;
    @ApiModelProperty(value = "提现最大数量")
    private BigDecimal withdrawMaxAmount;
    @ApiModelProperty(value = "BSTS汇率")
    private BigDecimal changeRate;
    @ApiModelProperty(value = "CNY")
    private BigDecimal moneyCny;
    @ApiModelProperty(value = "USD")
    private BigDecimal moneyUsd;
    private String addressTag;
}
