package com.cmd.wallet.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-08-13 10:41:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CartModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("主键")
    @JsonIgnore
    private Integer id;
    @ApiModelProperty("会员Id")
    @JsonIgnore
    private Integer userId;
    @ApiModelProperty("商品Id")
    @JsonIgnore
    private Integer goodId;
    @ApiModelProperty("产品名称")
    @JsonIgnore
    private String goodName;
    @ApiModelProperty("人民币价值")
    @JsonIgnore
    private BigDecimal cny;
    @ApiModelProperty("数量")
    @JsonIgnore
    private Integer number;
    @ApiModelProperty("商品图片")
    @JsonIgnore
    private String listPicUrl;
    @ApiModelProperty("店铺ID")
    private Integer shopId;
    @ApiModelProperty("店铺名称")
    private String shopName;
    @ApiModelProperty("添加时间")
    @JsonIgnore
    private Date addTime;
    @ApiModelProperty("eng11商品单价")
    @JsonIgnore
    private BigDecimal eng11;
    @ApiModelProperty("bsts商品单价")
    @JsonIgnore
    private BigDecimal bsts;



    @ApiModelProperty("商品列表")
    private List<CartGoodModel> cartGoodModelList;

}
