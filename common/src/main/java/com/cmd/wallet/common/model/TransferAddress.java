package com.cmd.wallet.common.model;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class TransferAddress implements Serializable {
    private static final long serialVersionUID = -1L;

    private Integer id;
    private Integer userId;
    private String coinName;
    private String address;
    private String name;
    private Integer status;
    private String comment;
    private Date createTime;
    private Date lastTime;
    private String addressTag;
    private String icon;
}
