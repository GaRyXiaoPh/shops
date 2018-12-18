package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.ConfigKey;
import com.cmd.wallet.common.constants.UserBillReason;
import com.cmd.wallet.common.enums.SalesPermit;
import com.cmd.wallet.common.oauth2.OAuth2Token;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.enums.SmsCaptchaType;
import com.cmd.wallet.common.mapper.*;
import com.cmd.wallet.common.model.*;
import com.cmd.wallet.common.utils.*;
import com.cmd.wallet.common.vo.*;
import com.github.pagehelper.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class UserService {
    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${token.expireTime:43200}")
    private int TOKENEXPIRETIME;

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserBillMapper userBillMapper;
    @Autowired
    SmsService smsService;
    @Autowired
    ConfigService configService;
    @Autowired
    UserCoinService userCoinService;
    @Autowired
    NationalCodeMapper nationalCodeMapper;
    @Autowired
    AppVersionMapper appVersionMapper;
    @Autowired
    ConfigLevelMapper configLevelMapper;
    @Autowired
    private EthAddressMapper ethAddressMapper;
    @Autowired
    private UserTaskMapper userTaskMapper;
    @Autowired
    private UserStatMapper userStatMapper;
    @Autowired
    private UserWordsMapper userWordsMapper;
    @Autowired
    private UserEarningsMapper userEarningsMapper;
    @Autowired
    private MallShopMapper mallShopMapper;
    @Autowired
    private MallGoodService mallGoodService;
    @Autowired
    private MallOrderService mallOrderService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private MallApplyMapper mallApplyMapper;

    //获取用户token
    public UserToken getUserTokenByUserId(int userId) {
        return userMapper.getUserTokenByUserId(userId);
    }
    public UserToken getUserTokenByToken(String token) {
        return userMapper.getUserTokenByToken(token);
    }

    //获取用户
    public User getUserByMobile(String mobile) {
        return userMapper.getUserByMobile(mobile);
    }
    public User getUserByUserName(String userName) {
        return userMapper.getUserByUserName(userName);
    }
    public User getUserByUserId(int userId) {
        return userMapper.getUserByUserId(userId);
    }
    public User getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }
    public List<User> adminGetUserByMobile(List<String> mobile) {
        return userMapper.adminGetUserByMobile(mobile);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //获取图像验证码
    public String getCaptchaImgWord(String areaCode, String mobile) {
        return smsService.getCaptchaImgWord(areaCode, mobile);
    }

    //获取短信验证码
    public void getSmsCode(String areaCode, String mobile, String type, String captchaImgCode) {
        Assert.check(mobile == null || mobile.length() == 0, ErrorCode.ERR_USER_MOBILE_ERROR);
        smsService.sendSmsCaptcha(areaCode, mobile, type, captchaImgCode);
    }

    //验证短信验证码
    public void checkSmsCode(String areaCode, String mobile, String type, String captchaImgCode) {
        Assert.check(!smsService.checkCaptchaFirst(areaCode, mobile, type, captchaImgCode), ErrorCode.ERR_USER_SMSCODE_ERROR);
    }

    //递归查找左子树叶子节点
    private Integer findLeft(Integer userId) {
        User user = userMapper.getUserByUserId(userId);
        Assert.check(user == null, ErrorCode.ERR_USER_ACCOUNT_ERROR);

        if (user.getLeftChild() == null) {
            return user.getId();
        } else {
            if (userId.intValue() >= user.getLeftChild().intValue()) {
                logger.error("二叉树错误，联系开发人员修复数据，错误的用户：" + userId + ":" + user.getLeftChild());
                Assert.check(true, ErrorCode.ERR_USER_ACCOUNT_ERROR);
            }
            return findLeft(user.getLeftChild());
        }
    }

    //循环查找左子树叶子节点
    private Integer findLeft2(Integer userId) {
        do {
            User user = userMapper.getUserByUserId(userId);
            Assert.check(user == null, ErrorCode.ERR_USER_ACCOUNT_ERROR);

            if (user.getLeftChild()==null){
                return user.getId();
            } else {
                if (userId.intValue() >= user.getLeftChild().intValue()) {
                    logger.error("二叉树错误，联系开发人员修复数据，错误的用户：" + userId + ":" + user.getLeftChild());
                    Assert.check(true, ErrorCode.ERR_USER_ACCOUNT_ERROR);
                }
                userId = user.getLeftChild();
            }
        }while (true);
    }

    private Integer findInvite(String mobile, String inviteCode) {
        if (inviteCode == null || inviteCode.trim().length() <= 0)
            return null;

        User user = userMapper.getUserByInviteCode(inviteCode);
        Assert.check(user == null, ErrorCode.ERR_USER_ACCOUNT_ERROR);

        if (user.getLeftInvite().equalsIgnoreCase(inviteCode)) {
            //左子树
            if (user.getLeftChild() == null) {
                return user.getId();
            } else {
                return findLeft2(user.getLeftChild());
            }
        } else if (user.getRightInvite().equalsIgnoreCase(inviteCode)) {
            //右子树
            if (user.getRightChild() == null) {
                return user.getId();
            } else {
                return findLeft2(user.getRightChild());
            }
        }
        return null;
    }

    //用户注册@Transactional(rollbackFor=Exception.class)
    @Transactional
    public void register(User user, String inviteCode, String code, String mobile) {
        Assert.check(user.getMobile() == null && user.getEmail() == null, ErrorCode.ERR_USER_ACCOUNT_ERROR);
        Assert.check(user.getPassword() == null, ErrorCode.ERR_USER_PASSWORD_ERROR);
        Assert.check(!StringUtils.isNotBlank(inviteCode), ErrorCode.ERR_PARAM_ERROR);

        if (user.getUserName() == null)
            user.setUserName(user.getMobile());

        if (user.getNickName()!=null) {
            if (userMapper.getUserByNickName(user.getNickName()) != null)
                Assert.check(true, ErrorCode.ERR_USER_NICK_NAME_EXIST);
        }

        if (mobile.contains("@")) {
            Assert.check(userMapper.getUserByEmail(user.getEmail()) != null, ErrorCode.ERR_USER_EMAIL_EXIST);
            Assert.check(!smsService.checkCaptcha(user.getAreaCode(), user.getEmail(), SmsCaptchaType.REGISTER.getValue(), code), ErrorCode.ERR_USER_SMSCODE_ERROR);
        } else {
            Assert.check(userMapper.getUserByMobile(user.getMobile()) != null, ErrorCode.ERR_USER_MOBILE_EXIST);
            Assert.check(!smsService.checkCaptcha(user.getAreaCode(), user.getMobile(), SmsCaptchaType.REGISTER.getValue(), code), ErrorCode.ERR_USER_SMSCODE_ERROR);
        }

        user.setPassword(EncryptionUtil.MD5(user.getPassword()));
        user.setPayPassword(EncryptionUtil.MD5(user.getPayPassword()));

        //生成邀请码
        user.setLeftInvite("1"+CageUtil.getWords(10));
//        user.setRightInvite("2"+CageUtil.getWords(10));

        //查找直接推荐人,
        User tmp = userMapper.getUserByInviteCode(inviteCode);
        Assert.check(tmp == null, ErrorCode.ERR_REFERRER_NOT_EXIST);
        Assert.check(tmp.getStatus().intValue()!=0, ErrorCode.ERR_USER_INVITE_CODE_ERROR);

        String image = configService.getDefaultHeadImage();
//        Integer referrerUserId = findInvite(tmp.getMobile(), inviteCode);
        Assert.check(tmp.getId() == null, ErrorCode.ERR_REFERRER_NOT_EXIST);
        user.setReferrer(tmp.getId());
        user.setInviteCode(inviteCode);
        user.setInvite(tmp.getId());
        user.setImage(image);
        userMapper.addUser(user);

        //查找间接推荐人
        User indirectUser = userMapper.getUserByUserId(tmp.getInvite());
        //添加直接推荐人的总数
        userMapper.updateUserByUserId(new User().setId(tmp.getId()).setLeftChild(tmp.getLeftChild()+1));
        //添加间接推荐人的总数
        userMapper.updateUserByUserId(new User().setId(indirectUser.getId()).setRightChild(indirectUser.getRightChild()+1));

//        //更新推荐人的左右子树
//        if (tmp.getRightInvite().equalsIgnoreCase(inviteCode) && referrerUserId.intValue() == tmp.getId().intValue()) {
//            userMapper.updateUserByUserId(new User().setId(user.getReferrer()).setRightChild(user.getId()));
//        } else {
//            userMapper.updateUserByUserId(new User().setId(user.getReferrer()).setLeftChild(user.getId()));
//        }

        //生成默认的Token
        UserToken token = new UserToken().setUserId(user.getId()).setToken(TokenGeneratorUtil.generateValue())
                .setExpireTime(new Date(new Date().getTime() + TOKENEXPIRETIME * 1000));
        userMapper.addUserToken(token);

        //加入到统计节点
//        userStatMapper.add(new UserStat().setUserId(user.getId()));
//        userEarningsMapper.add(new UserEarnings().setUserId(user.getId()).setFreezeReward(BigDecimal.ZERO).setGiveReward(BigDecimal.ZERO));
//        userTaskMapper.add(UserTask.TASK_STAT_NODES, user.getId(), "", 0);
//        userTaskMapper.add(UserTask.TASK_REGISTER_ADDRESS, user.getId(),"", 0);

        //注册赠送平台币:BTST
//        String platCoin = configService.getPlatformCoinName();
//        if (platCoin != null) {
//            Integer maxRegister = configService.getRegisterReward();
//            if (maxRegister==null)
//                maxRegister = 5;
//            if (maxRegister.intValue()>0){
//                double registerReward = (int)(Math.random()*100%maxRegister.intValue())+1;//configService.getRegisterReward();
//                if (registerReward > 0) {
//                    userCoinService.changeUserCoin(user.getId(), platCoin, BigDecimal.valueOf(registerReward), BigDecimal.ZERO, UserBillReason.REGISTER_REWARD, "注册赠送" + platCoin);
//                }
//            }
//        }
    }

    //用户登录
    @Transactional
    public TokenVo login(int type, String areaCode, String mobile, String password, String loginIp, String captchaImgCode) {

        //图形验证码检查
        smsService.CheckCaptchaImgWord(areaCode, mobile, captchaImgCode);

        User user = null;
        if (mobile.contains("@")) {
            user = userMapper.getUserByEmail(mobile);
        } else {
            user = userMapper.getUserByMobile(mobile);
        }

        Assert.check(user == null, ErrorCode.ERR_USER_NOT_EXIST);
        //Assert.check(user.getStatus() == 1, ErrorCode.ERR_USER_DISABLE);
        Assert.check(!EncryptionUtil.checkMD5(password, user.getPassword()), ErrorCode.ERR_USER_PASSWORD_ERROR);

        //生成token
        UserToken token = new UserToken().setUserId(user.getId()).setToken(TokenGeneratorUtil.generateValue())
                .setExpireTime(new Date(new Date().getTime() + TOKENEXPIRETIME * 1000));
        if (userMapper.getUserTokenByUserId(user.getId()) == null) {
            userMapper.addUserToken(token);
        } else {
            userMapper.updateUserToken(token);
        }

        //修改最后登录IP
        userMapper.updateUserByUserId(new User().setId(user.getId()).setLastLoginIp(loginIp));

        Subject subject = ShiroUtils.getSubject();
        try {
            subject.login(new OAuth2Token(token.getToken(), true));
        } catch (IncorrectCredentialsException e1) {
            Assert.check(true, ErrorCode.ERR_TOKEN_NOT_EXIST);
        } catch (LockedAccountException e2) {
            Assert.check(true, ErrorCode.ERR_TOKEN_NOT_EXIST);
        }

        //返回的数据
        boolean isPayPassword = (user.getPayPassword() != null && user.getPayPassword().length() > 0) ? true : false;
        boolean isMobile = user.getMobile() != null && user.getMobile().length() > 0 ? true : false;
        boolean isEmail = user.getEmail() != null && user.getEmail().length() > 0 ? true : false;

        boolean isMnemonic = false;
        UserWords userWords =  userWordsMapper.getUserWords(user.getId());
        if (userWords!=null){
            isMnemonic = userWords.getStatus()==UserWords.MEMC_FINISH?true:false;
        }

        return new TokenVo().setToken(token.getToken()).setExpiretTime(new Date(new Date().getTime() + TOKENEXPIRETIME * 1000))
                .setBindPaypwd(isPayPassword).setBindEmail(isEmail).setBindMobile(isMobile).setMnemonic(isMnemonic);
    }

    //退出登录
    public void logout(int type, Integer userId) {
        userMapper.disableUserToken(userId, new Date());
    }

    //设置交易密码
    public void setPayPassword(int userId, String areaCode, String mobile, String code, String payPassword) {
        User user = userMapper.getUserByUserId(userId);
        Assert.check(user == null, ErrorCode.ERR_USER_NOT_EXIST);
        if (mobile.contains("@")) {
            Assert.check(!mobile.equals(user.getEmail()), ErrorCode.ERR_PARAM_ERROR);
        } else {
            Assert.check(!mobile.equals(user.getMobile()), ErrorCode.ERR_PARAM_ERROR);
        }
        Assert.check(!smsService.checkCaptcha(areaCode, mobile, SmsCaptchaType.TRANSACTION_PASSWORD.getValue(), code), ErrorCode.ERR_USER_SMSCODE_ERROR);

        //生成用户密码
        User tmp = new User().setId(userId).setPayPassword(EncryptionUtil.MD5(payPassword));
        userMapper.updateUserByUserId(tmp);
    }

    //忘记密码，重置密码
    @Transactional
    public void resetPassword(String areaCode, String mobile, String code, String password) {
        Assert.check(mobile == null, ErrorCode.ERR_USER_MOBILE_ERROR);
        Assert.check(password == null, ErrorCode.ERR_USER_PASSWORD_ERROR);

        User user = null;
        if (mobile.contains("@")) {
            user = userMapper.getUserByEmail(mobile);
        } else {
            user = userMapper.getUserByMobile(mobile);
        }

        Assert.check(user == null, ErrorCode.ERR_USER_NOT_EXIST);
        Assert.check(!smsService.checkCaptcha(areaCode, mobile, SmsCaptchaType.PASSWORD_FORGET.getValue(), code), ErrorCode.ERR_USER_SMSCODE_ERROR);

        //生成用户密码
        User tmp = new User().setId(user.getId()).setPassword(EncryptionUtil.MD5(password));
        userMapper.updateUserByUserId(tmp);
    }

    //重置交易密码
    @Transactional
    public void resetPayPassword(String areaCode, String mobile, String code, String payPassword) {
        Assert.check(mobile == null, ErrorCode.ERR_USER_MOBILE_EXIST);
        Assert.check(payPassword == null, ErrorCode.ERR_USER_PASSWORD_ERROR);

        User user = null;
        if (mobile.contains("@")) {
            Assert.check(!ToolsUtil.checkEmail(mobile), ErrorCode.ERR_USER_ACCOUNT_ERROR);
            user = userMapper.getUserByEmail(mobile);
        } else {
            user = userMapper.getUserByMobile(mobile);
        }
        Assert.check(user == null, ErrorCode.ERR_USER_NOT_EXIST);
        Assert.check(!smsService.checkCaptcha(areaCode, mobile, SmsCaptchaType.TRANSACTION_FORGET.getValue(), code), ErrorCode.ERR_USER_SMSCODE_ERROR);

        //生成用户密码
        User tmp = new User().setId(user.getId()).setPayPassword(EncryptionUtil.MD5(payPassword));
        userMapper.updateUserByUserId(tmp);
    }

    @Transactional
    public void resetPassword(Integer userId, String newPassword) {
        User user = userMapper.getUserByUserId(userId);
        Assert.check(user == null, ErrorCode.ERR_USER_NOT_EXIST);

        User tmp = new User().setId(user.getId()).setPayPassword(EncryptionUtil.MD5(newPassword));
        userMapper.updateUserByUserId(tmp);
    }

    @Transactional
    public void resetPayPassword(Integer userId, String newPassword) {
        User user = userMapper.getUserByUserId(userId);
        Assert.check(user == null, ErrorCode.ERR_USER_NOT_EXIST);

        User tmp = new User().setId(user.getId()).setPayPassword(EncryptionUtil.MD5(newPassword));
        userMapper.updateUserByUserId(tmp);
    }

    public void modifyPassword(Integer userId, String oldPassword, String newPassword){
        User user = userMapper.getUserByUserId(userId);
        Assert.check(user == null, ErrorCode.ERR_USER_NOT_EXIST);
        Assert.check(!EncryptionUtil.checkMD5(oldPassword, user.getPassword()), ErrorCode.ERR_USER_PASSWORD_ERROR);

        userMapper.updateUserByUserId(new User().setId(user.getId()).setPassword(EncryptionUtil.MD5(newPassword)));
    }

    public void modifyPayPassword(Integer userId, String oldPassword, String newPassword){
        User user = userMapper.getUserByUserId(userId);
        Assert.check(user == null, ErrorCode.ERR_USER_NOT_EXIST);
        Assert.check(!EncryptionUtil.checkMD5(oldPassword, user.getPayPassword()), ErrorCode.ERR_USER_PASSWORD_ERROR);

        userMapper.updateUserByUserId(new User().setId(user.getId()).setPayPassword(EncryptionUtil.MD5(newPassword)));
    }

    public void updateUserMobile(String areaCode, String oldPhone, String code, String newPhone) {
        Assert.check(StringUtils.isBlank(newPhone), ErrorCode.ERR_PARAM_ERROR);
        Assert.check(!smsService.checkCaptcha(areaCode, oldPhone, SmsCaptchaType.MOBILE_EDIT.getValue(), code), ErrorCode.ERR_USER_SMSCODE_ERROR);

        User oldUser = userMapper.getUserByMobile(oldPhone);
        Assert.check(oldUser == null, ErrorCode.ERR_USER_NOT_EXIST);

        User tmp = new User().setId(oldUser.getId()).setMobile(newPhone);
        userMapper.updateUserByUserId(tmp);
    }

    public UserInfoVO getUserInfo(Integer userId) {
        UserInfoVO userInfoVO = new UserInfoVO();
        User user = userMapper.getUserByUserId(userId);
        BeanUtils.copyProperties(user, userInfoVO);

//        List<ConfigLevel> level = configLevelMapper.getConfigList();
//        ConfigLevel levlast = null;
//        for (ConfigLevel lev:level){
//            if (user.getMoneyAll().doubleValue()>=lev.getMinAmount() && user.getMoneyAll().doubleValue()<lev.getMaxAmount()){
//                levlast = lev;
//                break;
//            }
//            levlast=lev;
//        }
//        userInfoVO.setLevel(levlast.getLevel());
        return userInfoVO;
    }

    //获取国家区号配置
    public List<NationalCode> getNationalCode() {
        return nationalCodeMapper.getNationalCode();
    }


    //绑定手机号
    public void bindMobile(int userId, String areaCode, String mobile, String code) {
        Assert.check(userMapper.getUserByMobile(mobile) != null, ErrorCode.ERR_USER_MOBILE_EXIST);

        User user = userMapper.getUserByUserId(userId);
        Assert.check(user == null, ErrorCode.ERR_USER_NOT_EXIST);
        Assert.check(user.getMobile() != null && user.getMobile().length() > 0, ErrorCode.ERR_USER_MOBILE_EXIST);
        Assert.check(!smsService.checkCaptcha(areaCode, mobile, SmsCaptchaType.MOBILE_BIND.getValue(), code), ErrorCode.ERR_USER_SMSCODE_ERROR);

        userMapper.updateUserByUserId(new User().setId(user.getId()).setAreaCode(areaCode).setMobile(mobile));
    }

    //绑定邮件
    public void bindEmail(int userId, String areaCode, String email, String code) {
        Assert.check(userMapper.getUserByEmail(email) != null, ErrorCode.ERR_USER_EMAIL_EXIST);

        User user = userMapper.getUserByUserId(userId);
        Assert.check(user == null, ErrorCode.ERR_USER_NOT_EXIST);
        Assert.check(user.getEmail() != null && user.getEmail().length() > 0, ErrorCode.ERR_USER_EMAIL_EXIST);
        Assert.check(!smsService.checkCaptcha(areaCode, email, SmsCaptchaType.EMAIL_BIND.getValue(), code), ErrorCode.ERR_USER_SMSCODE_ERROR);

        userMapper.updateUserByUserId(new User().setId(user.getId()).setEmail(email));
    }

    //获取最新版本号
    public AppVersion getAppVersion(String platform) {
        return appVersionMapper.getAppVersion(platform);
    }

    public List<AppVersion> getAppVersionList() {
        return appVersionMapper.getAppVersionList();
    }

    @Transactional
    public void changeNickName(int userId, String nickName) {
        if (userMapper.getUserByNickName(nickName)==null) {
            userMapper.updateUserByUserId(new User().setId(userId).setNickName(nickName));
        }else{
            Assert.check(true, ErrorCode.ERR_USER_NICK_NAME_EXIST);
        }
    }

    public Page<UserInfoVO> getUserList(int pageNo, int pageSize, String mobile, String email, String address) {
        Integer userId = null;
        if (StringUtils.isNotBlank(address)){
            EthAddress ethAddress = ethAddressMapper.getEthAddressByAddress(address);
            if (ethAddress != null){
                userId = ethAddress.getUserId();
            }
        }

        Page<UserInfoVO> userList = userMapper.getUserList(mobile, email, userId, new RowBounds(pageNo, pageSize));
        if (userList != null) {
            for (UserInfoVO userVo : userList) {
                userVo.setReferrerCount(userMapper.getUserReferrerCount(userVo.getId()));
            }
        }
        return userList;
    }
    public User getUserDetail(Integer userId) {
        return userMapper.getUserByUserId(userId);
    }

    @Transactional
    public void updateUser(UpdateUserVo userToUpdate) {
        User user = userMapper.getUserByUserId(userToUpdate.getId());
        Assert.check(user == null, ErrorCode.ERR_USER_NOT_EXIST);
        User tmp = new User().setId(userToUpdate.getId());

        if (StringUtils.isNotBlank(userToUpdate.getPassword())) {
            tmp.setPassword(EncryptionUtil.MD5(userToUpdate.getPassword()));
        }
        if (StringUtils.isNotBlank(userToUpdate.getMobile())) {
            User userMobile = userMapper.userExits(userToUpdate.getId(),null,userToUpdate.getMobile(),null);
            Assert.check(userMobile!=null,ErrorCode.ERR_USER_MOBILE_EXIST);
            tmp.setMobile(userToUpdate.getMobile());
            tmp.setUserName(userToUpdate.getMobile());
        }
        if (StringUtils.isNotBlank(userToUpdate.getEmail())) {
            User userEmail = userMapper.userExits(userToUpdate.getId(),null,null,userToUpdate.getEmail());
            Assert.check(userEmail!=null,ErrorCode.ERR_USER_EMAIL_EXIST);
            tmp.setEmail(userToUpdate.getEmail());
            //tmp.setUserName(userToUpdate.getEmail());
        }
        if (StringUtils.isNotBlank(userToUpdate.getImage())){
            tmp.setImage(userToUpdate.getImage());
        }
        BeanUtils.copyProperties(userToUpdate,tmp);
        userMapper.updateUserByUserId(tmp);
    }

    public Page<UserBillVO> getUserBill(int pageNo,int pageSize,Integer userId){
        Page<UserBillVO> userBill = userBillMapper.getUserBill(userId,new RowBounds(pageNo,pageSize));
        return userBill;
    }

    public Page<UserBillVO> getUserBillByReason(Integer userId, String coinName, String[] reason, int pageNo, int pageSize){
        Page<UserBillVO> userBill = userBillMapper.getUserBillByReason(userId, coinName, reason, new RowBounds(pageNo, pageSize));
        return userBill;
    }

    public Page<UserBillVO> getUserBillByReason2(Integer userId, String coinName, String[] reason, String userName,int pageNo, int pageSize){
        Page<UserBillVO> userBill = userBillMapper.getUserBillByReason2(userId, coinName, reason,userName, new RowBounds(pageNo, pageSize));
        return userBill;
    }

    private UserRelationVO getUserRelationVOByUserId(int userId){
        UserRelationVO userRelationVO = new UserRelationVO();
        User user1 = userMapper.getUserByUserId(userId);
        if (user1!=null)
            userRelationVO.setId(user1.getId()).setMobile(user1.getMobile()).setEmail(user1.getEmail()).
                    setUserName(user1.getUserName()).setNickName(user1.getNickName()).setLeftChild(user1.getLeftChild()).setRightChild(user1.getRightChild());
        return userRelationVO;
    }
    public UserRelationVO getUserRelationByUserId(int userId, int count){
        if (count++>=3)
            return null;

        //获取1级
        UserRelationVO userRelationVO = getUserRelationVOByUserId(userId);

        //获取2级
        if (userRelationVO.getLeftChild()!=null){
            UserRelationVO userRelationVO1 = getUserRelationByUserId(userRelationVO.getLeftChild(), count);
            if (userRelationVO1!=null)
                userRelationVO.setLeftChildNode(userRelationVO1);
        }
        if (userRelationVO.getRightChild()!=null){
            UserRelationVO userRelationVO2 = getUserRelationByUserId(userRelationVO.getRightChild(), count);
            if (userRelationVO2!=null)
                userRelationVO.setRightChildNode(userRelationVO2);
        }
        return userRelationVO;
    }

    //获取用户统计数据
    public UserStat getUserStat(Integer userId){
        return userStatMapper.getUserStat(userId);
    }

    @Transactional
    public void updateUserStatus(List<Integer> userIds, Integer status) {
        for (Integer userId : userIds) {
            User tmp = new User().setId(userId);
            tmp.setStatus(status);
            int rows = userMapper.updateUserByUserId(tmp);
            Assert.check(rows == 0, ErrorCode.ERR_USER_NOT_EXIST);
        }
    }
    public void updateUserSellPermitStatu(Integer id,SalesPermit salesPermit){
        User user = getUserByUserId(id);
        Assert.check(user == null ,ErrorCode.ERR_RECORD_NOT_EXIST);
        if(user.getSalesPermit() == salesPermit.getValue()){
            return;
        }
        if(salesPermit.getValue() == 1){//如果请求是开启，则需校验用户是否被禁用
            Assert.check(user.getStatus() == 1 ,ErrorCode.ERR_USER_DISABLE);
            MallShop mallShop = mallShopMapper.getMallShopByUserId(id);
            if(mallShop == null ){
                mallShop = new MallShop().setUserId(id).setShopName(user.getNickName());
                mallShopMapper.addMallShop(mallShop);
            }
            //获取一级推荐人
            User one = userMapper.getUserByUserId(user.getInvite());
            User two = null;
            if(one != null){
                two = userMapper.getUserByUserId(one.getInvite());
            }

            String coinName = configService.getConfigValue("shop.referrer.coin");
            Coin coinByName = coinService.getCoinByName(coinName);
            if(coinByName != null){
                int oneLevel = configService.getConfigValue("shop.referrer.level.one", 0);
                if(oneLevel > 0 && one != null){
                    userCoinService.changeUserCoin(one.getId(),coinByName.getName(),BigDecimal.valueOf(oneLevel),BigDecimal.ZERO,BigDecimal.ZERO,UserBillReason.SHOP_ONE,"商家一级推介奖励");
                }
                int twoLevel = configService.getConfigValue("shop.referrer.level.two", 0);
                if(twoLevel > 0 && two != null){
                    userCoinService.changeUserCoin(two.getId(),coinByName.getName(),BigDecimal.valueOf(twoLevel),BigDecimal.ZERO,BigDecimal.ZERO,UserBillReason.SHOP_TWO,"商家二级推介奖励");
                }
                logger.debug("商家推荐奖励开启，直推奖励"+oneLevel+coinName+"，间接推荐人奖励币种"+twoLevel+coinName);
            }
        }else{//关闭用户销售权限，需要下架该用户的所有商品，取消未发货的订单，修改未退货的的订单未已退货,修改已发货的为已经完成。
            MallApply byUserId = mallApplyMapper.findByUserId(user.getId());
            if(byUserId != null){
                MallApply mallApply = new MallApply().setId(byUserId.getId()).setStatus(1);
                mallApplyMapper.update(mallApply);
            }
            mallGoodService.downGoodsByUserId(user.getId());
            mallOrderService.closeOrderByUserId(user.getId());
        }
        int i = userMapper.updateUserSalesPermit(user.getId(), salesPermit.getValue());
        Assert.check(i != 1,ErrorCode.ERR_RECORD_UPDATE);
    }

    /**
     * 获取我的团队数据
     * @param user
     */
    public GroupVO getGroup(User user) {
        User userDb = userMapper.getUserByUserId(user.getId());
        //直接邀请人
        Integer leftChild = userDb.getLeftChild();
        //间接受益人
        Integer rightChild = userDb.getRightChild();
        //直接受益BSTS
        BigDecimal leftBsts = BigDecimal.ZERO;
        //直接受益ENG11
        BigDecimal leftEng11 = BigDecimal.ZERO;
        //间接受益BSTS
        BigDecimal rightBsts = BigDecimal.ZERO;
        //间接受益ENG11
        BigDecimal rightEng11 = BigDecimal.ZERO;
        String [] arr = {UserBillReason.MALL_ONE,UserBillReason.MALL_TWO,UserBillReason.SHOP_ONE,UserBillReason.SHOP_TWO};
        Page<UserBillVO> userBillByReason = userBillMapper.getUserBillByReason(userDb.getId(), null, arr, null);
        for (UserBillVO reason : userBillByReason) {
            if(UserBillReason.MALL_ONE.equals(reason.getReason()) || UserBillReason.SHOP_ONE.equals(reason.getReason()) ){
                if(Coin.BSTS.equalsIgnoreCase(reason.getCoinName())){
                    leftBsts.add(reason.getChangeAmount());
                }else if(Coin.ENG11.equalsIgnoreCase(reason.getCoinName())){
                    leftEng11.add(reason.getChangeAmount());
                }
            }else if(UserBillReason.MALL_TWO.equals(reason.getReason()) || UserBillReason.SHOP_TWO.equals(reason.getReason())){
                if(Coin.BSTS.equalsIgnoreCase(reason.getCoinName())){
                    rightBsts.add(reason.getChangeAmount());
                }else if(Coin.ENG11.equalsIgnoreCase(reason.getCoinName())){
                    rightEng11.add(reason.getChangeAmount());
                }
            }
        }
        return new GroupVO().setLeftChild(leftChild).setRightChild(rightChild).setLeftBsts(leftBsts).setLeftEng11(leftEng11).setRightBsts(rightBsts).setRightEng11(rightEng11);

    }

    public GroupVO getGlobal(User user) {
        if(user.getBrandPermit() == 1){
            //注册人数
            Integer count = userMapper.countUser();
            //计算成交额
            BigDecimal bsts = BigDecimal.ZERO;
            BigDecimal eng11 = BigDecimal.ZERO;
            String [] arr = {UserBillReason.MALL_SALE};
            Page<UserBillVO> userBillByReason = userBillMapper.getUserBillByReason(user.getId(), null, arr, null);
            for (UserBillVO reason : userBillByReason) {
                if(Coin.BSTS.equalsIgnoreCase(reason.getCoinName())){
                    bsts.add(reason.getChangeAmount());
                }else if(Coin.ENG11.equalsIgnoreCase(reason.getCoinName())){
                    eng11.add(reason.getChangeAmount());
                }
            }
            return new GroupVO().setLeftChild(count).setLeftEng11(eng11).setLeftBsts(bsts);
        }
        return new GroupVO();
    }
}
