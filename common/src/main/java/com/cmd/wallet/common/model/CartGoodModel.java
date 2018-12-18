package com.cmd.wallet.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-08-13 10:41:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CartGoodModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("主键")
    private Integer id;
    @ApiModelProperty("会员Id")
    private Integer userId;
    @ApiModelProperty("商品Id")
    private Integer goodId;
    @ApiModelProperty("产品名称")
    private String goodName;
    @ApiModelProperty("人民币价值")
    private BigDecimal cny;
    @ApiModelProperty("数量")
    private Integer number;
    @ApiModelProperty("商品图片")
    private String listPicUrl;
    @ApiModelProperty("店铺ID")
    private Integer shopId;
    @ApiModelProperty("店铺名称")
    private String shopName;
    @ApiModelProperty("添加时间")
    private Date addTime;
    @ApiModelProperty("eng11商品单价")
    private BigDecimal eng11;
    @ApiModelProperty("bsts商品单价")
    private BigDecimal bsts;
    @ApiModelProperty
    private Integer stock;

}
