package com.cmd.wallet.admin.controller;

import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.model.User;
import com.cmd.wallet.common.model.UserBill;
import com.cmd.wallet.common.response.CommonListResponse;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.common.vo.UpdateUserVo;
import com.cmd.wallet.common.vo.UserBillVO;
import com.cmd.wallet.common.vo.UserVo;
import com.cmd.wallet.service.UserService;
import com.github.pagehelper.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "用户管理模块")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation(value = "获取用户列表")
    @GetMapping("")
    public CommonListResponse<UserVo> getUserList(
            @ApiParam(value = "页数",name = "pageNo") @RequestParam(name = "pageNo",required = true) int pageNo,
            @ApiParam(value = "每页显示条数",name = "pageSize") @RequestParam(name = "pageSize",required = true) int pageSize,
            @ApiParam(value = "用户名",name = "userName") @RequestParam(name = "userName",required = false) String userName,
            @ApiParam(value = "Eth地址",name = "address", required = false) @RequestParam(name = "address", required = false)String address
    ) {
        //Page<UserVo> userList = userService.getUserList(pageNo,pageSize,userName,address);
        return CommonListResponse.fromPage(null);
    }

    @ApiOperation(value = "用户详情")
    @GetMapping("detail")
    public CommonResponse<User> getUserDetail(@ApiParam(value = "用户id", required = true) @RequestParam Integer userId) {
        User user = userService.getUserDetail(userId);
        return new CommonResponse(user);
    }

    @ApiOperation("更新用户信息")
    @PutMapping("")
    public CommonResponse updateUser(
            @RequestBody UpdateUserVo updateUserVO
    ) {
        userService.updateUser(updateUserVO);
        return new CommonResponse(ErrorCode.ERR_SUCCESS);
    }
    @ApiOperation(value = "获取账户流水")
    @GetMapping("/bill")
    public CommonListResponse<UserBillVO> getUserBillList(
            @ApiParam(value = "页数",name = "pageNo") @RequestParam(name = "pageNo",required = true) int pageNo,
            @ApiParam(value = "每页显示条数",name = "pageSize") @RequestParam(name = "pageSize",required = true) int pageSize,
            @ApiParam(value = "用户id",name = "userId") @RequestParam(name = "userId",required = false) Integer userId
    ){
        Page<UserBillVO> userBill = userService.getUserBill(pageNo,pageSize,userId);
        return CommonListResponse.fromPage(userBill);
    }
    @ApiOperation("启用用户")
    @PostMapping("enable")
    public CommonResponse enableUser(
            @ApiParam(value = "用户id数组", required = true) @RequestBody(required = true) List<Integer> userIds
    ) {
        userService.updateUserStatus(userIds, 0);
        return new CommonResponse(ErrorCode.ERR_SUCCESS);
    }

    @ApiOperation("禁用用户")
    @PostMapping("disable")
    public CommonResponse disable(
            @ApiParam(value = "用户id数组", required = true) @RequestBody(required = true) List<Integer> userIds
    ) {
        userService.updateUserStatus(userIds, 1);
        return new CommonResponse(ErrorCode.ERR_SUCCESS);
    }
}
