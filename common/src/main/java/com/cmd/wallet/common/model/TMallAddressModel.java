package com.cmd.wallet.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class TMallAddressModel {
    @ApiModelProperty(value = "主键ID,添加不要传入，编辑必传")
    private Integer id;
    @JsonIgnore
    private Integer userId;
    @ApiModelProperty(value = "收货人姓名",required=true)
    @NotBlank
    private String receiverName;
    @ApiModelProperty(value = "收货人手机号",required=true)
    @NotBlank
    private String receiverMobile;
    @ApiModelProperty(value = "收货地址省编号",required=true)
    @NotBlank
    private String provinceId;
    @ApiModelProperty(value = "收货地址市编号",required=true)
    @NotBlank
    private String cityId;
    @ApiModelProperty(value = "收货地址区县编号",required=true)
    @NotBlank
    private String areaId;
    @ApiModelProperty(value = "详细地址",required=true)
    @NotBlank
    private String detailAddr;
    @ApiModelProperty("是否默认地址：0否，1是，不传默认为0")
    private Integer isDefault = 0;
    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private Date updateTime;

}
