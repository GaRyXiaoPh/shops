package com.cmd.wallet.admin.controller;

import com.cmd.wallet.common.model.PlatConfig;
import com.cmd.wallet.common.response.CommonListResponse;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.service.PlatConfigService;
import com.github.pagehelper.Page;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plat-config")
public class PlatConfigController {
    @Autowired
    PlatConfigService platConfigService;

    @ApiOperation(value = "获取平台币配置")
    @GetMapping(value = "")
    public CommonListResponse<PlatConfig> getPlatConfig(
            @RequestParam(name = "pageNo") Integer pageNo,
            @RequestParam(name = "pageSize") Integer pageSize
    ) {
        Page<PlatConfig> platConfigs = platConfigService.getPlat(pageNo,pageSize);
        return CommonListResponse.fromPage(platConfigs);
    }
    @ApiOperation(value = "获取平台币配置详情")
    @GetMapping("detail")
    public CommonResponse getPlatConfigDetail(
            @ApiParam(name = "coinName",required = true) @RequestParam(name = "coinName",required = true) String coinName
    ){
        return  new CommonResponse(platConfigService.getPlatDetailByCoinName(coinName));
    }

    @ApiOperation(value = "修改配置详情")
    @PutMapping("")
    public CommonResponse getPlatConfigDetail(
            @RequestBody PlatConfig platConfig
    ){
        platConfigService.updatePlatConfig(platConfig);
        return  new CommonResponse();
    }
    @ApiOperation(value = "添加配置详情")
    @PostMapping("")
    public CommonResponse addPlatConfig(
            @RequestBody PlatConfig platConfig
    ){
        platConfigService.addPlatConfig(platConfig);
        return  new CommonResponse();
    }
}

