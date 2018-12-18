package com.cmd.wallet.webadmin.controller;

import cn.stylefeng.roses.core.base.controller.BaseController;
import com.cmd.wallet.common.mapper.UserMapper;
import com.cmd.wallet.common.model.User;
import com.cmd.wallet.common.vo.DiscoveryVO;
import com.cmd.wallet.service.DiscoveryService;
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

import javax.jws.WebParam;

@Controller
@RequestMapping("/discovery")
public class DiscoveryController extends BaseController {
    @Autowired
    DiscoveryService discoveryService;
    @Autowired
    UserMapper userMapper;

    private String PREFIX = "/webadmin/discovery/";

    @RequestMapping("")
    public String index() {
        return PREFIX + "discovery.html";
    }

    @ResponseBody
    @RequestMapping(value = "/list")
    public PageResponse<DiscoveryVO> list(@ApiParam(value = "手机号码", required = false) @RequestParam(required = false) String mobile,
                                          @ApiParam(value = "状态：1发布申请，2审核通过，3审核失败", required = false) @RequestParam(required = false) Integer status,
                                          @ApiParam(value = "分页参数， 从1开始", required = true) @RequestParam(required = true) Integer offset,
                                          @ApiParam(value = "每页记录数", required = true) @RequestParam(required = true) Integer limit) {
        if (mobile!=null && mobile.trim().length() ==0)
            mobile = null;

        int pageNo = PageUtil.offsetToPage(offset, limit);
        int pageSize = limit;

        Integer userId = null;
        User user = userMapper.getUserByMobile(mobile);
        if (user!=null){
            userId = user.getId();
        }

        Page<DiscoveryVO> list = discoveryService.getDiscovery(userId, status, pageNo, pageSize);
        return new PageResponse<DiscoveryVO>(list);
    }


    @RequestMapping("/discovery_edit/{id}")
    public String discoveryEdit(@PathVariable Integer id, Model model) {
        DiscoveryVO vo = discoveryService.getDiscoveryById(id);
        model.addAttribute("item", vo);
        return PREFIX + "discovery_edit.html";
    }

    @RequestMapping(value = "/discovery-pass/{id}")
    @ResponseBody
    public Object discovery_pass(@PathVariable Integer id, Model model) {
        discoveryService.adminCheckDiscoveryPass(id);
        return SUCCESS_TIP;
    }
    @RequestMapping(value = "/discovery-fail/{id}")
    @ResponseBody
    public Object discovery_fail(@PathVariable Integer id, Model model) {
        discoveryService.adminCheckDiscoveryFail(id);
        return SUCCESS_TIP;
    }
    @RequestMapping(value = "/discovery-delete/{id}")
    @ResponseBody
    public Object discovery_delete(@PathVariable Integer id, Model model) {
        discoveryService.adminDelDiscovery(id);
        return SUCCESS_TIP;
    }
}
