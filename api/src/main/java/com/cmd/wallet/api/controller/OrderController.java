package com.cmd.wallet.api.controller;

import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.model.CartModel;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.response.CommonListResponse;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.common.vo.MallOrderListVO;
import com.cmd.wallet.common.vo.MallOrderVO;
import com.cmd.wallet.common.vo.PayOrderVO;
import com.cmd.wallet.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 订单相关接口
 */
@Api(tags = "订单模块")
@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    private MallOrderService mallOrderService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private final static String SUBMIT = "submit_";





    @ApiOperation("支付订单")
    @PostMapping("/pay")
    public CommonResponse payOrder(@RequestBody PayOrderVO payOrderVO){
        preventResubmission("mall/order/pay",ShiroUtils.getUser().getId());
        mallOrderService.payOrder(payOrderVO);
        return new CommonResponse();
    }
    @ApiOperation("确认收货")
    @ApiImplicitParams({@ApiImplicitParam(name="orderId",value="订单ID"),@ApiImplicitParam(name="reputation",value="评价类型（默认好评）：0好评，1中评，2差评")})
    @PostMapping("/confirm")
    public CommonResponse confirmOrder(@RequestParam Integer orderId, @RequestParam(defaultValue = "0",required = false) Integer reputation){
        preventResubmission("mall/order/confirm",ShiroUtils.getUser().getId());
        mallOrderService.confirmOrder(orderId,reputation);
        return new CommonResponse();
    }
    @ApiOperation("确认发货")
    @ApiImplicitParams({@ApiImplicitParam(name="orderId",value="订单ID"),@ApiImplicitParam(name="imgUrl",value="发货凭证")})
    @PostMapping("/send")
    public CommonResponse sendOrder(@RequestParam Integer orderId, @RequestParam String imgUrl){
        preventResubmission("mall/order/send",ShiroUtils.getUser().getId());
        Integer salesPermit = ShiroUtils.getUser().getSalesPermit();
        Assert.check(salesPermit == 0,ErrorCode.ERR_MALL_GOOD_UNPERMIT);
        mallOrderService.sendOrder(orderId,imgUrl);
        return new CommonResponse();
    }
    @ApiOperation("发起退货")
    @ApiImplicitParams({@ApiImplicitParam(name="orderId",value="订单ID"),@ApiImplicitParam(name="imgUrl",value="发货凭证"),@ApiImplicitParam(name="returnReason",value="退货原因")})
    @PostMapping("/return")
    public CommonResponse returnOrder(@RequestParam Integer orderId, @RequestParam(required = false) String imgUrl, @RequestParam(required = false,defaultValue = "") String returnReason){
        preventResubmission("mall/order/return",ShiroUtils.getUser().getId());
        mallOrderService.returnOrder(orderId,imgUrl,returnReason);
        return new CommonResponse();
    }
    @ApiOperation("确认退货")
    @PostMapping("/confirm-return")
    public CommonResponse confirmReturnOrder(@RequestParam Integer orderId){
        preventResubmission("mall/order/confirm-return",ShiroUtils.getUser().getId());
        Integer salesPermit = ShiroUtils.getUser().getSalesPermit();
        Assert.check(salesPermit == 0,ErrorCode.ERR_MALL_GOOD_UNPERMIT);
        mallOrderService.confirmReturnOrder(orderId);
        return new CommonResponse();
    }
    @ApiImplicitParams({@ApiImplicitParam(name="pageNo",value="页数",defaultValue = "1"),
            @ApiImplicitParam(name="pageNo",value="一页的条数",defaultValue = "10"),
            @ApiImplicitParam(name="status",value="状态：0:退货；1:待发货，2:已发货,3:已完成,4:已取消,不传全部")})
    @ApiOperation("获取我的订单列表")
    @GetMapping("/my")
    public CommonListResponse<MallOrderListVO> getOrdersMy(@RequestParam(value = "pageNo",defaultValue = "1")Integer pageNo,
                                                           @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                                           @RequestParam(value = "status",required = false)Integer status ){
        Integer userId = ShiroUtils.getUser().getId();
        return new CommonListResponse<>().fromPage(mallOrderService.getMyOrdersByStatus(pageNo,pageSize,userId,status));
    }
    @ApiImplicitParams({@ApiImplicitParam(name="pageNo",value="页数",defaultValue = "1"),
            @ApiImplicitParam(name="pageNo",value="一页的条数",defaultValue = "10"),
            @ApiImplicitParam(name="status",value="状态：0:退货；1:待发货，2:已发货,3:已完成,4:已取消不传全部")})
    @ApiOperation("获取商家订单列表")
    @GetMapping("/seller")
    public CommonListResponse<MallOrderListVO> getOrdersSeller(@RequestParam(value = "pageNo",defaultValue = "1")Integer pageNo,
                                                               @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                                               @RequestParam(value = "status",required = false)Integer status ){
        Integer salesPermit = ShiroUtils.getUser().getSalesPermit();
        Assert.check(salesPermit == 0,ErrorCode.ERR_MALL_GOOD_UNPERMIT);
        Integer userId = ShiroUtils.getUser().getId();
        return new CommonListResponse<>().fromPage(mallOrderService.getSellerOrdersByStatus(pageNo,pageSize,userId,status));
    }

    @ApiImplicitParams({@ApiImplicitParam(name="id",value="订单ID")})
    @ApiOperation("获取订单详情")
    @GetMapping("/order")
    public CommonResponse<MallOrderVO> getOrder(@RequestParam("id") Integer id){
        return new CommonResponse<>(mallOrderService.getOrderVOById(id));
    }

    public void preventResubmission(String url,Integer userId){
        String key = SUBMIT + userId.toString() + url;
        String s = redisTemplate.opsForValue().get(key);
        redisTemplate.opsForValue().set(key,key,2L,TimeUnit.SECONDS);
        Assert.check(s != null,ErrorCode.ERR_REPEAT_SUBMIT);//重复提交
    }



}
