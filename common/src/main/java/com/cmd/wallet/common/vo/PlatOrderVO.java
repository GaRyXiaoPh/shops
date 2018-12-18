package com.cmd.wallet.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class PlatOrderVO {

    private Integer id;
    @ApiModelProperty("用户ID")
    private Integer userId;
    private String userName;
    @ApiModelProperty("1买2卖")
    private String coinName;
    private Integer type;
    @ApiModelProperty("数量")
    private BigDecimal amount;
    @ApiModelProperty("手续费")
    private BigDecimal fee;
    @ApiModelProperty("价格")
    private BigDecimal price;
    @ApiModelProperty("1银行卡2支付宝3微信")
    private Integer bankType;
    @ApiModelProperty("银行名称")
    private String bankName;
    @ApiModelProperty("开户姓名")
    private String bankUser;
    @ApiModelProperty("卡号")
    private String bankNo;
    @ApiModelProperty("订单状态")
    private Integer status;
    private String comment;
    private String cancelComment;
    private Date expireTime;
    private String payCode;
    private Date createTime;
    private Date lastTime;
}
