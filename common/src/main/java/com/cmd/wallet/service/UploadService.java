package com.cmd.wallet.service;


import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectResult;
import com.cmd.wallet.common.mapper.OssConfigMapper;
import com.cmd.wallet.common.model.OssConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

@Service
public class UploadService {
    @Autowired
    private OssConfigMapper ossConfigMapper;

    private static OssConfig ossConfig = null;

    public String upload(String fileName,FileInputStream fileInputStream){
        if (ossConfig==null){
            ossConfig = ossConfigMapper.getOssConfigByName(OssConfig.OSS_ALIYUN);
        }

        fileName = ""+(new Date().getTime())+"_"+fileName;

        String bucket = ossConfig.getBucket();
        String endpoint = ossConfig.getEndpoint();
        String accessKeyId = ossConfig.getAccessKey();
        String accessKeySecret = ossConfig.getSecretKey();

        try{
            OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            ossClient.putObject(bucket, fileName, fileInputStream);
            ossClient.shutdown();
        }catch (Exception e){
            return null;
        }
        return "http://"+ossConfig.getBucket()+"."+ossConfig.getEndpoint()+"/"+fileName;
    }

    public static String upload(){
        String filePath = "C://about.png";
        String url = "http://kuanggongzhijia.oss-ap-southeast-1.aliyuncs.com/";
        String fileName = ""+(new Date().getTime());

        String endpoint = "oss-ap-southeast-1.aliyuncs.com";
        String accessKeyId = "LTAIF03t3tMEV8Qy";
        String accessKeySecret = "EEschohE8O5NNFarKNXVyDtluYk1g7";

        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject("kuanggongzhijia", fileName, new File(filePath));
        ossClient.shutdown();
        return url+fileName;
    }

    public static void main(String[]argv){
        String tmp = upload();
        int t = 90;
    }
}
