package com.cmd.wallet.api.controller;

import com.cmd.wallet.common.mapper.PlatConfigMapper;
import com.cmd.wallet.common.model.PlatConfig;
import com.cmd.wallet.common.model.PlatOrder;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.response.CommonListResponse;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.service.PlatOrderService;
import com.github.pagehelper.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

//@Api(tags = "用户对平台交易ENG11")
//@RestController
//@RequestMapping("/plat-trade")
public class PlatTradeController {

    @Autowired
    PlatOrderService platOrderService;
    @Autowired
    PlatConfigMapper platConfigMapper;

    @ApiOperation(value = "获取OTC配置")
    @GetMapping("/plat-config")
    public CommonResponse<PlatConfig> getPlatConfig(@RequestParam("coinName")String coinName){
        return new CommonResponse(platConfigMapper.getPlatConfig(coinName));
    }

    @ApiOperation("申诉订单")
    @PutMapping("/appeal")
    public CommonResponse appeal(
            @ApiParam(value = "订单id",required = true)@RequestParam("orderId")Integer orderId,
            @ApiParam(value = "申诉说明",required = true)@RequestParam("reason")String reason
    ){
        platOrderService.complaint(ShiroUtils.getUser().getId(), orderId, reason);
        return new CommonResponse();
    }

    @ApiOperation(value = "挂买单（充值）")
    @PostMapping("/deposit")
    public CommonResponse<Integer> deposit(@RequestParam("coinName")String coinName,
                                   @RequestParam("amount")BigDecimal amount,
                                   @RequestParam("price")BigDecimal price){
        Integer id = platOrderService.deposit(ShiroUtils.getUser().getId(), coinName, amount, price);
        return new CommonResponse(id);
    }

    @ApiOperation(value = "挂卖单（提现）")
    @PostMapping("/withdraw")
    public CommonResponse<Integer> withdraw(@RequestParam("coinName")String coinName,
                                   @RequestParam("amount")BigDecimal amount,
                                   @RequestParam("price")BigDecimal price,
                                   @RequestParam("bankId")Integer bankId){
        Integer id = platOrderService.withdraw(ShiroUtils.getUser().getId(), coinName, amount, price, bankId);
        return new CommonResponse(id);
    }

    @ApiOperation(value = "用户确定付款")
    @PostMapping("/confirm")
    public CommonResponse confirm(@RequestParam("orderId")Integer orderId){
        platOrderService.confirm(ShiroUtils.getUser().getId(), orderId);
        return new CommonResponse();
    }

    @ApiOperation(value = "取消订单")
    @PostMapping("/cancel")
    public CommonResponse cancel(@RequestParam("orderId")Integer orderId,
                                 @RequestParam(required = false)String comment){
        platOrderService.cancel(ShiroUtils.getUser().getId(), orderId, comment);
        return new CommonResponse();
    }

    @ApiOperation(value = "获取我的订单")
    @GetMapping("/my-order-list")
    public CommonListResponse<PlatOrder> getMyPlatOrderList(@ApiParam(value = "coinName", required = false)@RequestParam(value="coinName",required = false)String coinName,
                                                            @ApiParam(value = "status", required = false)@RequestParam(value="status",required = false) Integer[]status,
                                                            @ApiParam(value = "pageNo", required = true)@RequestParam(value = "pageNo", required = true)Integer pageNo,
                                                            @ApiParam(value = "pageSize", required = true)@RequestParam(value = "pageSize", required = true)Integer pageSize){
        Page<PlatOrder> pg = platOrderService.getMyPlatOrderList(ShiroUtils.getUser().getId(), coinName, status, pageNo, pageSize);
        return new CommonListResponse<>().fromPage(pg);
    }

    @ApiOperation(value = "订单详情")
    @GetMapping("/order-detail")
    public CommonResponse<PlatOrder> getPlatOrderDetail(@RequestParam("orderId")Integer orderId){
        return new CommonResponse<>(platOrderService.getPlatOrderDetail(orderId));
    }

}
