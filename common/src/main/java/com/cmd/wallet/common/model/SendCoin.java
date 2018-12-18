package com.cmd.wallet.common.model;

import com.cmd.wallet.common.enums.SendCoinStatus;
import io.swagger.annotations.ApiModelProperty;
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
public class SendCoin implements Serializable {
    private static final long serialVersionUID = -1402494593;

    private Integer id; //转出钱的用户id

    private Integer userId;
    private String userName;    //用户名
    //收款的用户id，只有在内部转账的时候才会有
    private Integer receivedUserId;
    //收款的地址
    private String address;
    //币种名称
    private String coinName;
    //转账的交易id
    private String txid;
    //转账外部的金额大小
    private BigDecimal amount;
    //转账费用
    private BigDecimal fee;
    //发送的时间，一般是记录创建时间
    private Date sendTime;
    //状态，1：已经成功发出
    private SendCoinStatus status;
    private Date lastTime;
    private Integer type;
    private String comment;

    @ApiModelProperty("是否内部转账")
    public boolean isInnerTransfer() {
        if(txid == null) {
            return false;
        }
        return txid.toLowerCase().startsWith("inner-");
    }
}


