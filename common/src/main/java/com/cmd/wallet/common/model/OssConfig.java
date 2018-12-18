package com.cmd.wallet.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class OssConfig implements Serializable {

    public static final String OSS_QINIU="QINIU";
    public static final String OSS_ALIYUN="ALIYUN";
    public static final String OSS_QCLOUD="QCLOUD";


    private Integer id;
    private String name;
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String token;
    private String bucket;
    private Integer status;
    private Date createTime;
    private Date lastTime;
}
