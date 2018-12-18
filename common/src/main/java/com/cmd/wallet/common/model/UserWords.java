package com.cmd.wallet.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class UserWords implements Serializable {

    public static final Integer MEMC_FINISH = 1;
    public static final Integer MEMC_NON=0;

    private Integer id;
    private Integer userId;
    private String words;
    private Date createTime;
    private Date lastTime;
    private Integer status;
}
