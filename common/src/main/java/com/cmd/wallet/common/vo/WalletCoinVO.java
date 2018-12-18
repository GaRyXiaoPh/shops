package com.cmd.wallet.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class WalletCoinVO {

    @ApiModelProperty("钱包名称")
    private String walletName;
    @ApiModelProperty("钱包地址")
    private String address;
    @ApiModelProperty("钱包总资产")
    private BigDecimal balance;
    @ApiModelProperty("钱包币种")
    private List<UserCoinVO> list;
}
