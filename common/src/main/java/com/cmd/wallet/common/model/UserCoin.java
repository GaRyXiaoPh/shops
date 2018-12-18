package com.cmd.wallet.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserCoin implements Serializable{
    private static final long serialVersionUID = -1661830493897252605L;

    public static final Integer EOS_TAG = 100000;  //地址标签=EOS_TAG+用户ID
    public static String getAddressTag(Integer userId){
        return ""+(EOS_TAG.intValue()+userId);
    }
    public static Integer getUserIdByAddressTag(String addressTag){
        Integer tag = Integer.parseInt(addressTag);
        return tag.intValue()-EOS_TAG;
    }

    private Integer id;
    private Integer userId;
    @ApiModelProperty(value = "币种名称")
    private String coinName;
    @ApiModelProperty(value = "绑定地址")
    private String bindAddress;
    @ApiModelProperty(value = "可用余额")
    private BigDecimal availableBalance;
    @ApiModelProperty(value = "可用余额")
    private BigDecimal freezeBalance;
    @ApiModelProperty(value = "促销奖励冻结总金额")
    private  BigDecimal awardBalance;


    //用户币种状态，0：正常，1：冻结使用
    private Integer status;
}