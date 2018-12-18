package com.cmd.wallet.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class CoinLastVO {
    @ApiModelProperty("币名")
    private String coinName;
    @ApiModelProperty("对换币名")
    private String changeName;
    @ApiModelProperty("最新价格")
    private BigDecimal LastPrice;
    @ApiModelProperty("最新美元价格")
    private BigDecimal LastUsdPrice;
    @ApiModelProperty("当日涨跌")
    private BigDecimal changeRate;
    private String icon;
}
