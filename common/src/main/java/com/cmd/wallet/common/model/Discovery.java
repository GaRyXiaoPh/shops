package com.cmd.wallet.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class Discovery implements Serializable {
    private static final long serialVersionUID = -1L;

    public static final Integer STATUS_PUBLISH=1;
    public static final Integer STATUS_PASS=2;
    public static final Integer STATUS_FAIL=3;

    private Integer id;
    private Integer userId;
    private Integer type;
    private String title;
    private String image;
    private String content;
    private Integer status;
    private Date createTime;
    private Date lastTime;
}
