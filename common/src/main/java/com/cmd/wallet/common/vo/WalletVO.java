package com.cmd.wallet.common.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class WalletVO  implements Serializable {

    private String walletName;
    private String icon;
}
