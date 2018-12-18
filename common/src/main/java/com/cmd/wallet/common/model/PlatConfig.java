package com.cmd.wallet.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class PlatConfig implements Serializable {
    private static final long serialVersionUID = -1L;

    private Integer id;
    @ApiModelProperty("币名")
    private String coinName;
    @ApiModelProperty("价格")
    private BigDecimal buyPrice;
    private BigDecimal sellPrice;
    @ApiModelProperty("订单最小")
    private BigDecimal orderAmountMin;
    @ApiModelProperty("订单最大")
    private BigDecimal orderAmountMax;
    private Integer expireMinute;

    private Date createTime;
    private Date lastTime;

    private String startTime;
    private String endTime;
}


