package com.cmd.wallet.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class TMallGoodModel {
    private static final long serialVersionUID = -1L;

    private Integer id;
    private Integer userId;
    private String name;
    private String detail;
    private BigDecimal price;
    private String coinName;
    private Integer stock;
    private String sellerWechat;
    private String sellerMobile;
    private String shopAddress;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Integer isDelete;
    private Integer salesVolume;
    private Integer categoryId;
    private String userName;
    private String shopName;
    private String shopAvatar;
    private BigDecimal cny;
}
