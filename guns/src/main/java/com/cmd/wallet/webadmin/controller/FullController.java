package com.cmd.wallet.webadmin.controller;

import cn.stylefeng.roses.core.base.controller.BaseController;
import com.cmd.wallet.common.model.ChangeConfig;
import com.cmd.wallet.common.model.Coin;
import com.cmd.wallet.common.vo.CoinVO;
import com.cmd.wallet.common.vo.UserBillVO;
import com.cmd.wallet.common.vo.UserInfoVO;
import com.cmd.wallet.service.ChangeConfigService;
import com.cmd.wallet.service.CoinService;
import com.cmd.wallet.service.UserService;
import com.cmd.wallet.webadmin.common.PageResponse;
import com.cmd.wallet.webadmin.common.PageUtil;
import com.github.pagehelper.Page;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/full")
public class FullController extends BaseController {
    private String PREFIX = "/webadmin/full/";

    @Autowired
    private UserService userService;

    @RequestMapping("/bill")
    public String index() {
        return PREFIX + "bill.html";
    }

    @RequestMapping(value = "/bill/list")
    @ResponseBody
    public PageResponse<UserBillVO> listBill(@ApiParam(value = "类型", required = false) @RequestParam(required = false) String types,
                                             @ApiParam(value = "用户名", required = false) @RequestParam(required = false) String userName,
                                             @ApiParam(value = "分页参数， 从1开始", required = true) @RequestParam(required = true) Integer offset,
                                             @ApiParam(value = "每页记录数", required = true) @RequestParam(required = true) Integer limit) {
        int pageNo = PageUtil.offsetToPage(offset, limit);
        int pageSize = limit;
        String[]type;
        if (types==null || types.trim().length()==0){
            type=null;
        }else{
            type=types.split(",");
        }

        Page<UserBillVO> bill =  userService.getUserBillByReason2(null, null, type,userName,pageNo, pageSize);
        return new PageResponse<UserBillVO>(bill);
    }

}
