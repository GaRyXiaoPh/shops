package com.cmd.wallet.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class TMallOrderModel {
    @ApiModelProperty(value = "主键ID,添加不要传入，编辑必传")
    private Integer id;
    @ApiModelProperty(value = "用户ID，请求不需要")
    private Integer userId;
    @ApiModelProperty(value = "卖家ID，请求不需要")
    private Integer sellerId;
    @ApiModelProperty(value = "商品ID")
    private Integer goodId;
    @ApiModelProperty(value = "购买数量")
    private Integer count;
    @ApiModelProperty(value = "地址ID")
    private Integer addressId;
    @ApiModelProperty(value = "总价值，请求不需要")
    private BigDecimal totalPrice;
    @ApiModelProperty(value = "订单状态，请求不需要")
    private Integer status;
    @ApiModelProperty(value = "创建时间，请求不需要")
    private Date createTime;
    @ApiModelProperty(value = "创建时间，请求不需要")
    private Date updateTime;
    private Integer isDelete;
    private Integer returnStatus;
    private String receiverName;
    private String receiverMobile;
    private String provinceId;
    private String cityId;
    private String areaId;
    private String detailAddr;
    private BigDecimal price;
    private String coinName;
    private String returnReason;
    @ApiModelProperty(value = "评价")
    private Integer reputation;
}
