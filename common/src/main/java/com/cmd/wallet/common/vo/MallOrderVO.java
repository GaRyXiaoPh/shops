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
@ApiModel("订单详情")
public class MallOrderVO {
    @ApiModelProperty("订单ID")
    private Integer id;
    @ApiModelProperty("商品ID")
    private Integer goodId;
    @ApiModelProperty("商品名称")
    private String name;
    @ApiModelProperty("商品库存")
    private Integer stock;
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
    @ApiModelProperty("付款时间")
    private Date createTime;
    @ApiModelProperty("收货人姓名")
    private String receiverName;
    @ApiModelProperty("收货人手机号")
    private String receiverMobile;
    @ApiModelProperty("省ID")
    private String provinceId;
    @ApiModelProperty("市ID")
    private String cityId;
    @ApiModelProperty("区县ID")
    private String areaId;
    @ApiModelProperty("详细地址")
    private String detailAddr;
    @ApiModelProperty("1:待发货；2:已发货；3:已完成,4取消订单,5：退货中，6：已退货")
    private Integer status;
    @ApiModelProperty("0:正常，1：退货中，2：已退货")
    @JsonIgnore
    private Integer returnStatus;
    @ApiModelProperty("退货原因")
    private String returnReason;
    @ApiModelProperty("发货凭证")
    private String sendImg;
    @ApiModelProperty("退货凭证")
    private String returnImg;
}
