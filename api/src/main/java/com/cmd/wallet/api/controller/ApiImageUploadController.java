package com.cmd.wallet.api.controller;

import com.aliyun.oss.ServiceException;
import com.cmd.wallet.common.model.OssConfig;
import com.cmd.wallet.common.oss.OSSFactory;
import com.cmd.wallet.common.response.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "api图片上传")
@RequestMapping("/imageupload")
@RestController
@Slf4j
public class ApiImageUploadController {

    /**
     * 上传图片
     */
    @ApiOperation(value = "上传图片")
    @PostMapping(value = "upload")
    public CommonResponse<String> upload(@RequestParam("upload") MultipartFile multipartFile) {
        String pictureName ;
        if (multipartFile.isEmpty() || StringUtils.isBlank(multipartFile.getOriginalFilename())) {
            throw new RuntimeException("image params error");
        }

        String contentType = multipartFile.getContentType();
        if (!contentType.contains("")) {
            throw new RuntimeException("image error");
        }
        try {
            pictureName = OSSFactory.build(OssConfig.OSS_QCLOUD).upload(multipartFile);
            return new CommonResponse<>(pictureName);

        } catch (Exception e) {
          log.error("上传失败",e);
        }
        return new CommonResponse<>(null);
    }
}
