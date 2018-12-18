package com.cmd.wallet.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class UserBank implements Serializable {
    private static final long serialVersionUID = -1L;

    private Integer id;
    private Integer userId;
    private Integer bankType;
    private String bankName;
    private String bankNameChild;
    private String bankUser;
    private String bankNo;
    private Integer status;
    private Date createTime;
    private Date lastTime;
}
