package com.cmd.wallet.common.vo;

import com.cmd.wallet.common.model.ConfigLevel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class ConfigVo {
    @ApiModelProperty(value = "注册奖励")
    private String registerReward;
    @ApiModelProperty(value = "推荐奖励")
    private String referrerReward;
    @ApiModelProperty(value = "超时时间")
    private String expireTime;
    @ApiModelProperty("挖矿比例")
    private String minerReward;
    @ApiModelProperty("每人最大激活数量")
    private String userMaxInvest;
    @ApiModelProperty("每人最小激活数量")
    private String userMinInvest;
    @ApiModelProperty(value = "奖励百分比")
    private List<ConfigLevel> configLevels;


}
