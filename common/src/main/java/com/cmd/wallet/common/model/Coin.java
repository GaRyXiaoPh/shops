package com.cmd.wallet.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(value={"serverAddress","serverPort","serverUser","serverPassword", "coinSelfParameter"})
public class Coin implements Serializable {
    private static final long serialVersionUID = -1L;

    public static final String CNY="CNY";
    public static final String USD="USD";
    public static final String USDT="USDT";
    public static final String ENG11="ENG11";
    public static final String EOS="EOS";
    public static final String BSTS="BSTS";

    public static final int COIN_NORMAL=0;
    public static final int COIN_DISABLE=1;

    private Integer     id;
    private String      name;               // 币种名称
    private String      symbol;             // 币种符号
    private String      category;           // 币种类别,参考CoinCategory
    private String      displayName;        // 默认显示名
    private String      displayNameAll;     // 所有的显示名，用于多语言
    private String      image;                  // 大图片
    private String      icon;                   // 小图片
    private Integer     sort;                   // 排序顺序
    private Integer     status;                 // 币的状态
    private String      serverAddress;      // 区块链服务器地址
    private Integer     serverPort;         // 服务器断开
    private String      serverUser;         // 区块链服务器用户
    private String      serverPassword;     // 区块链服务器密码
    private float      sendFee;             // 对外发送币所需要收的手续费
    private float      receivedFee;         // 从外接收币所需要收的手续费
    private String      contractAddress;    // 区块链服务器合约地址，目前只有类型是token才有用
    private String      coinSelfParameter;  // 区块链服务器其它参数信息
    private Date        createTime;     //创建时间
    private Date        lastTime;       // 最后一次更新时间
    private String      coinBase;       //主账户地址(提现时使用)

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 小数点位数，这个不在数据库里面
    private int        decimals = 18;
}
