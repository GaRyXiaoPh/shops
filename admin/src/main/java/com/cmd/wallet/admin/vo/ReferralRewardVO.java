package com.cmd.wallet.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class ReferralRewardVO {
    private Integer id;
    private String coinName;
    @ApiModelProperty("推荐人电话")
    private String referrerMobile;
    @ApiModelProperty("被推荐人电话")
    private String mobile;
    @ApiModelProperty("奖励数量")
    private BigDecimal rewardAmount;
    @ApiModelProperty("推荐时间")
    private Date rewardTime;
}
