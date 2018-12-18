package com.cmd.wallet.admin.controller;

import com.cmd.wallet.common.model.ChangeConfig;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.service.ChangeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coin")
@Api(tags = "币种转换率接口")
public class CoinChangeRateController {
    @Autowired
    private ChangeConfigService changeConfigService;

    @ApiOperation("获取币种兑换率列表")
    @GetMapping("/rate")
    public CommonResponse coinRate(){
        List<ChangeConfig> changeConfigs =  changeConfigService.getChangeConfigList();
        return new CommonResponse(changeConfigs);
    }
    @ApiOperation("获取币种兑换率详情")
    @GetMapping("/rate-detail")
    public CommonResponse  rateDetail(
            @ApiParam(value = "id",name = "id") @RequestParam(required = true,name = "id") Integer id
    ){
        ChangeConfig changeConfig = changeConfigService.getChangeConfigDetail(id);
        return  new CommonResponse(changeConfig);
    }
    @ApiOperation("修改币种汇率")
    @PutMapping("rate")
    public CommonResponse updateRate(@RequestBody ChangeConfig changeConfig)
    {
        changeConfigService.updateChangeConfig(changeConfig);
        return new CommonResponse();
    }
    @ApiOperation("添加币种兑换率")
    @PostMapping("/rate")
    public CommonResponse addRate(@RequestBody ChangeConfig changeConfig)
    {
        changeConfigService.add(changeConfig);
        return new CommonResponse();
    }
}
