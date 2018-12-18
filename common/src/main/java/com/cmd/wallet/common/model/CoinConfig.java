package com.cmd.wallet.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CoinConfig implements Serializable{
    private static final long serialVersionUID = -1L;

    private long      id;
    @ApiModelProperty(value = "币种名称")
    private String     coinName;
    @ApiModelProperty(value = "提现手续费率")
    private BigDecimal transferFeeRate;
    @ApiModelProperty(value = "提现手续费固定值")
    private BigDecimal transferFeeStatic;
    @ApiModelProperty(value = "提现手续费选择0费率，1固定")
    private Integer transferFeeSelect;
    @ApiModelProperty(value = "提现最小")
    private BigDecimal transferMinAmount;
    @ApiModelProperty(value = "提现最小")
    private BigDecimal transferMaxAmount;
    @ApiModelProperty(value = "节点确认数")
    private Integer nodeConfirmCount;
    @ApiModelProperty("当天最大提币数量")
    private BigDecimal maximumAmountDay;
    @ApiModelProperty("当天最大提币次数")
    private Integer maximumNumberDay;
    @ApiModelProperty("是否开启转账：1开启，0关闭")
    private Integer transferEnable;
    @ApiModelProperty("提币最小值")
    private BigDecimal withdrawFeeRate;
    @ApiModelProperty("提币最小值")
    private BigDecimal withdrawMinAmount;
    @ApiModelProperty("提币最大值")
    private BigDecimal withdrawMaxAmount;

    private Integer gatherEnable;
    private BigDecimal gatherMin;
    private BigDecimal gatherBase;
}
