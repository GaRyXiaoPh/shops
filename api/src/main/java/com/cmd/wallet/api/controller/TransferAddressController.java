package com.cmd.wallet.api.controller;


import com.cmd.wallet.common.model.TransferAddress;
import com.cmd.wallet.common.model.UserBank;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.service.TransferAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "联系人地址管理")
@RestController
@RequestMapping("/transfer-address")
public class TransferAddressController {

    @Autowired
    TransferAddressService transferAddressService;

    @ApiOperation(value = "新增联系人转账地址")
    @PostMapping(value = "add-transfer-address")
    public CommonResponse addTransferAddress( @RequestParam(value = "coinName") String coinName,
                                              @RequestParam(value = "address")String address,
                                              @RequestParam(value = "name")String name,
                                              @RequestParam(value = "addressTag", required = false)String addressTag,
                                              @RequestParam(value = "comment")String comment){
        transferAddressService.addTransferAddress(ShiroUtils.getUser().getId(), new TransferAddress().setAddress(address).setAddressTag(addressTag)
                .setCoinName(coinName).setName(name).setComment(comment));
        return new CommonResponse();
    }

    @ApiOperation(value = "删除联系人转账地址")
    @PostMapping(value = "del-transfer-address")
    public CommonResponse addTransferAddress( @RequestParam(value = "id") Integer id){
        transferAddressService.delTransferAddress(ShiroUtils.getUser().getId(), id);
        return new CommonResponse();
    }

    @ApiOperation(value = "获取联系人转账地址")
    @GetMapping(value = "get-transfer-address")
    public CommonResponse<List<TransferAddress>> getTransferAddress(@RequestParam(value = "coinName", required = false) String coinName){
        if (coinName!=null && coinName.trim().length()==0){
            coinName=null;
        }
        List<TransferAddress> list = transferAddressService.getTransferAddressList(ShiroUtils.getUser().getId(), coinName);
        return new CommonResponse(list);
    }
}
