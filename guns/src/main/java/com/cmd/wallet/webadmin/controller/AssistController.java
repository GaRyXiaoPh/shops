package com.cmd.wallet.webadmin.controller;

import cn.stylefeng.roses.core.base.controller.BaseController;
import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.service.UploadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;

@Controller
@RequestMapping("/assist")
public class AssistController extends BaseController {

    @Autowired
    UploadService uploadService;

    @PostMapping(value = "/test")
    public Object test() {
        return SUCCESS_TIP;
    }

    @PostMapping("/img/upload")
    @ResponseBody
    public String uploadImg(@RequestParam("upload") MultipartFile multipartFile)  {
        if (multipartFile.isEmpty() || StringUtils.isBlank(multipartFile.getOriginalFilename())) {
            throw new RuntimeException("image params error");
        }
        String contentType = multipartFile.getContentType();
        if (!contentType.contains("")) {
            throw new RuntimeException("image error");
        }

        try {
            String fileName = multipartFile.getOriginalFilename();
            FileInputStream fileInputStream = (FileInputStream) multipartFile.getInputStream();
            String url = uploadService.upload(fileName, fileInputStream);
            if (url!=null)
                return url;
        } catch (Exception e) {
            ;
        }
        Assert.check(true, ErrorCode.ERR_PARAM_ERROR);
        return null;
    }
}
