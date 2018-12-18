package com.cmd.wallet.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ConfigVO {
    @ApiModelProperty(value = "注册奖励个数")
    @NotNull
    private Integer registerReward;
    @ApiModelProperty(value = "推荐奖励个数")
    @NotNull
    private Integer referrerReward;
    @ApiModelProperty(value = "交易返佣， 20代表20%")
    @NotNull
    private BigDecimal tradeReward;
    @ApiModelProperty(value = "BCB单价")
    private BigDecimal bcnPrice;
    @NotNull
    @ApiModelProperty(value = "排行第一的用户电话")
    @NotBlank
    private String firstRewardPhone;
    @ApiModelProperty(value = "排行第一的平台币数量")
    @NotNull
    private BigDecimal firstRewardAmount;
    @ApiModelProperty(value = "排行第二的用户电话")
    @NotBlank
    private String secondRewardPhone;
    @ApiModelProperty(value = "排行第二的平台币数量")
    @NotNull
    private BigDecimal secondRewardAmount;
    @ApiModelProperty(value = "排行第三的用户电话")
    @NotBlank
    private String thirdRewardPhone;
    @ApiModelProperty(value = "排行第三的平台币数量")
    @NotNull
    private BigDecimal thirdRewardAmount;
    @ApiModelProperty(value = "微信名片链接")
    private String wxImageLink;
    @ApiModelProperty(value = "qq链接")
    private String qqLink;
    @ApiModelProperty(value = "1个ETH兑换成平台币（BON）的数量")
    private BigDecimal ethToPlatNum;
    @ApiModelProperty(value = "1个USDT兑换成平台币（BON）的数量")
    private BigDecimal usdtToPlatNum;
    @ApiModelProperty(value = "平台币")
    private String platformCoinName;
}
