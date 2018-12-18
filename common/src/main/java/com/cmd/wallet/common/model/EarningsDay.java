package com.cmd.wallet.common.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class EarningsDay implements Serializable {
    private static final long serialVersionUID = -1L;

    private Integer id;
    private Integer userId;
    private String statDay;
    private BigDecimal rewardMiner;
    private BigDecimal rewardReferrer;
    private BigDecimal rewardCommunity;
}
