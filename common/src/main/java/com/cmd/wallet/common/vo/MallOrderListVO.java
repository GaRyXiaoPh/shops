package com.cmd.wallet.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel("订单列表列表")
public class MallOrderListVO {
    @ApiModelProperty("订单ID")
    private Integer id;
    @ApiModelProperty("商品名称")
    private String name;
    @ApiModelProperty("商品数量")
    private Integer count;
    @ApiModelProperty("付款金额")
    private BigDecimal totalPrice;
    @ApiModelProperty("人民币价值")
    private BigDecimal cny;
    @ApiModelProperty(value = "计价币种，大写，默认ENG11")
    private String coinName;
    @ApiModelProperty("缩略图")
    private String imgUrl;
    @ApiModelProperty("退货状态：0:正常，1：退货中，2：已退货")
    private Integer returnStatus;
}
