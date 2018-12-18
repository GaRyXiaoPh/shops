package com.cmd.wallet.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class PlatBank implements Serializable {
    private static final long serialVersionUID = -1L;

    private Integer id;
    @ApiModelProperty("银行卡类型")
    private Integer bankType;
    @ApiModelProperty("银行卡名称")
    private String bankName;
    @ApiModelProperty("银行之行")
    private String bankNameChild;
    @ApiModelProperty("银行卡账户")
    private String bankUser;
    @ApiModelProperty("银行卡号")
    private String bankNo;
    @ApiModelProperty("状态")
    private Integer status;

    private Date createTime;
    private Date lastTime;
}
