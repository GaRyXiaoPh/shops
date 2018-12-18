package com.cmd.wallet.admin.controller;

import com.cmd.wallet.common.constants.UserBillReason;
import com.cmd.wallet.common.model.Task;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.service.UserCoinService;
import com.cmd.wallet.service.WalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Api(tags = "admin管理模块")
@RestController
@RequestMapping("/wallet")
public class WalletController {
    @Autowired
    WalletService walletService;
    @Autowired
    UserCoinService userCoinService;


    @ApiOperation("拨币")
    @PostMapping("/dispatch-coin")
    public CommonResponse dispatchCoin(@RequestParam("userId")Integer userId,
                                       @RequestParam("coinName")String coinName,
                                       @RequestParam("amount")BigDecimal amount){
        userCoinService.changeUserCoin(userId, coinName, amount, BigDecimal.ZERO, UserBillReason.DISPATCH_RELEASE, "系统拨币");
        return new CommonResponse();
    }

}
