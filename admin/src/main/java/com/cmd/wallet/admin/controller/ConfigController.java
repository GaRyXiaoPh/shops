package com.cmd.wallet.admin.controller;

import com.cmd.wallet.common.mapper.AppVersionMapper;
import com.cmd.wallet.common.model.AppVersion;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.common.vo.ConfigVo;
import com.cmd.wallet.service.ConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "全局配置")
public class ConfigController {
    @Autowired
    ConfigService configService;
    @Autowired
    private AppVersionMapper appVersionMapper;

    @ApiOperation("获取奖励配置接口")
    @GetMapping("/system/config")
    public CommonResponse systemConfig() {
        ConfigVo configVo = configService.getSystemConfig();
        return new CommonResponse(configVo);
    }

    @ApiOperation("设置奖励配置")
    @PostMapping("/system/config")
    public CommonResponse setSystemConfig(@RequestBody ConfigVo configVo)
    {
        configService.setSystemConfig(configVo);
        return  new CommonResponse();
    }
    @ApiOperation(value = "版本号的获取")
    @GetMapping("config/app-version")
    public CommonResponse<List<AppVersion>> getAppVersion() {
        return new CommonResponse<>(appVersionMapper.getAppVersionList());
    }

    @ApiOperation(value = "版本修改")
    @PutMapping("config/app-version")
    public CommonResponse updateAppVersion(@RequestBody AppVersion appVersion) {
        appVersionMapper.updateAppVersion(appVersion);
        return new CommonResponse();
    }

    @ApiOperation(value = "获取版本号详情")
    @GetMapping("config/app-version-detail")
    public CommonResponse getAppVersionById(@ApiParam(value = "id", name = "id") @RequestParam(name = "id", required = true) int id) {
        return new CommonResponse(appVersionMapper.getAppVersionById(id));
    }
}
