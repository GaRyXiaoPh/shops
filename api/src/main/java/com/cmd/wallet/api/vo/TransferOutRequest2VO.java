package com.cmd.wallet.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class TransferOutRequest2VO {
    @NotBlank
    @ApiModelProperty("币种")
    String coinName;
    @NotBlank
    @ApiModelProperty("手机号")
    String mobile;
    @NotNull
    @ApiModelProperty("数量")
    BigDecimal amount;

    @ApiModelProperty("备注")
    String comment;
    //@NotBlank
    //@ApiModelProperty("验证码")
    //String validCode;
    @NotBlank
    @ApiModelProperty("交易密码")
    String paypassword;
}