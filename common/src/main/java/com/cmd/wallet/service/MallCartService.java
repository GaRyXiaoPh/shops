package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.enums.GoodStauts;
import com.cmd.wallet.common.mapper.ChangeConfigMapper;
import com.cmd.wallet.common.mapper.MallCartMapper;
import com.cmd.wallet.common.mapper.MallGoodMapper;
import com.cmd.wallet.common.mapper.MallShopMapper;
import com.cmd.wallet.common.model.*;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.common.vo.MallGoodVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2018-10-25
 */
@Service
public class MallCartService {

    @Autowired
    private MallCartMapper mallCartMapper;
    @Autowired
    private MallGoodService mallGoodService;
    @Autowired
    private MallShopMapper mallShopMapper;
    @Autowired
    ChangeConfigMapper changeConfigMapper;
    public List<CartModel> getMallCartByUserId(Integer userId,Integer[] cartIds){
        List<CartModel> mallCartByUserId = new ArrayList<>();
       // List<CartModel> mallCartByUserId = new ArrayList<>();
        List<CartModel> mallCartByUserIds = mallCartMapper.getMallCartByUserId(userId);
        if(cartIds ==null||cartIds.length==0){
            mallCartByUserId = mallCartByUserIds;
        }else{
            for(int i:cartIds) {
                for (CartModel model : mallCartByUserIds) {
                    if(model.getId()==i){
                        mallCartByUserId.add(model);
                    }
                }
            }
        }

        ChangeConfig eng11Config = changeConfigMapper.getChangeConfig(Coin.ENG11, Coin.CNY);
        ChangeConfig bstsConfig = changeConfigMapper.getChangeConfig(Coin.BSTS, Coin.CNY);

        Assert.check(eng11Config == null,ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
        Assert.check(bstsConfig == null,ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
        TreeMap<Integer,List<CartGoodModel>> map = new TreeMap();
        mallCartByUserId.forEach(cartModel -> {
            List<CartGoodModel> orDefault = map.getOrDefault(cartModel.getShopId(), new ArrayList<>());
            BigDecimal eng11 = cartModel.getCny().divide(eng11Config.getRate(),8,RoundingMode.HALF_UP);
            cartModel.setEng11(eng11);
            BigDecimal bsts = cartModel.getCny().divide(bstsConfig.getRate(),8,RoundingMode.HALF_UP);
            cartModel.setBsts(bsts);
            CartGoodModel cartGoodModel = new CartGoodModel();
            BeanUtils.copyProperties(cartModel,cartGoodModel);
            int stack = mallGoodService.getMallGoodStack(cartGoodModel.getGoodId());
            cartGoodModel.setStock(stack);
            orDefault.add(cartGoodModel);
            map.put(cartModel.getShopId(), orDefault);
        });
        List<CartModel> cartModel = new ArrayList<>();
        map.forEach((key,value) ->{
            CartModel cartModel1 = new CartModel();
            cartModel1.setShopId(key);
            cartModel1.setShopName(value.get(0).getShopName());

            cartModel1.setCartGoodModelList(value);

            cartModel.add(cartModel1);
        });
        return cartModel;
    }
    @Transactional
    public void addCart(Integer goodId,Integer number){
        Integer id = ShiroUtils.getUser().getId();
        CartModel cartModel = mallCartMapper.getMallCartByUserIdAndGoodId(id,goodId);
        MallGoodVO good = mallGoodService.getGoodInfoAndImg(goodId);
        Assert.check(good == null || good.getIsDelete() == 1 || good.getStatus() == GoodStauts.DOWN.getValue(),ErrorCode.ERR_MALL_GOOD_DOWN);
        Assert.check(good.getStock() < number,ErrorCode.ERR_MALL_GOOD_UNDERSTOCK);
        MallShop shop = mallShopMapper.getMallShopByUserId(good.getUserId());
        if(cartModel == null){
            cartModel = new CartModel();
            List<String> images = good.getImages();
            if(images != null && !images.isEmpty()){
                cartModel.setListPicUrl(images.get(0));
            }
            cartModel.setGoodId(goodId).setAddTime(new Date()).setCny(good.getCny()).setGoodName(good.getName()).setNumber(number)
                    .setShopId(shop.getId()).setShopName(shop.getShopName()).setUserId(id);
            mallCartMapper.save(cartModel);
        }else{//之前就在购物车
            cartModel.setNumber(cartModel.getNumber()+number);
            mallCartMapper.update(cartModel);
        }
    }

    @Transactional
    public int cutCart(Integer goodId,Integer number){
        Integer id = ShiroUtils.getUser().getId();
        CartModel cartModel = mallCartMapper.getMallCartByUserIdAndGoodId(id,goodId);
        int cart_num = 0;
        if (null != cartModel) {
            if (cartModel.getNumber() > number) {
                cartModel.setNumber(cartModel.getNumber() - number);
                mallCartMapper.update(cartModel);
                cart_num = cartModel.getNumber();
            } else if (cartModel.getNumber() == 1) {
                mallCartMapper.delete(cartModel.getId());
                cart_num = 0;
            }
        }
        return cart_num;
    }


    public int delCartGood(Integer goodId,Integer userId){
        CartModel cartModel = mallCartMapper.getMallCartByUserIdAndGoodId(userId,goodId);
        Assert.check(cartModel==null,ErrorCode.ERR_RECORD_NOT_EXIST);
        int i = mallCartMapper.delete(cartModel.getId());
        return  i;
    }

    public int update(CartModel cartModel){
        return mallCartMapper.update(cartModel);
    }
}
