package com.cmd.wallet.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ChangeConfig implements Serializable {
    private static final long serialVersionUID = -1L;

    private Integer id;
    private String coinName;
    private String changeName;
    private BigDecimal rate;
    private BigDecimal cnyRate;
    private BigDecimal usdRate;
    private BigDecimal changeRate;
    private Date createTime;
    private Date lastTime;
    private BigDecimal amount;
    private Integer status;
}
