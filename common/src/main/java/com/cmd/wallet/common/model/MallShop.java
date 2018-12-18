package com.cmd.wallet.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author stylefeng
 * @since 2018-10-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel
public class MallShop  {

    private static final long serialVersionUID = 1L;

    /**
     * 店铺ID
     */
    @ApiModelProperty("店铺ID")
    private Integer id;
    /**
     * 店主的ID
     */
    @ApiModelProperty("用户ID")
    private Integer userId;
    /**
     * 店铺头像
     */
    @ApiModelProperty("店铺头像")
    private String shopAvatar;
    /**
     * 店铺名称
     */
    @ApiModelProperty("店铺名称")
    private String shopName;
    /**
     * 店铺简介
     */
    @ApiModelProperty("店铺简介")
    private String shopIntro;
    /**
     * 店铺好评个数
     */
    @ApiModelProperty("店铺好评个数")
    private Integer shopGoodRept;
    /**
     * 店铺中评个数
     */
    @ApiModelProperty("店铺中评个数")
    private Integer shopMiddleRept;
    /**
     * 店铺差评个数
     */
    @ApiModelProperty("店铺差评个数")
    private Integer shopBadRept;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;
    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private Date updateTime;
//    /**
//     * 状态
//     */
//    @ApiModelProperty("0：申请中，1:已驳回，2：已通过")
//    private Integer status;

}
