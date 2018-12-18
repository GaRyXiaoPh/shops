package com.cmd.wallet.webadmin.controller;

import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ErrorResponseData;
import com.cmd.wallet.common.enums.SalesPermit;
import com.cmd.wallet.common.mapper.MallApplyMapper;
import com.cmd.wallet.common.model.MallApply;
import com.cmd.wallet.common.vo.UserInfoVO;
import com.cmd.wallet.service.ImageService;
import com.cmd.wallet.service.MallShopService;
import com.cmd.wallet.service.UserService;
import com.cmd.wallet.webadmin.common.PageResponse;
import com.cmd.wallet.webadmin.common.PageUtil;
import com.github.pagehelper.Page;
import io.swagger.annotations.ApiParam;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;


/**
 * 商家审核控制器
 *
 * @author fengshuonan
 * @Date 2018-12-05 15:00:13
 */
@Controller
@RequestMapping("/tMallApply")
public class TMallApplyController extends BaseController {

    private String PREFIX = "/webadmin/tMallApply/";

    @Autowired
    private UserService userService;
    @Autowired
    private MallApplyMapper mallApplyMapper;
    @Autowired
    private MallShopService mallShopService;
    /**
     * 跳转到商家审核首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "tMallApply.html";
    }

    /**
     * 跳转到添加商家审核
     */
    @RequestMapping("/tMallApply_add")
    public String tMallApplyAdd() {
        return PREFIX + "tMallApply_add.html";
    }

    /**
     * 跳转到修改商家审核
     */
    @RequestMapping("/tMallApply_update/{tMallApplyId}")
    public String tMallApplyUpdate(@PathVariable Integer tMallApplyId, Model model) {

        MallApply byId = mallShopService.getMallApply(tMallApplyId);
        model.addAttribute("item",byId);
        LogObjectHolder.me().set(byId);
        return PREFIX + "tMallApply_edit.html";
    }

    /**
     * 获取商家审核列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(@ApiParam(value = "分页参数， 从1开始", required = true) @RequestParam(required = true) Integer offset,
                       @ApiParam(value = "每页记录数", required = true) @RequestParam(required = true) Integer limit,
                       @ApiParam(value = "状态：0，审核中，1已驳回，2已通过", required = true) @RequestParam(required = false) Integer status) {
        RowBounds rowBounds = new RowBounds(PageUtil.offsetToPage(offset, limit), limit);
        Page<MallApply> page = mallApplyMapper.findAll(status,rowBounds);
        return new PageResponse<MallApply>(page);
    }





    /**
     * 修改商家审核
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(MallApply mallApply) {
        MallApply mallApply1 = new MallApply();
        mallApply1.setId(mallApply.getId()).setStatus(mallApply.getStatus());
        MallApply byId = mallApplyMapper.findById(mallApply1.getId());
        if(byId != null && byId.getStatus() == 0){//审核中的才可以审核
            mallApplyMapper.update(mallApply1);
            if(mallApply1.getStatus() == 2){
                userService.updateUserSellPermitStatu(byId.getUserId(),SalesPermit.YES);
            }
        }else{
            return new ErrorResponseData("该申请记录不是审核中状态");
        }
        return SUCCESS_TIP;
    }

    /**
     * 商家审核详情
     */
    @RequestMapping(value = "/detail/{tMallApplyId}")
    @ResponseBody
    public Object detail(@PathVariable("tMallApplyId") Integer tMallApplyId) {

        return mallShopService.getMallApply(tMallApplyId);
    }
}
