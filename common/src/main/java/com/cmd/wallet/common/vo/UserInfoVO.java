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
public class UserInfoVO implements Serializable{
    private static final long serialVersionUID = -1L;

    private Integer id;
    private String nickName;
    @ApiModelProperty(value = "用户登录名称，全表唯一")
    private String userName;
    @ApiModelProperty(value = "国家代码（86）")
    private String areaCode;
    @ApiModelProperty(value = "用户绑定的手机号码")
    private String mobile;
    private String email;
    @ApiModelProperty(value = "邀请码")
    private String inviteCode;
    @ApiModelProperty(value = "邀请人ID")
    private Integer invite;
    @ApiModelProperty(value = "上级ID")
    private Integer referrer;
    @ApiModelProperty(value = "用户真实姓名")
    private String realName;
    private String image;

    private String leftInvite;  //左推荐码
    private String rightInvite; //右推荐码
    @ApiModelProperty(value = "直接邀请人总数")
    private Integer leftChild;      //左孩子
    @ApiModelProperty(value = "间接邀请人总数")
    private Integer rightChild;     //右孩子

    private Integer loginTimes;  //用户登录次数
    private String registerIp;  //注册时的ip
    private String lastLoginIp;  //最近一次登录的用户ip
    private Date registerTime;  //注册时间
    private Date lastLoginTime;  //最后一次登录时间
    private Integer status;      //用户状态，0：正常，1：被禁用
    private Date updateTime;    //最后一次用户修改资料的时间，登录不修改这个时间

    @ApiModelProperty(value = "推荐人电话")
    private String referrerMobile;
    private Integer referrerCount;
    private String level;

    @ApiModelProperty("是否开启销售权限：0未开启，1开启")
    private Integer salesPermit;
    @ApiModelProperty("自品牌专区")
    private Integer brandPermit;    //自品牌专区许可
    @ApiModelProperty("全平台")
    private Integer globalPermit;    //全平台访问许可
}
