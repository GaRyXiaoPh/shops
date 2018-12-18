package com.cmd.wallet.common.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class DiscoveryVO {
    private Integer id;
    private Integer userId;
    private Integer type;
    private String title;
    private String image;
    private String content;
    private Integer status;
    private Date createTime;
    private Date lastTime;

    private String userName;
}

