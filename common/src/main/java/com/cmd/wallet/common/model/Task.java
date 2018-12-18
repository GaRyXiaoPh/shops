package com.cmd.wallet.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Task implements Serializable {
    private static final long serialVersionUID = -1661830493897252605L;

    public static final int TASK_STATUS_UNRUN = 0;
    public static final int TASK_STATUS_START=1;
    public static final int TASK_STATUS_SUCCESS=2;
    public static final int TASK_STATUS_FAIL = 3;

    //任务ID
    public static final int TASK_CHANGE=1;  ////系统管理员强制兑换

    private Integer id;
    private Integer type;
    private String params;
    private Integer status;
    private Date createTime;
    private Date lastTime;
}
