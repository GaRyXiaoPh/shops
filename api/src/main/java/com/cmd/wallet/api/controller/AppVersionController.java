package com.cmd.wallet.api.controller;

import com.cmd.wallet.common.model.AppVersion;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "app升级接口")
@RestController
@RequestMapping("/app-version")
public class AppVersionController {

    @Autowired
    UserService userService;

    @ApiOperation(value = "获取升级版本号")
    @GetMapping(value = "")
    public CommonResponse<AppVersion> getArticleByLocale(
            @ApiParam(value = "platform:ios,android",required = true) @RequestParam String platform) {
        return new CommonResponse(userService.getAppVersion(platform));
    }
}
