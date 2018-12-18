package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.ConfigKey;
import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.constants.PayType;
import com.cmd.wallet.common.constants.UserBillReason;
import com.cmd.wallet.common.enums.ImageType;
import com.cmd.wallet.common.enums.OrderStauts;
import com.cmd.wallet.common.enums.ReputationStauts;
import com.cmd.wallet.common.enums.SmsCaptchaType;
import com.cmd.wallet.common.mapper.*;
import com.cmd.wallet.common.model.*;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.common.utils.EncryptionUtil;
import com.cmd.wallet.common.vo.*;
import com.github.pagehelper.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
public class MallOrderService {
    private static final Logger log = LoggerFactory.getLogger(MallOrderService.class);

    @Autowired
    private MallOrderMapper mallOrderMapper;
    @Autowired
    private MallGoodService mallGoodService;
    @Autowired
    private MallAddressService mallAddressService;
    @Autowired
    private UserCoinService userCoinService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private SmsService smsService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ChangeConfigMapper changeConfigMapper;
    @Autowired
    private MallShopMapper mallShopMapper;
    @Autowired
    private MallCartMapper mallCartMapper;
    @Autowired
    private ConfigService configService;

    private  String smsMsg = "您在矿工之家商城有一笔订单，请及时查看并完成";

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void payOrder(PayOrderVO payOrderVO) {
        //校验用户密码
        User user = userMapper.getUserByUserId(ShiroUtils.getUser().getId());
        Assert.check(StringUtils.isBlank(user.getMobile()), ErrorCode.ERR_USER_MOBILE_ERROR);

        //检查验证码(支付也走转账的短信)
        if (!smsService.checkCaptcha(user.getAreaCode(), user.getMobile(), SmsCaptchaType.TRANSFER_OUT.getValue(), payOrderVO.getSmsCode())) {
            Assert.check(true, ErrorCode.ERR_USER_SMSCODE_ERROR);
        }

        Assert.check(StringUtils.isBlank(user.getPayPassword()), ErrorCode.ERR_USER_PAY_PASSWORD_NOT_FOUND);
        Assert.check(!EncryptionUtil.checkMD5(payOrderVO.getPayPassword(), user.getPayPassword()), ErrorCode.ERR_USER_PASSWORD_ERROR);
        //汇率
        ChangeConfig changeConfig = changeConfigMapper.getChangeConfig(payOrderVO.getCoin(), Coin.CNY);
        Assert.check(changeConfig==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
        BigDecimal totalPrice = BigDecimal.ZERO;
        //用于存储购买的商品
        List<TMallGoodModel> goodModels = new ArrayList<>();
        if(PayType.ONCE.equals(payOrderVO.getType())){
            totalPrice = totalPrice.add(payOnceOrder(payOrderVO,goodModels));
        }else{
            Integer[] cartIds = payOrderVO.getCartIds();
            if(cartIds != null && cartIds.length >= 1){
                for (Integer id :
                        cartIds) {
                    PayOrderVO payOrderVO1 = new PayOrderVO();
                    CartModel cart = mallCartMapper.getMallCartById(id);
                    payOrderVO1.setAddressId(payOrderVO.getAddressId()).setCoin(payOrderVO.getCoin()).setGoodId(cart.getGoodId()).setCount(cart.getNumber());
                    totalPrice = totalPrice.add(payOnceOrder(payOrderVO1,goodModels));
                }
            }
        }
        //冻结用户金额
        userCoinService.changeUserCoin(user.getId(),payOrderVO.getCoin(),totalPrice.negate(),totalPrice ,BigDecimal.ZERO,UserBillReason.MALL_BUY,"商城付款");
        //清空购物车
        if(PayType.CART.equals(payOrderVO.getType())){
            Integer[] cartIds = payOrderVO.getCartIds();
            if(cartIds != null && cartIds.length >= 1){
                for (Integer id :
                        cartIds) {
                    mallCartMapper.delete(id);

                }
            }
        }
        //购买给商家发送短信
        try{
            for (TMallGoodModel mallGoodModel :goodModels){
                User tmp = userMapper.getUserByUserId(mallGoodModel.getUserId());
                if (tmp.getMobile()!=null) {
                    smsService.sendPostSmsNormal(tmp.getAreaCode(), tmp.getMobile(), smsMsg);
                }
            }
        }catch (Exception e){
            log.error(""+e);
        }

    }

    public BigDecimal payOnceOrder(PayOrderVO payOrderVO,List sellerIds){
        TMallOrderModel mallOrderModel = new TMallOrderModel();
        BeanUtils.copyProperties(payOrderVO,mallOrderModel);
        Integer shopCount = mallOrderModel.getCount();
        Assert.check(shopCount <= 0,ErrorCode.ERR_PARAM_ERROR);
        TMallGoodModel goodInfo = mallGoodService.getGoodInfo(mallOrderModel.getGoodId());
        Assert.check(goodInfo.getStock() < shopCount,ErrorCode.ERR_MALL_GOOD_UNDERSTOCK);
        //设置卖家和买家
        mallOrderModel.setSellerId(goodInfo.getUserId());
        mallOrderModel.setUserId(ShiroUtils.getUser().getId());
        //设置金额
        BigDecimal price = goodInfo.getPrice();
        BigDecimal count = new BigDecimal(shopCount);
        BigDecimal totalPrice = count.multiply(price);
        mallOrderModel.setTotalPrice(totalPrice);
        mallOrderModel.setPrice(price);
        mallOrderModel.setCoinName(payOrderVO.getCoin());
        //设置收货地址
        TMallAddressModel address = mallAddressService.getAddress(mallOrderModel.getAddressId());
        mallOrderModel.setReceiverName(address.getReceiverName());
        mallOrderModel.setReceiverMobile(address.getReceiverMobile());
        mallOrderModel.setProvinceId(address.getProvinceId());
        mallOrderModel.setCityId(address.getCityId());
        mallOrderModel.setAreaId(address.getAreaId());
        mallOrderModel.setDetailAddr(address.getDetailAddr());
        //生成订单为待发货
        int id = mallOrderMapper.addMallOrder(mallOrderModel);
        Assert.check(id <= 0,ErrorCode.ERR_MALL_ORDER_ADD);
        //修改库存
        MallGoodVO mallGoodVO = new MallGoodVO();
        mallGoodVO.setId(goodInfo.getId());
        mallGoodVO.setStock(goodInfo.getStock()-shopCount);
        mallGoodService.editGoodInfo(mallGoodVO);
        sellerIds.add(goodInfo);
        return totalPrice;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void confirmOrder(Integer orderId,Integer reputation) {
        TMallOrderModel mallOrder = mallOrderMapper.getMallOrder(orderId);
        //已经完成
        Assert.check(mallOrder.getStatus() == OrderStauts.SUCCESS.getValue(),ErrorCode.ERR_MALL_ORDER_REPEATED_CONFIRMATION);
        //已经取消
        Assert.check(mallOrder.getStatus() == OrderStauts.CANCEL.getValue(),ErrorCode.ERR_MALL_ORDER_CANCELLED);
        //已经退货
        Assert.check(mallOrder.getReturnStatus() == OrderStauts.RETURNED.getValue(),ErrorCode.ERR_MALL_ORDER_RETURNED);
        TMallGoodModel goodInfo = mallGoodService.getGoodInfo(mallOrder.getGoodId());
        BigDecimal totalPrice = mallOrder.getTotalPrice();
        Assert.check(mallOrder.getReturnStatus() != 0,ErrorCode.ERR_MALL_ORDER_RETURN);
        //扣除买家冻结
        userCoinService.changeUserCoin(mallOrder.getUserId(),mallOrder.getCoinName(),BigDecimal.ZERO,BigDecimal.ZERO,totalPrice.negate(),UserBillReason.MALL_BUY,"确认收货扣除冻结");
        //增加卖家余额
        userCoinService.changeUserCoin(mallOrder.getSellerId(),mallOrder.getCoinName(),totalPrice,BigDecimal.ZERO ,BigDecimal.ZERO,UserBillReason.MALL_SALE,"商城卖出收款");
        //修改订单为已完成状态
        int i = mallOrderMapper.updateMallOrderStatus(orderId, OrderStauts.SUCCESS.getValue());
        //更新销量
        mallGoodService.editGoodInfo(new MallGoodVO().setId(mallOrder.getGoodId()).setSalesVolume(goodInfo.getSalesVolume()+mallOrder.getCount()));
        Assert.check(i != 1,ErrorCode.ERR_RECORD_UPDATE);
        //修改评价
        mallOrderMapper.updateMallOrderReputation(orderId,reputation);
        MallShop mallShop = mallShopMapper.getMallShopByUserId(goodInfo.getUserId());
        Assert.check(mallShop == null,ErrorCode.ERR_MALL_GOOD_UNPERMIT);
        if(ReputationStauts.GOOD.getValue() == reputation){
            mallShopMapper.addGoodRept(mallShop.getUserId());
        }else if(ReputationStauts.MIDDLE.getValue() == reputation){
            mallShopMapper.addMiddleRept(mallShop.getUserId());
        }else if(ReputationStauts.BAD.getValue() == reputation){
            mallShopMapper.addBadRept(mallShop.getUserId());
        }
        try{
            User user = userMapper.getUserByUserId(mallOrder.getUserId());
            //获取一级推荐人
            User one = userMapper.getUserByUserId(user.getInvite());
            User two = null;
            if(one != null){
                two = userMapper.getUserByUserId(one.getInvite());
            }

            //返利
            int buyRebate = configService.getConfigValue(ConfigKey.USER_BUY_REBATE, 0);
            //返利总额
            if(buyRebate >= 0){
                //订单价钱*返利比率
                BigDecimal multiply = totalPrice.multiply(new BigDecimal(buyRebate).divide(BigDecimal.valueOf(100)));
                userCoinService.changeUserCoin(mallOrder.getUserId(),mallOrder.getCoinName(),BigDecimal.ZERO,BigDecimal.ZERO,multiply,UserBillReason.MALL_BUY_REWARD,"平台促销返利");
            }

            int oneLevel = configService.getConfigValue(ConfigKey.USER_REFERRER_LEVEL_ONE, 0);
            if(oneLevel > 0 && one != null){
                //订单价钱*返利比率
                BigDecimal multiply = totalPrice.multiply(new BigDecimal(oneLevel).divide(BigDecimal.valueOf(100)));
                userCoinService.changeUserCoin(one.getId(),mallOrder.getCoinName(),multiply,BigDecimal.ZERO,BigDecimal.ZERO,UserBillReason.MALL_ONE,"消费一级推介奖励");
            }

            int twoLevel = configService.getConfigValue(ConfigKey.USER_REFERRER_LEVEL_TWO, 0);
            if(twoLevel > 0 && two != null){
                //订单价钱*返利比率
                BigDecimal multiply = totalPrice.multiply(new BigDecimal(twoLevel).divide(BigDecimal.valueOf(100)));
                userCoinService.changeUserCoin(two.getId(),mallOrder.getCoinName(),multiply,BigDecimal.ZERO,BigDecimal.ZERO,UserBillReason.MALL_TWO,"消费二级推介奖励");
            }
            //确认收货给商家发送短信
            User tmp = userMapper.getUserByUserId(goodInfo.getUserId());
            if (tmp.getMobile()!=null) {
                smsService.sendPostSmsNormal(tmp.getAreaCode(), tmp.getMobile(), smsMsg);
            }
        }catch (Exception e){
            log.error(""+e);
        }
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void sendOrder(Integer orderId, String imgUrl) {
        TMallOrderModel mallOrder = mallOrderMapper.getMallOrder(orderId);
        Assert.check(mallOrder.getStatus() == OrderStauts.SENT.getValue(),ErrorCode.ERR_MALL_ORDER_SENT);
        int i = imageService.addImages(orderId, ImageType.MALL_SEND.getValue(), Collections.singletonList(imgUrl));
        Assert.check(i != 1,ErrorCode.ERR_RECORD_DATA_ERROR);
        int j = mallOrderMapper.updateMallOrderStatus(orderId, OrderStauts.SENT.getValue());
        Assert.check(j != 1,ErrorCode.ERR_RECORD_DATA_ERROR);
        //发货给买家发送短信。
        try{
            User tmp = userMapper.getUserByUserId(mallOrder.getUserId());
            if (tmp.getMobile()!=null) {
                smsService.sendPostSmsNormal(tmp.getAreaCode(), tmp.getMobile(), smsMsg);
            }
        }catch (Exception e){
            log.error(""+e);
        }
    }
    @Transactional
    public void returnOrder(Integer orderId, String imgUrl,String reason) {
        TMallOrderModel mallOrder = mallOrderMapper.getMallOrder(orderId);
        Integer status = mallOrder.getStatus();
        Assert.check(mallOrder.getReturnStatus() == OrderStauts.RETURNING.getValue(),ErrorCode.ERR_MALL_ORDER_RETURNING);
        if(status != OrderStauts.UNSEND.getValue()){//已发货的订单发起退货必须要上传凭证
            Assert.check(StringUtils.isBlank(imgUrl) || StringUtils.isBlank(reason),ErrorCode.ERR_MALL_ORDER_RETURN_SENT);
        }
        if(imgUrl == null){
            imgUrl="";
        }
        int i = imageService.addImages(orderId, ImageType.MALL_RETURN.getValue(), Collections.singletonList(imgUrl));
        Assert.check(i != 1,ErrorCode.ERR_RECORD_DATA_ERROR);
        int j = mallOrderMapper.updateMallOrderReturnStatus(orderId, OrderStauts.RETURNING.getValue(),reason);
        Assert.check(j != 1,ErrorCode.ERR_RECORD_DATA_ERROR);
        //退货给商家发送短信。
        try{
            User tmp = userMapper.getUserByUserId(mallOrder.getSellerId());
            if (tmp.getMobile()!=null) {
                smsService.sendPostSmsNormal(tmp.getAreaCode(), tmp.getMobile(), smsMsg);
            }
        }catch (Exception e){
            log.error(""+e);
        }
    }
    @Transactional()
    public void confirmReturnOrder(Integer orderId) {
        TMallOrderModel mallOrder = mallOrderMapper.getMallOrder(orderId);
        //已经完成
        Assert.check(mallOrder.getStatus() == OrderStauts.SUCCESS.getValue(),ErrorCode.ERR_MALL_ORDER_REPEATED_CONFIRMATION);
        //已经取消
        Assert.check(mallOrder.getStatus() == OrderStauts.CANCEL.getValue(),ErrorCode.ERR_MALL_ORDER_CANCELLED);
        //已经退货
        Assert.check(mallOrder.getReturnStatus() == OrderStauts.RETURNED.getValue(),ErrorCode.ERR_MALL_ORDER_RETURNED);
//        Assert.check(!mallOrder.getSellerId().equals(ShiroUtils.getUser().getId()),ErrorCode.ERR_MALL_ORDER_RETURN);
        //解冻用户金额
        userCoinService.changeUserCoin(mallOrder.getUserId(),mallOrder.getCoinName(),mallOrder.getTotalPrice(),mallOrder.getTotalPrice().negate() ,BigDecimal.ZERO,UserBillReason.MALL_RETURN,"商城退货");
        int j = mallOrderMapper.updateMallOrderReturnStatus(orderId, OrderStauts.RETURNED.getValue(),mallOrder.getReturnReason());
        Assert.check(j != 1,ErrorCode.ERR_RECORD_DATA_ERROR);
        editCancelOrderStock(mallOrder);
        //确认退货给买家发送短信。
        try{
            User tmp = userMapper.getUserByUserId(mallOrder.getUserId());
            if (tmp.getMobile()!=null) {
                smsService.sendPostSmsNormal(tmp.getAreaCode(), tmp.getMobile(), smsMsg);
            }
        }catch (Exception e){
            log.error(""+e);
        }
    }

    public Page<MallOrderListVO> getMyOrdersByStatus(Integer pageNo, Integer pageSize, Integer userId , Integer status) {
        Page<MallOrderListVO> mallOrderListVOPage;
        if(status != null && OrderStauts.NORMAL.getValue() == status){
            mallOrderListVOPage = mallOrderMapper.getReturnOrdersByUserId(userId,new RowBounds(pageNo,pageSize));
        }else{
            mallOrderListVOPage = mallOrderMapper.getOrdersByUserIdAndStatus(userId,status,new RowBounds(pageNo,pageSize));
        }
        setMallOrderListVOSCny(mallOrderListVOPage);
        return mallOrderListVOPage;
    }

    public Page<MallOrderListVO> getSellerOrdersByStatus(Integer pageNo, Integer pageSize,Integer userId ,Integer status) {
        Page<MallOrderListVO> mallOrderListVOPage;
        if(status != null && OrderStauts.NORMAL.getValue() == status){
            mallOrderListVOPage = mallOrderMapper.getReturnSellerOrdersByUserId(userId,new RowBounds(pageNo,pageSize));
        }else{
            mallOrderListVOPage = mallOrderMapper.getSellerOrdersByUserIdAndStatus(userId,status,new RowBounds(pageNo,pageSize));
        }
        setMallOrderListVOSCny(mallOrderListVOPage);
        return mallOrderListVOPage;
    }
    public MallOrderVO getOrderVOById(Integer id){
        MallOrderVO orderVOById = mallOrderMapper.getOrderVOById(id);
        Integer returnStatus = orderVOById.getReturnStatus();
        Integer status = orderVOById.getStatus();
        if(OrderStauts.UNSEND.getValue() != status){
            List<TImageModel> sendImgs = imageService.getImgByRefIdAndType(id, ImageType.MALL_SEND.getValue());
            if(sendImgs != null && !sendImgs.isEmpty()){
                orderVOById.setSendImg(sendImgs.get(0).getImgUrl());
            }
        }
        if(returnStatus != OrderStauts.NORMAL.getValue()){
            List<TImageModel> returnImgs = imageService.getImgByRefIdAndType(id, ImageType.MALL_RETURN.getValue());
            if(returnImgs != null && !returnImgs.isEmpty()){
                orderVOById.setReturnImg(returnImgs.get(0).getImgUrl());
            }
            if(returnStatus == OrderStauts.RETURNING.getValue()){
                orderVOById.setStatus(OrderStauts.RETURNING_VO.getValue());
            }else if(returnStatus == OrderStauts.RETURNED.getValue()){
                orderVOById.setStatus(OrderStauts.RETURNED_VO.getValue());
            }
        }
        ChangeConfig changeConfig = changeConfigMapper.getChangeConfig(orderVOById.getCoinName(), Coin.CNY);
        Assert.check(changeConfig == null,ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
        BigDecimal rate = changeConfig.getRate();
        BigDecimal cny = rate.multiply(orderVOById.getTotalPrice());
        orderVOById.setCny(cny);
        return orderVOById;
    }
    public void setMallOrderListVOSCny(Page<MallOrderListVO> mallOrderListVOS){
        ChangeConfig changeConfig = null;
        String lastCoin = "";
        for (int i = 0; i < mallOrderListVOS.size(); i++) {
            MallOrderListVO mallOrderListVO = mallOrderListVOS.get(i);
            if(!lastCoin.equals(mallOrderListVO.getCoinName())){
                changeConfig = changeConfigMapper.getChangeConfig(mallOrderListVO.getCoinName(), Coin.CNY);
                Assert.check(changeConfig == null,ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
                lastCoin = mallOrderListVO.getCoinName();
            }
            BigDecimal rate = changeConfig.getRate();
            BigDecimal cny = rate.multiply(mallOrderListVO.getTotalPrice());
            mallOrderListVO.setCny(cny);
        }
    }

    public void closeOrderByUserId(Integer id) {
        //待发货订单直接取消,并退还用户冻结金额
        List<MallOrderListVO> unsendOrder = mallOrderMapper.getAllSellerOrdersByUserIdAndStatus(id, OrderStauts.UNSEND.getValue());
        unsendOrder.forEach(order-> {
            try{
                cancelUnsendOrder(order.getId());
            }catch (Exception e){
                log.error(""+e);
            }
        });
        //待确认退货直接退货,并退还用户冻结金额
        List<TMallOrderModel> returnOrders = mallOrderMapper.getReturnSellerOrders(id, OrderStauts.RETURNING.getValue());
        returnOrders.forEach(order-> {
            try{
                confirmReturnOrder(order.getId());
            }catch (Exception e){
                log.error(""+e);
            }
        });
        //已经发货直接置为已完成
        List<MallOrderListVO> sentOrder = mallOrderMapper.getAllSellerOrdersByUserIdAndStatus(id, OrderStauts.SENT.getValue());
        sentOrder.forEach(order ->{
            try{
                confirmOrder(order.getId(),ReputationStauts.GOOD.getValue());
            }catch (Exception e){
                log.error(""+e);
            }
        });
    }
    @Transactional
    public void cancelUnsendOrder(Integer orderId){
        TMallOrderModel mallOrder = mallOrderMapper.getMallOrder(orderId);
        //已经完成
        Assert.check(mallOrder.getStatus() == OrderStauts.SUCCESS.getValue(),ErrorCode.ERR_MALL_ORDER_REPEATED_CONFIRMATION);
        //已经取消
        Assert.check(mallOrder.getStatus() == OrderStauts.CANCEL.getValue(),ErrorCode.ERR_MALL_ORDER_CANCELLED);
        //已经退货
        Assert.check(mallOrder.getReturnStatus() == OrderStauts.RETURNED.getValue(),ErrorCode.ERR_MALL_ORDER_RETURNED);
//        Assert.check(!mallOrder.getSellerId().equals(ShiroUtils.getUser().getId()),ErrorCode.ERR_MALL_ORDER_RETURN);
        //解冻用户金额
        userCoinService.changeUserCoin(mallOrder.getUserId(),mallOrder.getCoinName(),mallOrder.getTotalPrice(),mallOrder.getTotalPrice().negate() ,BigDecimal.ZERO,UserBillReason.MALL_RETURN,"商城后台取消订单");
        int i = mallOrderMapper.updateMallOrderStatus(orderId, OrderStauts.CANCEL.getValue());
        Assert.check(i != 1,ErrorCode.ERR_RECORD_DATA_ERROR);
        editCancelOrderStock(mallOrder);
    }

    /**
     * 修改取消订单，已退货订单的库存
     * @param mallOrder
     */
    public void editCancelOrderStock(TMallOrderModel mallOrder){
        TMallGoodModel goodInfo = mallGoodService.getGoodInfo(mallOrder.getGoodId());
        mallGoodService.editGoodInfo(new MallGoodVO().setId(goodInfo.getId()).setStock(goodInfo.getStock()+mallOrder.getCount()));
    }

    public Page<MallOrderListAdminVO> getOrdersByAdmin(String buyerId, String sellerId, Integer status, String goodName, Integer pageNo, Integer pageSize) {
        Integer returnStatus = 0;
        if(status == null){
            returnStatus = null;
        }else{
            if(status == OrderStauts.RETURNING_VO.getValue()){
                returnStatus = OrderStauts.RETURNING.getValue();
            }else if(status == OrderStauts.RETURNED_VO.getValue()){
                returnStatus = OrderStauts.RETURNED.getValue();
            }
        }
        Page<MallOrderListAdminVO> mallOrderListAdminVOS = mallOrderMapper.getOrdersByAdmin(buyerId,sellerId,status,returnStatus,goodName,new RowBounds(pageNo,pageSize));
        mallOrderListAdminVOS.forEach(mallOrderListAdminVO -> {
            if(mallOrderListAdminVO.getReturnStatus() != OrderStauts.NORMAL.getValue()){
                if(mallOrderListAdminVO.getReturnStatus() == OrderStauts.RETURNING.getValue()){
                    mallOrderListAdminVO.setStatus(OrderStauts.RETURNING_VO.getValue());
                }else if(mallOrderListAdminVO.getReturnStatus() == OrderStauts.RETURNED.getValue()){
                    mallOrderListAdminVO.setStatus(OrderStauts.RETURNED_VO.getValue());
                }
            }
        });
        return mallOrderListAdminVOS;
    }

    public MallOrderListAdminVO getOrderByAdmin(Integer id) {
        MallOrderListAdminVO orderByAdmin = mallOrderMapper.getOrderByAdmin(id);
        Assert.check(orderByAdmin == null,ErrorCode.ERR_RECORD_NOT_EXIST);
        Integer returnStatus = orderByAdmin.getReturnStatus();
        Integer status = orderByAdmin.getStatus();
        if(OrderStauts.UNSEND.getValue() != status){
            List<TImageModel> sendImgs = imageService.getImgByRefIdAndType(id, ImageType.MALL_SEND.getValue());
            if(sendImgs != null && !sendImgs.isEmpty()){
                orderByAdmin.setSendImg(sendImgs.get(0).getImgUrl());
                orderByAdmin.setSendTime(sendImgs.get(0).getCreateTime());
            }
        }
        if(returnStatus != OrderStauts.NORMAL.getValue()){//
            List<TImageModel> returnImgs = imageService.getImgByRefIdAndType(id, ImageType.MALL_RETURN.getValue());
            if(returnImgs != null && !returnImgs.isEmpty()){
                orderByAdmin.setReturnImg(returnImgs.get(0).getImgUrl());
                orderByAdmin.setReturnTime(returnImgs.get(0).getCreateTime());
            }
            if(orderByAdmin.getReturnStatus() == OrderStauts.RETURNING.getValue()){
                orderByAdmin.setStatus(OrderStauts.RETURNING_VO.getValue());
            }else if(orderByAdmin.getReturnStatus() == OrderStauts.RETURNED.getValue()){
                orderByAdmin.setStatus(OrderStauts.RETURNED_VO.getValue());
            }
        }
        return orderByAdmin;
    }

}
