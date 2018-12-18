package com.cmd.wallet.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CoinVO {
    private Integer id;
    // 币种名称
    @NotBlank
    private String name;
    // 币种符号
    @NotBlank
    private String symbol;
    // 币种类别,参考CoinCategory
    @NotBlank
    private String category;
    // 默认显示名
    @NotBlank
    private String displayName;
    // 所有的显示名，用于多语言
    @NotBlank
    private String displayNameAll;
    // 大图片
    @NotBlank
    private String image;
    // 小图片
    @NotBlank
    private String icon;
    // 排序顺序
    @NotNull
    private Integer sort = 0;
    // 币的状态
    @NotNull
    private Integer status = 0;
    // 区块链服务器地址
    @NotBlank
    private String serverAddress;
    // 服务器断开
    @NotNull
    private Integer serverPort;
    // 区块链服务器用户
    private String serverUser;
    // 区块链服务器密码
    private String serverPassword;
    // 区块链服务器合约地址，目前只有类型是token才有用
    private String contractAddress;
    // 区块链服务器其它参数信息
    private String coinSelfParameter;
    @ApiModelProperty(value = "主账户地址")
    private String coinBase;
    // 提现手续费
    @ApiModelProperty(value = "提现手续费， 0.1代表10%")
    @NotNull
    private BigDecimal transferFeeRate = BigDecimal.ZERO;
    // 最小
    @ApiModelProperty(value = "提现最小金额")
    @NotNull
    private BigDecimal transferMinAmount;
    // 最大
    @ApiModelProperty(value = "提现最大金额")
    @NotNull
    private BigDecimal transferMaxAmount;

    @ApiModelProperty("转账收取固定值")
    private BigDecimal transferFeeStatic = BigDecimal.ZERO;;
    @ApiModelProperty("手续费方式0:百分比 1:固定")
    private Integer transferFeeSelect = 0;
    @ApiModelProperty("是否开启转账")
    private Integer transferEnable = 1;
    @ApiModelProperty("提币最小值")
    private BigDecimal withdrawMinAmount;
    @ApiModelProperty("提币最大值")
    private BigDecimal withdrawMaxAmount;
    private BigDecimal withdrawFeeRate = BigDecimal.ZERO;
    private Integer gatherEnable;
    private BigDecimal gatherMin;
}
