package com.cmd.wallet.api.controller;


import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.model.UserBank;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.service.UserBankService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Api(tags = "银行卡管理")
//@RestController
//@RequestMapping("/user-bank")
public class UserBankController {

    @Autowired
    UserBankService userBankService;

    @ApiOperation(value = "新增银行钱包转账地址")
    @PostMapping(value = "add-bank-address")
    public CommonResponse<UserBank> addTransferAddress(
            @ApiParam(required = true, name = "bankType", value = "银行类型，0：普通银行，1：支付宝，2：微信支付") @RequestParam(value = "bankType") Integer bankType,
            @ApiParam(required = true, name = "bankName", value = "银行名称/支付宝/微信等") @RequestParam(value = "bankName") String bankName,
            @ApiParam(required = false, name = "bankNameChild", value = "银行之行xxx") @RequestParam(value = "bankNameChild", required = false) String bankNameChild,
            @ApiParam(required = true, name = "bankUser", value = "银行/支付宝等账户名(真实姓名)") @RequestParam(value = "bankUser") String bankUser,
            @ApiParam(required = true, name = "bankNo", value = "银行卡号/支付宝微信账号") @RequestParam(value = "bankNo") String bankNo
    ) {
        Assert.check(bankName.trim().length()==0, ErrorCode.ERR_PARAM_ERROR);
        Assert.check(bankUser.trim().length()==0, ErrorCode.ERR_PARAM_ERROR);
        Assert.check(bankNo.trim().length()==0, ErrorCode.ERR_PARAM_ERROR);

        UserBank userBank = new UserBank().setUserId(ShiroUtils.getUser().getId()).setBankType(bankType)
                .setBankUser(bankUser).setBankName(bankName).setBankNo(bankNo).setBankNameChild(bankNameChild);
        userBankService.addUserBank(userBank);
        return new CommonResponse(userBank);
    }

    @ApiOperation(value = "查询银行钱包转账地址")
    @GetMapping(value = "bank-addresses")
    public CommonResponse<List<UserBank>> bankAddresses() {
        List<UserBank> list = userBankService.getUserBankList(ShiroUtils.getUser().getId());
        return new CommonResponse(list);
    }

    @ApiOperation(value = "修改银行钱包转账地址")
    @PostMapping(value = "update-bank-address")
    public CommonResponse updateTransferAddress(
            @ApiParam(required = true, name = "id", value = "id") @RequestParam(value = "id") Integer id,
            @ApiParam(required = true, name = "bankName", value = "银行名称/支付宝/微信等") @RequestParam(value = "bankName") String bankName,
            @ApiParam(required = false, name = "bankNameChild", value = "银行之行xxx") @RequestParam(value = "bankNameChild", required = false) String bankNameChild,
            @ApiParam(required = true, name = "bankNo", value = "银行卡号/支付宝微信账号") @RequestParam(value = "bankNo") String bankNo
    ) {
        UserBank userBank = new UserBank().setId(id).setUserId(ShiroUtils.getUser().getId()).setBankName(bankName).setBankNo(bankNo).setBankNameChild(bankNameChild);
        userBankService.updateUserBank(userBank);
        return new CommonResponse();
    }

    @ApiOperation(value = "删除银行钱包转账地址")
    @DeleteMapping(value = "delete-bank-address")
    public CommonResponse deleteTransferAddress(
            @ApiParam(required = true, name = "id", value = "id") @RequestParam(value = "id") Integer id) {
        userBankService.del(ShiroUtils.getUser().getId(), id);
        return new CommonResponse();
    }

}
