package com.cmd.wallet.common.vo;


import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class UserEarningsVO {
    private BigDecimal giveReward;
    private BigDecimal freezeReward;
    private BigDecimal availableBalance;
}
