package com.cmd.wallet.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class GroupVO implements Serializable{
    private static final long serialVersionUID = -1L;


    @ApiModelProperty(value = "直接邀请人总数")
    private Integer leftChild = 0;      //左孩子
    @ApiModelProperty(value = "间接邀请人总数")
    private Integer rightChild = 0;     //右孩子
    @ApiModelProperty(value = "直接受益BSTS")
    private BigDecimal leftBsts = BigDecimal.ZERO;
    @ApiModelProperty(value = "直接受益ENG11")
    private BigDecimal leftEng11 = BigDecimal.ZERO;
    @ApiModelProperty(value = "间接受益BSTS")
    private BigDecimal rightBsts = BigDecimal.ZERO;
    @ApiModelProperty(value = "间接受益ENG11")
    private BigDecimal rightEng11 = BigDecimal.ZERO;

}
