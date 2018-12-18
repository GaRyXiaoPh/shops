package com.cmd.wallet.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

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
public class MallApply {

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
     * 联系人
     */
    @ApiModelProperty("联系人")
    private String contacts;
    /**
     * 联系人电话
     */
    @ApiModelProperty("联系人电话")
    private String phone;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date addTime;
    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private Date updateTime;
    /**
     * 状态
     */
    @ApiModelProperty("0：申请中，1:已驳回，2：已通过")
    private Integer status;


    //用于前端显示
    /**
     * 营业支招
     */
    @ApiModelProperty("营业执照")
    private String busLicense;
    /**
     * 门面照片
     */
    @ApiModelProperty("门面照片")
    private List<String> shopPhotos;

}
