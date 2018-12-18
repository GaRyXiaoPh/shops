package com.cmd.wallet.api.controller;

import com.cmd.wallet.common.model.TMallAddressModel;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.service.MallAddressService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址
 */
@Api(tags = "地址模块")
@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private MallAddressService addressService;

    @ApiOperation("获取用户的收货地址")
    @GetMapping("/addresses")
    public CommonResponse<List<TMallAddressModel>> getAddress(){
        Integer userId = ShiroUtils.getUser().getId();
        List<TMallAddressModel> addressModelList = addressService.getAddresses(userId);
        return new CommonResponse<>(addressModelList);
    }
    @ApiOperation("添加用户的收货地址")
    @PostMapping("/add-address")
    public CommonResponse addAddress(@RequestBody TMallAddressModel tMallAddressModel){
        Integer id = ShiroUtils.getUser().getId();
        tMallAddressModel.setUserId(id);
        addressService.addAddress(tMallAddressModel);
        return new CommonResponse<>();
    }
    @ApiOperation("编辑用户的收货地址")
    @PostMapping("/edit-address")
    public CommonResponse putAddress(@RequestBody TMallAddressModel tMallAddressModel){
        Integer id = ShiroUtils.getUser().getId();
        tMallAddressModel.setUserId(id);
        addressService.editAddress(tMallAddressModel);
        return new CommonResponse<>();
    }
    @ApiOperation("设置用户的默认收货地址")
    @PostMapping("/default-address")
    public CommonResponse defaultAddress(@ApiParam("地址ID") @RequestParam("id") Integer id){
        addressService.defaultAddress(id);
        return new CommonResponse<>();
    }
    @ApiOperation("获取用户的默认收货地址")
    @GetMapping("/default-address")
    public CommonResponse<TMallAddressModel> getDefaultAddress(){
        return new CommonResponse<>(addressService.getDefaultAddresses());
    }
    @ApiOperation("删除用户的收货地址")
    @ApiImplicitParams({@ApiImplicitParam(name="id",value="地址ID")})
    @PostMapping("/del-address")
    public CommonResponse delAddress(@RequestParam Integer id){
        Integer userId = ShiroUtils.getUser().getId();
        addressService.delAddress(userId,id);
        return new CommonResponse<>();
    }
}
