package com.cmd.wallet.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class PayOrderVO {
    @ApiModelProperty(value = "订单类型：购物车CART，立即购买ONCE")
    private String type;
    @ApiModelProperty(value = "商品ID")
    private Integer goodId;
    @ApiModelProperty(value = "购买数量")
    private Integer count;
    @ApiModelProperty(value = "地址ID")
    private Integer addressId;
    @ApiModelProperty(value = "支付密码")
    private String payPassword;
    @ApiModelProperty(value = "短信验证码")
    private String smsCode;
    @ApiModelProperty(value = "购物车ID集合")
    private Integer[] cartIds;
    @ApiModelProperty("币种名称")
    private String coin;
}
