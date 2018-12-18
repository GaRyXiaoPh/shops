package com.cmd.wallet.admin.controller;

import com.cmd.wallet.common.model.PlatBank;
import com.cmd.wallet.common.response.CommonListResponse;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.service.PlatBankService;
import com.github.pagehelper.Page;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plat-bank")
public class PlatBankController {
    @Autowired
    PlatBankService platBankService;

    @ApiOperation(value = "获取平台银行卡")
    @GetMapping(value = "")
    public CommonResponse<PlatBank> getPlatBankList(
            @RequestParam(name = "pageNo") Integer pageNo,
            @RequestParam(name = "pageSize") Integer pageSize
    ) {
        Page<PlatBank> platConfigs = platBankService.getList(pageNo,pageSize);
        return CommonListResponse.fromPage(platConfigs);
    }

    @ApiOperation(value = "获取平台币银行卡详情")
    @GetMapping("detail")
    public CommonResponse getPlatConfigDetail(
            @ApiParam(name = "id", required = true) @RequestParam(name = "id", required = true) Integer id
    ) {
        return new CommonResponse(platBankService.getPlatBankById(id));
    }

    @ApiOperation(value = "修改银行卡详情")
    @PutMapping("")
    public CommonResponse getPlatConfigDetail(
            @RequestBody PlatBank platBank
    ) {
        platBankService.updatePlatBank(platBank);
        return new CommonResponse();
    }

    @ApiOperation(value = "添加银行卡")
    @PostMapping("")
    public CommonResponse addPlatConfig(
            @RequestBody PlatBank platBank
    ) {
        platBankService.addPlatBank(platBank);
        return new CommonResponse();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("")
    public CommonResponse delPlatConfigById(
            @ApiParam(name = "id",required = true) @RequestParam(name = "id",required = true) Integer id
    ) {
        platBankService.delPlatConfigById(id);
        return new CommonResponse();
    }
}
