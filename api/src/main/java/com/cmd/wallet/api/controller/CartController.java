package com.cmd.wallet.api.controller;

import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.model.CartModel;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.service.MallCartService;
import com.cmd.wallet.service.MallGoodService;
import com.cmd.wallet.service.MallOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车相关接口
 */
@Api(tags = "购物车模块")
@RestController
@RequestMapping("cart")
public class CartController {
    @Autowired
    private MallCartService mallCartService;

    @ApiOperation("获取购物车列表")
    @GetMapping("/list")
    public CommonResponse<List<CartModel>> carts(){
        return new CommonResponse(mallCartService.getMallCartByUserId(ShiroUtils.getUser().getId(),null));
    }

    @ApiOperation("添加购物车")
    @PostMapping("/add")
    public CommonResponse cart(@ApiParam("商品ID")@RequestParam Integer goodId,@ApiParam("增加的数量")@RequestParam Integer number){
        mallCartService.addCart(goodId,number);
        return new CommonResponse();
    }

    @ApiOperation("减少购物车,返回当前商品的数量")
    @PostMapping("/minus")
    public CommonResponse<Integer> minus(@ApiParam("商品ID") @RequestParam Integer goodId, @ApiParam("减少的数量")@RequestParam Integer number){
        return new CommonResponse(mallCartService.cutCart(goodId,number));
    }

    @ApiOperation(value = "获取购物车商品的总件件数")
    @GetMapping("goodscount")
    public CommonResponse<Integer> goodscount() {
        //查询列表数据
        List<CartModel> mallCartByUserId = mallCartService.getMallCartByUserId(ShiroUtils.getUser().getId(),null);
        return new CommonResponse(mallCartByUserId.size());
    }

    @ApiOperation("删除购物车商品")
    @GetMapping("/del-cart-good")
    public CommonResponse delCartGood(@ApiParam("主键id") @RequestParam("goodId") Integer goodId){
        Integer userId = ShiroUtils.getUser().getId();
        int i = mallCartService.delCartGood(goodId,userId);
        if(i >0){
            return new CommonResponse(ErrorCode.ERR_SUCCESS);
        }
        return new CommonResponse();
    }


    @ApiOperation("获取购物车详情列表")
    @GetMapping("/get-cart-details")
    public CommonResponse<List<CartModel>> getCartsByIds(@ApiParam("购物车id合集") @RequestParam("cartIds") Integer[] cartIds){
        return new CommonResponse(mallCartService.getMallCartByUserId(ShiroUtils.getUser().getId(),cartIds));
    }

}
