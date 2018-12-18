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
public class ReceivedCoin implements Serializable{
    private static final long serialVersionUID = -1L;
    private Integer id;
    private Integer userId;
    private String userName;
    private String address;
    private String coinName;
    private String txid;
    private BigDecimal amount;
    private BigDecimal fee;
    private Integer txTime;
    private Date receivedTime;
    private Integer status;
    private Integer type;
    private String fromAddress;
}
