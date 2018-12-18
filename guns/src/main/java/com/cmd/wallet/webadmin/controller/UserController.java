package com.cmd.wallet.webadmin.controller;

import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.cmd.wallet.common.constants.UserBillReason;
import com.cmd.wallet.common.enums.SalesPermit;
import com.cmd.wallet.common.model.Coin;
import com.cmd.wallet.common.model.OssConfig;
import com.cmd.wallet.common.model.User;
import com.cmd.wallet.common.oss.OSSFactory;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.common.vo.*;
import com.cmd.wallet.service.UserCoinService;
import com.cmd.wallet.service.UserService;
import com.cmd.wallet.service.WalletService;
import com.cmd.wallet.webadmin.common.PageResponse;
import com.cmd.wallet.webadmin.common.PageUtil;
import com.github.pagehelper.Page;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
    private String PREFIX = "/webadmin/user/";

    @Autowired
    UserService userService;
    @Autowired
    UserCoinService userCoinService;
    @Autowired
    WalletService walletService;

    @RequestMapping("")
    public String index() {
        return PREFIX + "user.html";
    }

    @ResponseBody
    @RequestMapping(value = "/list")
    public PageResponse<UserInfoVO> list(@ApiParam(value = "手机号码", required = false) @RequestParam(required = false) String mobile,
                                         @ApiParam(value = "用户邮箱",required = false) @RequestParam(required = false) String email,
                                         @ApiParam(value = "分页参数， 从1开始", required = true) @RequestParam(required = true) Integer offset,
                                         @ApiParam(value = "每页记录数", required = true) @RequestParam(required = true) Integer limit
    ) {
        if (email==null || email.trim().length()==0)
            email = null;
        if (mobile==null || mobile.trim().length() ==0)
            mobile = null;
        Page<UserInfoVO> userList = userService.getUserList(PageUtil.offsetToPage(offset, limit), limit, mobile, email, null);
        return new PageResponse<UserInfoVO>(userList);
    }

    //跳转到修改用户界面
    @RequestMapping("/user_edit/{id}")
    public String userUpdate(@PathVariable Integer id, Model model) {
        User user = userService.getUserByUserId(id);
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);
        model.addAttribute("item", vo);
        return PREFIX + "user_edit.html";
    }

    //修改用户
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(UserInfoVO vo) {
        userService.updateUser(new UpdateUserVo().setId(vo.getId()).setEmail(vo.getEmail()).setMobile(vo.getMobile()).setNickName(vo.getNickName()));
        return SUCCESS_TIP;
    }

    //资产流水页面跳转
    @RequestMapping(value = "/user_bill/{id}")
    public String userBill(@PathVariable Integer id, Model model){
        model.addAttribute("id", id);
        return PREFIX + "user_bill.html";
    }
    @ResponseBody
    @RequestMapping(value = "/user_bill/list/{userId}")
    public PageResponse<UserBillVO> user_bill_list(@PathVariable Integer userId,
                                         @ApiParam(value = "分页参数， 从1开始", required = true) @RequestParam(required = true) Integer offset,
                                         @ApiParam(value = "每页记录数", required = true) @RequestParam(required = true) Integer limit) {
        int pageNo = PageUtil.offsetToPage(offset, limit);
        int pageSize = limit;
        Page<UserBillVO> bill =  userService.getUserBill(pageNo, pageSize, userId);
        return new PageResponse<UserBillVO>(bill);
    }



    //用户资产页面跳转
    @RequestMapping(value = "/user_coin/{id}")
    public String userCoin(@PathVariable Integer id, Model model){
        model.addAttribute("id", id);
        return PREFIX + "user_coin.html";
    }
    @ResponseBody
    @RequestMapping(value = "/user_coin/list/{userId}")
    public List<UserCoinVO> user_coin_list(@PathVariable Integer userId) {
        List<UserCoinVO> bill = userCoinService.getUserCoinByUserId(userId);//walletService.getUserCoinList(userId);//userCoinService.getUserCoinByUserId(userId);//userService.getUserBill(pageNo, pageSize, userId);
        return bill;
    }


    //跳转拨币页面
    @RequestMapping("/user-dispatch/{id}")
    public String userDispatch(@PathVariable Integer id, Model model){
        User user = userService.getUserByUserId(id);
        model.addAttribute("id", id);
        model.addAttribute("userName", user.getUserName());
        return PREFIX + "user_dispatch.html";
    }
    //系统拨币
    @RequestMapping("/dispatch-coin")
    @ResponseBody
    public Object dispatchCoin(@RequestParam("id")Integer id,
                               @RequestParam("coinName")String coinName,
                               @RequestParam("amount")BigDecimal amount){
        userCoinService.changeUserCoin(id, coinName, amount, BigDecimal.ZERO,BigDecimal.ZERO, UserBillReason.DISPATCH_RELEASE, "系统拨币");
        return SUCCESS_TIP;
    }
    /**
     * 上传图片
     */
    @RequestMapping(method = RequestMethod.POST, path = "/upload")
    @ResponseBody
    public String upload(@RequestPart("file") MultipartFile picture) {
        String pictureName ;
        try {
            pictureName = OSSFactory.build(OssConfig.OSS_QCLOUD).upload(picture);
        } catch (Exception e) {
            throw new ServiceException(BizExceptionEnum.UPLOAD_ERROR);
        }
        return pictureName;
    }
    /**
     * 给用户开启销售商品权限
     * @return
     */
    @ApiOperation("修改用户销售商品权限")
    @PostMapping("/sell-permit")
    @ResponseBody
    public CommonResponse sellPermit(@ApiParam("用户ID") @RequestParam Integer id,@ApiParam("状态：0关闭，1开启") @RequestParam Integer status) {
        SalesPermit salesPermit = status == 1 ? SalesPermit.YES : SalesPermit.NO;
        userService.updateUserSellPermitStatu(id,salesPermit);
        return new CommonResponse();
    }

    /**
     * 修改用户自品牌权限
     * @return
     */
    @ApiOperation("修改用户自品牌权限")
    @PostMapping("/brand-permit")
    @ResponseBody
    public CommonResponse brandPermit(@ApiParam("用户ID") @RequestParam Integer id,@ApiParam("状态：0关闭，1开启") @RequestParam Integer status) {
        UpdateUserVo updateUserVo = new UpdateUserVo().setId(id).setBrandPermit(status);
        userService.updateUser(updateUserVo);
        return new CommonResponse();
    }

    /**
     * 修改用户全平台权限
     * @return
     */
    @ApiOperation("修改用户全平台权限")
    @PostMapping("/global-permit")
    @ResponseBody
    public CommonResponse globalPermit(@ApiParam("用户ID") @RequestParam Integer id,@ApiParam("状态：0关闭，1开启") @RequestParam Integer status) {
        UpdateUserVo updateUserVo = new UpdateUserVo().setId(id).setGlobalPermit(status);
        userService.updateUser(updateUserVo);
        return new CommonResponse();
    }
}
