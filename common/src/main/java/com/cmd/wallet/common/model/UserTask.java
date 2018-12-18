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
public class UserTask implements Serializable{
    private static final long serialVersionUID = -1L;

    public static final int TASK_STAT_NODES=0;          //统计节点
    public static final int TASK_REGISTER_ADDRESS=1;    //注册地址

    private Integer id;
    private Integer userId;
    private Integer type;
    private String params;
    private Integer status;
}