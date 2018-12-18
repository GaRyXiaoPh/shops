package com.cmd.wallet.common.vo;

import com.cmd.wallet.common.model.Coin;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel(description="不管数据是否变化，请都提交")
public class MallGoodVO implements Serializable {
    @ApiModelProperty("商品ID，增加商品时不需要，编辑时需要")
    private Integer id;
    @NotBlank
    @ApiModelProperty("商品名称")
    private String name;
    @NotBlank
    @ApiModelProperty("商品详情")
    private String detail;
    @ApiModelProperty("商品单价")
    private BigDecimal price;
    @ApiModelProperty("人民币价值")
    private BigDecimal cny;
    @NotBlank
    @ApiModelProperty(value = "计价币种，大写，不传默认ENG11")
    private String coinName = Coin.ENG11;
    @ApiModelProperty("库存")
    private Integer stock;
    @ApiModelProperty("销量,添加和编辑商品时不需要此字段")
    private Integer salesVolume;
    @ApiModelProperty("卖家微信号，非必填字段，如需置空，请传入空字符串''")
    private String sellerWechat;
    @ApiModelProperty("卖家手机号")
    private String sellerMobile;
    @ApiModelProperty("线下地址")
    private String shopAddress;
    @NotNull
    @ApiModelProperty("轮播图片列表")
    private List<String> images;
    @ApiModelProperty("商品状态：0:下架，1：上架")
    private Integer status;
    @ApiModelProperty("是否删除")
    private Integer isDelete;
    @ApiModelProperty("分类ID")
    private Integer categoryId;
    @ApiModelProperty("店主名称")
    private String userName;
    @ApiModelProperty("商家ID")
    private Integer userId;
    @ApiModelProperty("商店名称")
    private String shopName;
    @ApiModelProperty("商店头像")
    private String shopAvatar;

    //新增用于显示
    @ApiModelProperty("eng11商品单价")
    private BigDecimal eng11;

    @ApiModelProperty("bsts商品单价")
    private BigDecimal bsts;



}
