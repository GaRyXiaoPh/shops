package com.cmd.wallet.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class TSearchHistoryModel {
    @ApiModelProperty(value = "主键ID,添加不要传入，编辑必传")
    //主键
    private Integer id;
    //关键字
    @ApiModelProperty(value = "关键字")
    private String keyword;
    //搜索来源，如PC、小程序、APP等
    @ApiModelProperty(value = "搜索来源，如PC、小程序、APP等")
    private String from;
    //搜索时间
    @ApiModelProperty(value = "搜索来源，如PC、小程序、APP等")
    private Date addTime;
    //会员Id
    private Integer userId;
}
