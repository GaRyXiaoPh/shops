package com.cmd.wallet.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel("商品列表")
public class MallGoodListVO {
    @ApiModelProperty("商品ID")
    private Integer id;
    @ApiModelProperty("商品名称")
    private String name;
    @ApiModelProperty("商品单价")
    private BigDecimal price;
    @ApiModelProperty("人民币价值")
    private BigDecimal cny;
    @ApiModelProperty(value = "计价币种，大写，不传默认ENG11")
    private String coinName;
    @ApiModelProperty("缩略图")
    private String imgUrl;
    @ApiModelProperty("库存")
    private Integer stock;
    @ApiModelProperty("销量")
    private Integer salesVolume;
    @ApiModelProperty("分类ID")
    private Integer categoryId;
    @ApiModelProperty("商家ID")
    private Integer userId;
    @ApiModelProperty("商店名称")
    private String shopName;

    //新增用于显示
    @ApiModelProperty("商品单价")
    private BigDecimal eng11;

    @ApiModelProperty("商品单价")
    private BigDecimal bsts;
}
