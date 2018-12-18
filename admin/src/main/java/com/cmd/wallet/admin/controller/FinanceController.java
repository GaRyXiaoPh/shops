package com.cmd.wallet.admin.controller;

import com.cmd.wallet.common.enums.SendCoinStatus;
import com.cmd.wallet.common.model.SendCoin;
import com.cmd.wallet.common.model.User;
import com.cmd.wallet.common.response.CommonListResponse;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.service.SendCoinService;
import com.cmd.wallet.service.UserService;
import com.github.pagehelper.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "财务中心（数字货币转入转出）")
@RestController
@RequestMapping("finance")
public class FinanceController {
    @Autowired
    UserService userService;
    @Autowired
    SendCoinService sendCoinService;
    @ApiOperation("获取转账列表")
    @GetMapping(value = "transfer-list")
    public CommonListResponse<SendCoin> getTransferList(@ApiParam("币种") @RequestParam(name="coinName",required = false) String coinName,
                                                        @ApiParam(value = "用户名") @RequestParam(name = "userName",required = false) String userName,
                                                        @ApiParam("状态:APPLYING:申请，PASSED 通过，FAILED:未通过， ALL：所有") @RequestParam SendCoinStatus status,
                                                        @ApiParam("pageNo") @RequestParam Integer pageNo,
                                                        @ApiParam("pageSize") @RequestParam Integer pageSize) {
        Integer userId = null;
        if(userName != null){
            User user=userService.getUserByUserName(userName);
            if(user != null){
                userId = user.getId();
            }
        }
        Page<SendCoin> rst = sendCoinService.getTransferList(userId, coinName, status, pageNo, pageSize);
        for(SendCoin sc : rst) {
            if(sc.getUserId() != null) {
                User user = userService.getUserByUserId(sc.getUserId());
                if(user != null) {
                    sc.setUserName(user.getUserName());
                }
            }
        }
        return new CommonListResponse<>().fromPage(rst);
    }
    @ApiOperation("审核失败")
    @PostMapping(value = "transfer-check-fail")
    public CommonResponse transferCheckFail(@ApiParam("转账ID") @RequestParam Integer id) {
        sendCoinService.transferCheckFail(id, 0);
        return new CommonResponse();
    }

    @ApiOperation("审核通过")
    @PostMapping(value = "transfer-check-pass")
    public CommonResponse transferCheckPass(@ApiParam("转账ID") @RequestParam Integer id) {
        sendCoinService.transferCheckPass(id, 0);
        return new CommonResponse();
    }
}
