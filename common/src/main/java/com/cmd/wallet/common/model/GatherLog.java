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
public class GatherLog implements Serializable {
    private static final long serialVersionUID = -1L;

    private long       id;
    private String      fromAddress;
    private String      toAddress;
    private String      coinName;
    private String      txid;
    private BigDecimal  amount;
    private Integer     type;
    private Date        createTime;
}
