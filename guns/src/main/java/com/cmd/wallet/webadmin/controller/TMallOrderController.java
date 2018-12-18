package com.cmd.wallet.webadmin.controller;

import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.roses.core.base.controller.BaseController;
import com.cmd.wallet.common.enums.ReputationStauts;
import com.cmd.wallet.common.model.MallCategory;
import com.cmd.wallet.common.model.TImageModel;
import com.cmd.wallet.common.model.TMallGoodModel;
import com.cmd.wallet.common.model.User;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.common.vo.MallGoodVO;
import com.cmd.wallet.common.vo.MallOrderListAdminVO;
import com.cmd.wallet.service.*;
import com.cmd.wallet.webadmin.common.PageResponse;
import com.cmd.wallet.webadmin.common.PageUtil;
import com.github.pagehelper.Page;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品列表控制器
 *
 * @author fengshuonan
 * @Date 2018-12-06 12:10:31
 */
@Controller
@RequestMapping("/tMallOrder")
public class TMallOrderController extends BaseController {

    private String PREFIX = "/webadmin/tMallOrder/";

    @Autowired
    private MallOrderService mallOrderService;

    /**
     * 跳转到商品列表首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "tMallOrder.html";
    }

    /**
     * 跳转到添加商品列表
     */
    @RequestMapping("/tMallOrder_add")
    public String tMallOrderAdd() {
        return PREFIX + "tMallOrder_add.html";
    }

    /**
     * 跳转到修改商品列表
     */
    @RequestMapping("/tMallOrder_update/{tMallOrderId}")
    public String tMallOrderUpdate(@PathVariable("tMallOrderId") Integer tMallOrderId, Model model) {
        MallOrderListAdminVO orderByAdmin = mallOrderService.getOrderByAdmin(tMallOrderId);
        model.addAttribute("item",orderByAdmin);
        LogObjectHolder.me().set(orderByAdmin);
        return PREFIX + "tMallOrder_edit.html";
    }

    /**
     * 获取商品列表列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(@ApiParam("状态:1:待发货,2:已发货,3:已完成,4取消订单，5：退货中，6：已退货") @RequestParam(required = false) Integer status,
                       @ApiParam("卖家姓名") @RequestParam(required = false) String buyer,
                       @ApiParam("卖家姓名") @RequestParam(required = false) String seller,
                       @ApiParam("商品名称") @RequestParam(required = false) String goodName,
                       @ApiParam(value = "分页参数， 从1开始", required = true) @RequestParam(required = true) Integer offset,
                       @ApiParam(value = "每页记录数", required = true) @RequestParam(required = true) Integer limit) {
        ;
        Page<MallOrderListAdminVO> ordersByAdmin = mallOrderService.getOrdersByAdmin(buyer, seller, status, goodName, PageUtil.offsetToPage(offset, limit), limit);
        return new PageResponse<MallOrderListAdminVO>(ordersByAdmin);

    }


    @ApiImplicitParams({@ApiImplicitParam(name="id",value="订单ID")})
    @ApiOperation("确认收货")
    @PostMapping("/confirm")
    @ResponseBody
    public CommonResponse confirm(@RequestParam("id") Integer id){
        mallOrderService.confirmOrder(id,ReputationStauts.GOOD.getValue());
        return new CommonResponse();
    }
    @ApiOperation("取消订单")
    @ApiImplicitParams({@ApiImplicitParam(name="id",value="订单ID")})
    @PostMapping("/cancel")
    @ResponseBody
    public CommonResponse cancelOrder(@RequestParam Integer orderId){
        mallOrderService.cancelUnsendOrder(orderId);
        return new CommonResponse();
    }
    @ApiOperation("确认退货")
    @ApiImplicitParams({@ApiImplicitParam(name="id",value="订单ID")})
    @PostMapping("/return")
    @ResponseBody
    public CommonResponse confirmReturnOrder(@RequestParam Integer id){
        mallOrderService.confirmReturnOrder(id);
        return new CommonResponse();
    }



//    /**
//     * 商品列表详情
//     */
//    @RequestMapping(value = "/detail/{tMallGoodId}")
//    @ResponseBody
//    public Object detail(@PathVariable("tMallGoodId") Integer tMallGoodId) {
//        return tMallGoodService.selectById(tMallGoodId);
//    }
}
