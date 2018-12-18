package com.cmd.wallet.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class UserEarnings implements Serializable {

    private Integer id;
    private Integer userId;
    private BigDecimal giveReward;
    private BigDecimal freezeReward;
}
