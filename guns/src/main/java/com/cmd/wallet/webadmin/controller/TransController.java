package com.cmd.wallet.webadmin.controller;


import cn.stylefeng.roses.core.base.controller.BaseController;
import com.cmd.wallet.common.model.*;
import com.cmd.wallet.service.ReceivedCoinService;
import com.cmd.wallet.service.SendCoinService;
import com.cmd.wallet.service.UserService;
import com.cmd.wallet.webadmin.common.PageResponse;
import com.cmd.wallet.webadmin.common.PageUtil;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/trans")
public class TransController extends BaseController {

    @Autowired
    SendCoinService sendCoinService;
    @Autowired
    ReceivedCoinService receivedCoinService;
    @Autowired
    UserService userService;

    private String PREFIX = "/webadmin/trans/";


    @RequestMapping("/check")
    public String indexCheck() {
        return PREFIX + "check.html";
    }

    @RequestMapping(value = "/check/list")
    @ResponseBody
    public Object listCheck(@RequestParam(required = false) String mobile,
                       @RequestParam(required = false) String address,
                       @RequestParam(required = false) String coinName,
                       @RequestParam(required = false) Integer status,
                       @RequestParam(required = true) Integer offset,
                       @RequestParam(required = true) Integer limit) {
        int pageNo = PageUtil.offsetToPage(offset, limit);
        int pageSize = limit;
        Integer[] statusex;
        if (status!=null){
            statusex = new Integer[]{status};
        }else{
            statusex = new Integer[]{0,1,2};
        }
        Integer userId = null;
        if (mobile!=null) {
            User user = userService.getUserByMobile(mobile);
            if (user!=null)
                userId = user.getId();
        }
        if (address!=null && address.trim().length()==0)
            address = null;
        Page<SendCoin> pg = sendCoinService.getTransferList2(userId, null, statusex, address, pageNo, pageSize);
        return new PageResponse<SendCoin>(pg);
    }

    @RequestMapping("/check/check_pass/{id}")
    @ResponseBody
    public Object checkPass(@PathVariable Integer id) {
        sendCoinService.transferCheckPass(id, 0);
        return SUCCESS_TIP;
    }

    @RequestMapping("/check/check_fail/{id}")
    @ResponseBody
    public Object checkFail(@PathVariable Integer id) {
        sendCoinService.transferCheckFail(id, 0);
        return SUCCESS_TIP;
    }


    @RequestMapping("/in")
    public String indexIn() {
        return PREFIX + "in.html";
    }

    @RequestMapping(value = "/in/list")
    @ResponseBody
    public Object listIn(@RequestParam(required = false) String mobile,
                            @RequestParam(required = false) String address,
                            @RequestParam(required = false) String coinName,
                            @RequestParam(required = true) Integer offset,
                            @RequestParam(required = true) Integer limit) {
        int pageNo = PageUtil.offsetToPage(offset, limit);
        int pageSize = limit;

        Integer userId = null;
        if (mobile!=null) {
            User user = userService.getUserByMobile(mobile);
            if (user!=null)
                userId = user.getId();
        }
        if (address!=null && address.trim().length()==0)
            address = null;

        Page<ReceivedCoin> pg = receivedCoinService.getTransferList(userId, null, pageNo, pageSize);
        return new PageResponse<ReceivedCoin>(pg);
    }

}
