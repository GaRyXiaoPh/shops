package com.cmd.wallet.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class PlatOrder implements Serializable {
    private static final long serialVersionUID = -1L;

    public static final int TYPE_BUY=1;
    public static final int TYPE_SELL=2;

    //ALL(0, "所有"),
    //MATCHING(2, "匹配中"),
    //MATCHED(3, "匹配成功, 等待接单"),
    //ACCEPTED(4, "已经接单"),
    //PAID(5, "已经付款"),
    //COMPLAINT(6, "申诉中"),
    //FREEZE(7, "冻结"),
    //CANCELED(100, "已经取消"),
    //DONE(1, "交易成功");

    public static final int STATUS_MATCHING=2;       //匹配中
    public static final int STATUS_ACCEPTED=4;      //平台已经接单
    public static final int STATUS_PAID=5;          //已经付款
    public static final int STATUS_COMPLAINT=6;     //订单申诉
    public static final int STATUS_FREEZE=7;        //冻结
    public static final int STATUS_CANCELED=100;    //已经取消
    public static final int STATUS_DONE=1;          //交易成功

    private Integer id;
    @ApiModelProperty("用户ID")
    private Integer userId;
    private String coinName;
    @ApiModelProperty("1买2卖")
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
    @ApiModelProperty("银行之行")
    private String bankNameChild;
    @ApiModelProperty("开户姓名")
    private String bankUser;
    @ApiModelProperty("卡号")
    private String bankNo;
    @ApiModelProperty("订单状态")
    private Integer status;
    @ApiModelProperty("付款码")
    private String payCode;
    private String comment;
    private String cancelComment;
    private Date expireTime;

    private Date createTime;
    private Date lastTime;
}


