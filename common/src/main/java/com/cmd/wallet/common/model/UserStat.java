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
public class UserStat implements Serializable {
    private static final long serialVersionUID = -1L;

    private Integer id;
    private Integer userId;
    private Integer leftNodes;
    private Integer rightNodes;
    private BigDecimal leftMoneyAll;
    private BigDecimal rightMoneyAll;
    private BigDecimal moneyAll;
    private Date createTime;
    private Date lastTime;
}
