package com.cmd.wallet.common.model;

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
public class ConfigLevel implements Serializable {
    private static final long serialVersionUID = -1L;

    private Integer     id;
    private String      level;
    private Integer     minAmount;
    private Integer     maxAmount;
    private BigDecimal  rate;
    private BigDecimal  consume;
}
