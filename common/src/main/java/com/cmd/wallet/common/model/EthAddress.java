package com.cmd.wallet.common.model;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class EthAddress {
    private int id;
    private int userId;
    private String address;
    private String password;
    private String fileName;
    private String credentials;
    private Date createTime;
}
