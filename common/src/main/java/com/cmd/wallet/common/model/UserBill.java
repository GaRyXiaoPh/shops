package com.cmd.wallet.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserBill implements Serializable {
    private static final long serialVersionUID = -1661830493897252605L;

    public  static final int SUB_TYPE_AVAILABLE = 0;
    public  static final int SUB_TYPE_FREEZE = 1;
    public  static final int SUB_TYPE_AWARD = 3;
    private BigInteger id;
    private Integer userId;
    private String coinName;
    private Boolean subType;
    private String reason;
    private BigDecimal changeAmount;
    private String comment;
    private Date lastTime;
}


