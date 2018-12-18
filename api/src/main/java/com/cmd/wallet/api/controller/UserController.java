package com.cmd.wallet.api.controller;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.cmd.wallet.common.model.*;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.constants.OpLogType;
import com.cmd.wallet.common.response.CommonListResponse;
import com.cmd.wallet.common.utils.*;
import com.cmd.wallet.common.vo.*;
import com.cmd.wallet.common.annotation.Auth;
import com.cmd.wallet.common.annotation.OpLog;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.service.ConfigService;
import com.cmd.wallet.service.UploadService;
import com.cmd.wallet.service.UserService;
import com.github.cage.GCage;
import com.github.pagehelper.Page;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    ConfigService configService;
    @Autowired
    UploadService uploadService;

    @ApiOperation(value = "获取账户图形验证码", notes = "返回:图片文件")
    @ApiResponse(code = 200, message = "图片文件")
    @GetMapping("word.jpg")
    public ResponseEntity<byte[]> getNew(@ApiParam(value = "国际区号(86,852)",required = false) @RequestParam(required = false)String areaCode,
                                         @ApiParam(value = "手机号码",required = true) @RequestParam(required = true) String mobile,
                                         @RequestParam(value = "type", required = false, defaultValue = "G") @ApiIgnore String type) throws IOException {
        if (areaCode==null || areaCode.trim().length()==0)
            areaCode="86";

        if (mobile.contains("@"))
            Assert.check(!ToolsUtil.checkEmail(mobile), ErrorCode.ERR_USER_ACCOUNT_ERROR);

        String words = userService.getCaptchaImgWord(areaCode, mobile);

        CageUtil.generate(new GCage(), 1, "cg1", ".jpg", "colding");
        return CageUtil.generate(type, words);
    }

    @ApiOperation("获取短信邮箱验证码")
    @PostMapping("/verify-code")
    public CommonResponse verificationCode(
            @ApiParam(value = "国际区号（86,852）",required = false)  @RequestParam(required = false,name = "areaCode") String areaCode,
            @ApiParam(value = "手机号码/email",required = true)  @RequestParam(required = true)String phone,
            @ApiParam(value = "图形验证码", required = true) @RequestParam(required = true)String captchaImgCode,
            @ApiParam(value = "类型：注册(REGISTER) 重置登录密码(PASSWORD_FORGET) 设置支付密码(TRANSACTION_PASSWORD)" +
                    " 重置支付密码(TRANSACTION_FORGET) 更换手机(MOBILE_EDIT) 转账(TRANSFER_OUT) " +
                    " 绑定手机号码(MOBILE_BIND) 绑定邮箱(EMAIL_BIND) Google验证器(GOOGLE_BIND) 登录(LOGIN)",required = true)
            @RequestParam(required = true,defaultValue = "REGISTER")String type) {
        if (areaCode==null || areaCode.trim().length()==0)
            areaCode = "86";
        //不允许重复注册, EMAIL检查
        if (phone.contains("@")) {
            Assert.check(!ToolsUtil.checkEmail(phone), ErrorCode.ERR_USER_ACCOUNT_ERROR);
            if (type.equalsIgnoreCase("REGISTER")) {
                Assert.check(userService.getUserByEmail(phone) != null, ErrorCode.ERR_USER_EMAIL_EXIST);
            }
        } else {
            if (type.equalsIgnoreCase("REGISTER")) {
                Assert.check(userService.getUserByMobile(phone) != null, ErrorCode.ERR_USER_MOBILE_EXIST);
            }
        }

        userService.getSmsCode(areaCode, phone, type, captchaImgCode);
        return new CommonResponse(ErrorCode.ERR_SUCCESS);
    }

    @ApiOperation("验证短信邮件验证码")
    @PostMapping("/check-verify-code")
    public CommonResponse checkVerificationCode(
            @ApiParam(value = "国际区号（86,852）",required = false)  @RequestParam(required = false,name = "areaCode") String areaCode,
            @ApiParam(value = "手机号码/email",required = true)  @RequestParam(required = true)String phone,
            @ApiParam(value = "图形验证码", required = true) @RequestParam(required = true)String captchaImgCode,
            @ApiParam(value = "类型：注册(REGISTER) 重置登录密码(PASSWORD_FORGET) 设置支付密码(TRANSACTION_PASSWORD)" +
                    " 重置支付密码(TRANSACTION_FORGET) 更换手机(MOBILE_EDIT) 转账(TRANSFER_OUT) " +
                    " 绑定手机号码(MOBILE_BIND) 绑定邮箱(EMAIL_BIND) Google验证器(GOOGLE_BIND) 登录(LOGIN)",required = true)
            @RequestParam(required = true,defaultValue = "REGISTER")String type) {
        if (areaCode==null || areaCode.trim().length()==0)
            areaCode = "86";

        if (phone.contains("@"))
            Assert.check(!ToolsUtil.checkEmail(phone), ErrorCode.ERR_USER_ACCOUNT_ERROR);

        userService.checkSmsCode(areaCode, phone, type, captchaImgCode);
        return new CommonResponse(ErrorCode.ERR_SUCCESS);
    }

    @ApiOperation("注册")
    @Auth(requireLogin = false)
    @PostMapping("/register")
    public CommonResponse register(
            HttpServletRequest request, HttpServletResponse response,
            @ApiParam(value = "国际区号（86,852）",required = false)  @RequestParam(required = false,name = "areaCode") String areaCode,
            @ApiParam(value = "手机号码/email",required = true)  @RequestParam(required = true,name = "phone") String mobile,
            @ApiParam(value = "验证码"  ,required = true)  @RequestParam(required = true) String verificationCode,
            @ApiParam(value = "密码"    ,required = true)  @RequestParam(required = true) String password,
            @ApiParam(value = "邀请码"  ,required = true)  @RequestParam(required = true) String invite,
            @ApiParam(value = "支付密码", required = true) @RequestParam("payPassword") String payPassword,
            @ApiParam(value = "用户昵称", required = false) @RequestParam("nickName")String nickName
    ) {
        if (areaCode==null || areaCode.trim().length()==0)
            areaCode="86";

        if (nickName==null || nickName.trim().length()==0)
            nickName=null;

        Assert.check(mobile.contains("@"), ErrorCode.ERR_USER_ACCOUNT_ERROR);

        if (mobile.contains("@"))
            Assert.check(!ToolsUtil.checkEmail(mobile), ErrorCode.ERR_USER_ACCOUNT_ERROR);

        mobile = mobile.replace(" ","");

        User user = new User().setAreaCode(areaCode).setUserName(mobile).setPassword(password).setInviteCode(invite).setPayPassword(payPassword).setNickName(nickName);
        if (mobile.contains("@")){
            user.setEmail(mobile);
        }else{
            user.setMobile(mobile);

            //手机号检查
            Assert.check(!StringUtils.isNumeric(mobile), ErrorCode.ERR_USER_MOBILE_ERROR);
            if (areaCode.equals("86")) {
                Assert.check(mobile.length() < 11, ErrorCode.ERR_USER_MOBILE_ERROR);
            } else {
                Assert.check(mobile.length() < 5, ErrorCode.ERR_USER_MOBILE_ERROR);
            }
        }
        userService.register(user, invite, verificationCode, mobile);
        return new CommonResponse(ErrorCode.ERR_SUCCESS);
    }

    @ApiOperation("登录")
    @OpLog(type = OpLogType.OP_USER_LOGIN)
    @PostMapping("/login")
    public CommonResponse<TokenVo> Login(HttpServletRequest request,
            @ApiParam(value = "国家(86)", required = false) @RequestParam(required = false) String areaCode,
            @ApiParam(value = "用户名(手机、email)"  ,required = true)  @RequestParam(required = true) String phone,
            @ApiParam(value = "登录密码",required = true)  @RequestParam(required = true) String password,
            @ApiParam(value = "图形验证码",required = true)  @RequestParam(required = true) String captchaImgCode
    ) {
        //type=1web登录，2手机端登录
        int type = 1;
        String plat = request.getHeader("platform");
        if (plat!=null && (plat.equalsIgnoreCase("ios")||plat.equalsIgnoreCase("android"))){
            type=2;
        }

        if (areaCode==null || areaCode.trim().length()==0)
            areaCode = "86";

        Assert.check(phone.contains("@"), ErrorCode.ERR_USER_ACCOUNT_ERROR);

        if (phone.contains("@"))
            Assert.check(!ToolsUtil.checkEmail(phone), ErrorCode.ERR_USER_ACCOUNT_ERROR);

        String loginIp = IPUtils.getIpAddr(request);
        return new CommonResponse(userService.login(type, areaCode,phone, password, loginIp, captchaImgCode));
    }

    @ApiOperation("退出登录")
    @OpLog(type = OpLogType.OP_USER_LOGOUT)
    @GetMapping("/logout")
    public CommonResponse logout(HttpServletRequest request) {
        //type=1web登录，2手机端登录
        int type = 1;
        String plat = request.getHeader("platform");
        if (plat!=null && (plat.equalsIgnoreCase("ios")||plat.equalsIgnoreCase("android"))){
            type=2;
        }

        userService.logout(type, ShiroUtils.getUser().getId());
        ShiroUtils.logout();
        return new CommonResponse(ErrorCode.ERR_SUCCESS);
    }


    @ApiOperation("设置交易密码")
    @OpLog(type = OpLogType.OP_USER_SET_PAY_PWD, comment = "'payPassword=' + #payPassword")
    @PostMapping("/setting-pay-pwd")
    public CommonResponse setPayPassword(
            @ApiParam(value = "国际区号(86)", required = false) @RequestParam(required = false)String areaCode,
            @ApiParam(value = "手机号码/email", required = true) @RequestParam(required = true,name = "phone") String phone,
            @ApiParam(value = "验证码"  ,required = true)  @RequestParam(required = true) String verificationCode,
            @ApiParam(value = "交易密码",required = true)  @RequestParam(required = true) String payPassword) {
        if (areaCode==null || areaCode.trim().length()==0)
            areaCode="86";

        if (phone.contains("@"))
            Assert.check(!ToolsUtil.checkEmail(phone), ErrorCode.ERR_USER_ACCOUNT_ERROR);

        userService.setPayPassword(ShiroUtils.getUser().getId(), areaCode, phone, verificationCode, payPassword);
        return  new CommonResponse(ErrorCode.ERR_SUCCESS);
    }

    @ApiOperation("重置交易密码")
    @OpLog(type = OpLogType.OP_USER_EDIT_PAY_PWD)
    @PostMapping("/reset-pay-pwd")
    public CommonResponse resetPayPassword(
            @ApiParam(value = "国际区号", required = false) @RequestParam(required = false)String areaCode,
            @ApiParam(value = "手机号码/email", required = true) @RequestParam(required = true,name = "phone") String phone,
            @ApiParam(value = "验证码"  ,required = true)  @RequestParam(required = true) String verificationCode,
            @ApiParam(value = "新密码",required = true)  @RequestParam(required = true) String payPassword) {
        if (areaCode==null || areaCode.trim().length()==0)
            areaCode="86";

        if (phone.contains("@"))
            Assert.check(!ToolsUtil.checkEmail(phone), ErrorCode.ERR_USER_ACCOUNT_ERROR);

        userService.resetPayPassword(areaCode, phone, verificationCode, payPassword);
        return new CommonResponse(ErrorCode.ERR_SUCCESS);
    }

    @ApiOperation("重设登录密码")
    @PostMapping("/reset-pwd")
    public CommonResponse resetPassword(
            @ApiParam(value = "国际区号", required = false) @RequestParam(required = false)String areaCode,
            @ApiParam(value = "手机号码/email", required = true) @RequestParam(required = true,name = "phone") String phone,
            @ApiParam(value = "验证码"  ,required = true)  @RequestParam(required = true) String verificationCode,
            @ApiParam(value = "密码",required = true)  @RequestParam(required = true) String password
    ) {
        if (areaCode==null || areaCode.trim().length()==0)
            areaCode="86";

        if (phone.contains("@"))
            Assert.check(!ToolsUtil.checkEmail(phone), ErrorCode.ERR_USER_ACCOUNT_ERROR);

        userService.resetPassword(areaCode, phone, verificationCode, password);
        return new CommonResponse(ErrorCode.ERR_SUCCESS);
    }

    @ApiOperation("修改登录密码")
    @PostMapping("/modify-pwd")
    public CommonResponse modifyPassword(@RequestParam("oldPassword")String oldPassword,
                                         @RequestParam("newPassword")String newPassword){
        userService.modifyPassword(ShiroUtils.getUser().getId(), oldPassword, newPassword);
        return new CommonResponse();
    }

    @ApiOperation("修改交易密码")
    @PostMapping("/modify-pay-pwd")
    public CommonResponse modifyPayPassword(@RequestParam("oldPassword")String oldPassword,
                                         @RequestParam("newPassword")String newPassword){
        userService.modifyPayPassword(ShiroUtils.getUser().getId(), oldPassword, newPassword);
        return new CommonResponse();
    }

    @ApiOperation("更换手机号")
    @PostMapping("/reset-phone")
    public CommonResponse resetPhone(
            @ApiParam(value = "国际区号", required = false) @RequestParam(required = false)String areaCode,
            @ApiParam(value = "旧手机号码", required = true) @RequestParam(required = true,name = "oldPhone") String oldPhone,
            @ApiParam(value = "验证码"  ,required = true)  @RequestParam(required = true) String verificationCode,
            @ApiParam(value = "新手机号码",required = true)  @RequestParam(required = true) String newPhone){
        if (areaCode==null || areaCode.trim().length()==0)
            areaCode="86";

        userService.updateUserMobile(areaCode, oldPhone, verificationCode, newPhone);
        return new CommonResponse(ErrorCode.ERR_SUCCESS);
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/user-info")
    public CommonResponse<UserInfoVO> getUserInfo(){
        return new CommonResponse<>(userService.getUserInfo(ShiroUtils.getUser().getId()));
    }

    @ApiOperation("获取国家代码")
    @GetMapping("/national-code")
    public CommonResponse<List<NationalCode>> getNationalCode(){
        return new CommonResponse(userService.getNationalCode());
    }

    @ApiOperation("绑定邮箱")
    @PostMapping("/bind-mobile-email")
    public CommonResponse bindEmail(@RequestParam(value = "areaCode", required = false)String areaCode,
                                    @RequestParam("mobile")String mobile,
                                    @RequestParam("verificationCode") String verificationCode){
        if (areaCode==null || areaCode.trim().length()==0)
            areaCode="86";

        if (mobile.contains("@"))
            Assert.check(!ToolsUtil.checkEmail(mobile), ErrorCode.ERR_USER_ACCOUNT_ERROR);

        if (mobile.contains("@")){
            userService.bindEmail(ShiroUtils.getUser().getId(), areaCode, mobile, verificationCode);
        } else {
            userService.bindMobile(ShiroUtils.getUser().getId(), areaCode, mobile, verificationCode);
        }

        return new CommonResponse();
    }

    @ApiOperation("修改昵称")
    @GetMapping("/change-nick-name")
    public CommonResponse changeNIkcName(
            @ApiParam(value = "新昵称", required = true)@RequestParam String nickName
    ){
        userService.changeNickName(ShiroUtils.getUser().getId(), nickName);
        return new CommonResponse();
    }

    @ApiOperation("修改头像")
    @PostMapping("/change-image")
    public CommonResponse changeImage(@RequestParam String image){
        userService.updateUser(new UpdateUserVo().setId(ShiroUtils.getUser().getId()).setImage(image));
        return new CommonResponse();
    }

    @ApiOperation("获取3级推荐关系")
    @GetMapping("/get-user-relation")
    public CommonResponse<UserRelationVO> getUserRelation(@RequestParam(required = false)Integer userId){
        if (userId==null) {
            userId = ShiroUtils.getUser().getId();
        }
        UserRelationVO userRelationVO = userService.getUserRelationByUserId(userId, 0);
        return new CommonResponse<>(userRelationVO);
    }

    @ApiOperation("获取用户统计数据")
    @GetMapping("/get-user-stat")
    public CommonResponse<UserStat> getUserStat(@RequestParam(required = false)Integer userId){
        if (userId==null){
            userId = ShiroUtils.getUser().getId();
        }
        return new CommonResponse<>(userService.getUserStat(userId));
    }

    @ApiOperation("二维码")
    @GetMapping("/qrcode.jpg")
    public ResponseEntity<byte[]> getQRCode(@RequestParam("content")String content,
                                            @RequestParam("width")Integer width,
                                            @RequestParam("height")Integer height) {
        if (width==null || width.intValue()>1000)
            width=300;
        if (height==null || height.intValue()>1000)
            height=300;

        return QRCodeUtil.generate(content, width,height);
    }

    @ApiOperation("用户等级")
    @GetMapping("/user-level")
    public CommonResponse<String> getUserLevel(){
        return new CommonResponse<>();
    }

    @ApiOperation("全局参数配置")
    @GetMapping("/config")
    public CommonResponse<String> getConfig(@RequestParam("key")String key){
        return new CommonResponse(configService.getConfigValue(key));
    }

    @PostMapping("/img/upload")
    public CommonResponse<String> uploadImg(@RequestParam("upload") MultipartFile multipartFile)  {
        if (multipartFile.isEmpty() || StringUtils.isBlank(multipartFile.getOriginalFilename())) {
            throw new RuntimeException("image params error");
        }
        String contentType = multipartFile.getContentType();
        if (!contentType.contains("")) {
            throw new RuntimeException("image error");
        }

        try {
            String fileName = multipartFile.getOriginalFilename();
            FileInputStream fileInputStream = (FileInputStream) multipartFile.getInputStream();
            String url = uploadService.upload(fileName, fileInputStream);
            if (url!=null) {
                return new CommonResponse<>(url);
            }
        } catch (Exception e) {
            ;
        }
        Assert.check(true, ErrorCode.ERR_PARAM_ERROR);
        return new CommonResponse<>(null);
    }

    @ApiOperation("获取我的团队")
    @GetMapping("/group")
    public CommonResponse group(){
        return new CommonResponse<>(userService.getGroup(ShiroUtils.getUser()));
    }

    @ApiOperation("获取全平台收益")
    @GetMapping("/global")
    public CommonResponse<GroupVO> global() {
        return new CommonResponse<>(userService.getGlobal(ShiroUtils.getUser()));
    }

}
