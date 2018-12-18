package com.cmd.wallet.common.oss;

import com.cmd.wallet.common.mapper.OssConfigMapper;
import com.cmd.wallet.common.model.OssConfig;
import com.cmd.wallet.common.task.SpringContextUtils;

/**
 * 文件上传Factory
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-03-26 10:18
 */
public final class OSSFactory {
    private static OssConfigMapper ossConfigMapper;

    static {
        OSSFactory.ossConfigMapper = (OssConfigMapper) SpringContextUtils.getBean("ossConfigMapper");
    }

    public static CloudStorageService build(String name) {
        OssConfig qcloud = ossConfigMapper.getOssConfigByName(name);
        if (OssConfig.OSS_QINIU.equals(qcloud.getName())) {
            CloudStorageConfig config = new CloudStorageConfig();
            return new QiniuCloudStorageService(config);
        } else if (OssConfig.OSS_ALIYUN.equals(qcloud.getName())) {
            CloudStorageConfig config = new CloudStorageConfig();
            config.setQcloudDomain(qcloud.getEndpoint());
            config.setQcloudSecretId(qcloud.getAccessKey());
            config.setQcloudSecretKey(qcloud.getSecretKey());
            config.setQcloudBucketName(qcloud.getBucket());
            return new AliyunCloudStorageService(config);
        } else if (OssConfig.OSS_QCLOUD.equals(qcloud.getName())) {
            CloudStorageConfig config = new CloudStorageConfig();
            config.setQcloudDomain(qcloud.getEndpoint());
            config.setQcloudSecretId(qcloud.getAccessKey());
            config.setQcloudSecretKey(qcloud.getSecretKey());
            config.setQcloudBucketName(qcloud.getBucket());
            config.setQcloudRegion("ap-hongkong");
            return new QcloudCloudStorageService(config);
        }

        return null;
    }


}
