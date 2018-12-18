package com.cmd.wallet.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class UpdateUserVo implements Serializable {
    private static final long serialVersionUID = -1661830493897252605L;

    private Integer id;

    private String nickName;
    private String userName;
    private String areaCode;
    private String mobile;
    private String email;
    private String password;
    private String payPassword;
    private String inviteCode;      //邀请码
    private Integer invite;         //推荐人ID
    private Integer referrer;       //上级ID
    private String realName;
    private String image;
    private Integer loginTimes;     //用户登录次数
    private String registerIp;      //注册时的ip
    private String lastLoginIp;     //最近一次登录的用户ip
    private Date registerTime;      //注册时间
    private Date lastLoginTime;     //最后一次登录时间
    private Integer status;         //用户状态，0：正常，1：被禁用
    private Date updateTime;        //最后一次用户修改资料的时间，登录不修改这个时间
    private Integer leftChild;      //左孩子
    private Integer rightChild;     //右孩子
    private String leftInvite;      //左推荐码
    private String rightInvite;     //右推荐码
    private Integer salesPermit;
    private Integer brandPermit;    //自品牌专区许可
    private Integer globalPermit;    //全平台访问许可

}

