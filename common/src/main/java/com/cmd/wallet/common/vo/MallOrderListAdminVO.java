package com.cmd.wallet.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel("订单列表列表")
public class MallOrderListAdminVO {
    @ApiModelProperty("订单ID")
    private Integer id;
    @ApiModelProperty("支付时间")
    private Date createTime;
    @ApiModelProperty("商品ID")
    private Integer goodId;
    @ApiModelProperty("商品名称")
    private String name;
    @ApiModelProperty("付款金额")
    private BigDecimal totalPrice;
    @ApiModelProperty(value = "计价币种，大写，默认ENG11")
    private String coinName;
    @ApiModelProperty("商品数量")
    private Integer count;
    @ApiModelProperty("商品单价")
    private BigDecimal price;
    @ApiModelProperty("1:待发货；2:已发货；3:已完成,4取消订单,5：退货中，6：已退货")
    private Integer status;
    @ApiModelProperty("退货状态：0:正常，1：退货中，2：已退货")
    @JsonIgnore
    private Integer returnStatus;
    @ApiModelProperty("买家ID")
    private Integer buyerId;
    @ApiModelProperty("买家名称")
    private String buyerName;
    @ApiModelProperty("卖家ID")
    private Integer sellerId;
    @ApiModelProperty("卖家名称")
    private String sellerName;
    @ApiModelProperty("商家发货时间")
    private Date sendTime;
    @ApiModelProperty("申请退货时间")
    private Date returnTime;
    @ApiModelProperty("收货人姓名")
    private String receiverName;
    @ApiModelProperty("收货人手机号")
    private String receiverMobile;
    @ApiModelProperty("省份")
    private String provinceId;
    @ApiModelProperty("城市")
    private String cityId;
    @ApiModelProperty("区县")
    private String areaId;
    @ApiModelProperty("详细地址")
    private String detailAddr;
    @ApiModelProperty("商家发货凭证")
    private String sendImg;
    @ApiModelProperty("买家退货凭证")
    private String returnImg;
    @ApiModelProperty("退货原因")
    private String returnReason;

}
