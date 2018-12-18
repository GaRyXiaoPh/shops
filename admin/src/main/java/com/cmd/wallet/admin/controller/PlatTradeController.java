package com.cmd.wallet.admin.controller;

import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.common.response.CommonListResponse;
import com.cmd.wallet.common.vo.PlatOrderVO;
import com.cmd.wallet.service.PlatOrderService;
import com.github.pagehelper.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "平台交易EC")
@RestController
@RequestMapping("/plat-trade")
public class PlatTradeController {

    @Autowired
    PlatOrderService platOrderService;

    @ApiOperation("订单列表")
    @GetMapping("/orders")
    public CommonListResponse<PlatOrderVO> getPlatOrderVOList(@ApiParam(name = "coinName", required = false) @RequestParam(name = "coinName",required = false) String coinName,
                                                              @ApiParam(value = "status状态 1交易成功，4:已经接单,6:申诉中,100:已经取消", required = false)
                                                                  @RequestParam(name = "status",required = false) Integer status,
                                                              @ApiParam(name = "pageNo", required = true) @RequestParam(name = "pageNo") Integer pageNo,
                                                              @ApiParam(name = "pageSize", required = true) @RequestParam(name = "pageSize") Integer pageSize){
        Page<PlatOrderVO> pg = platOrderService.getPlatOrderVOList(null, coinName, status, pageNo, pageSize);
        return  new CommonListResponse<>().fromPage(pg);
    }

    @ApiOperation("取消订单")
    @PostMapping("/cancel")
    public CommonResponse cancelPlatOrder(@RequestParam("id") Integer id){
        platOrderService.adminCancel(id, "管理员系统取消");
        return  new CommonResponse();
    }

    @ApiOperation("确认订单")
    @PostMapping("/confirm")
    public CommonResponse confirmPlatOrder(@RequestParam("id")Integer id){
        platOrderService.adminConfirm(id);
        return new CommonResponse();
    }

}
