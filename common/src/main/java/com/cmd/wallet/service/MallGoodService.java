package com.cmd.wallet.service;

import com.cmd.wallet.common.constants.ConfigKey;
import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.enums.GoodStauts;
import com.cmd.wallet.common.enums.ImageType;
import com.cmd.wallet.common.mapper.ChangeConfigMapper;
import com.cmd.wallet.common.mapper.MallGoodMapper;
import com.cmd.wallet.common.mapper.MallOrderMapper;
import com.cmd.wallet.common.mapper.SearchHistoryMapper;
import com.cmd.wallet.common.model.*;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.common.vo.MallGoodListVO;
import com.cmd.wallet.common.vo.MallGoodVO;
import com.github.pagehelper.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class MallGoodService {
    @Autowired
    private MallGoodMapper mallGoodMapper;
    @Autowired
    private CoinService coinService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private MallOrderMapper mallOrderMapper;
    @Autowired
    ChangeConfigMapper changeConfigMapper;

    @Autowired
    SearchHistoryMapper searchHistoryMapper;
    public static final Integer IMG_SLIDESHOW = 0;
    public static final Integer DELETE = 1;
    public static final Integer UP = 2;
    public static final Integer UNDELETE = 3;


    /**
     * 新增商品信息商品
     * @param mallGoodVO
     */
    public void publishGood(MallGoodVO mallGoodVO){
        //至少上传一张图片
        mallGoodVO.setPrice(mallGoodVO.getCny());
        mallGoodVO.setCoinName(Coin.CNY);  //固定
        int imgCount = mallGoodVO.getImages() != null  ? mallGoodVO.getImages().size() : 0;
        Assert.check(imgCount == 0,ErrorCode.ERR_MALL_GOOD_IMG_NO_ZERO);
        Assert.check(imgCount > 4,ErrorCode.ERR_MALL_GOOD_IMG_MAX);
        Integer id = ShiroUtils.getUser().getId();
        if(StringUtils.isNotBlank(mallGoodVO.getCoinName())){
            Coin coin = coinService.getCoinByName(mallGoodVO.getCoinName());
            Assert.check(coin == null ,ErrorCode.ERR_COIN_NOT_EXIST);
        }

        //上架商品的数量检查
        Integer goodMax = configService.getMallGoodMax();
        int goodCount = mallGoodMapper.getMallGoodCountByUserId(id);
        Assert.check(goodCount>=goodMax.intValue(), ErrorCode.ERR_MALL_GOOD_MAX);

        //创建一个商品，计价币种默认为ENG11
        TMallGoodModel mallGoodModel = buildTMallGoodModel(id, mallGoodVO);
        int i1 = mallGoodMapper.addMallGood(mallGoodModel);
        Assert.check(i1 <= 0,ErrorCode.ERR_RECORD_UPDATE);
        int i = imageService.addImages(mallGoodModel.getId(),ImageType.SLIDE_SHOW.getValue(), mallGoodVO.getImages());
        Assert.check(i != imgCount,ErrorCode.ERR_RECORD_DATA_ERROR);
    }
    /**
     * 构建一个商品TMallGoodModel
     * @param userId
     * @param mallGoodVO
     * @return
     */
    private TMallGoodModel buildTMallGoodModel(Integer userId,MallGoodVO mallGoodVO) {
        TMallGoodModel mallGoodModel = new TMallGoodModel();
        BeanUtils.copyProperties(mallGoodVO,mallGoodModel);
        mallGoodModel.setUserId(userId);
        return mallGoodModel;
    }


    /**
     * 编辑用商品的详情和图片
     * @param mallGoodVO
     */
    public void editGood(MallGoodVO mallGoodVO) {
        mallGoodVO.setPrice(mallGoodVO.getCny());
        mallGoodVO.setCoinName(Coin.ENG11);
        Assert.check(mallGoodVO.getId() == null || mallGoodVO.getId() <= 0,ErrorCode.ERR_PARAM_ERROR);
        TMallGoodModel mallGoodModel = buildTMallGoodModel(null, mallGoodVO);
        //更新商品实体
        mallGoodMapper.updateMallGoodById(mallGoodModel);
        imageService.updateImages(mallGoodModel.getId(),IMG_SLIDESHOW,mallGoodVO.getImages());
    }
    /**
     * 编辑用商品的详情
     * @param mallGoodVO
     */
    public void editGoodInfo(MallGoodVO mallGoodVO) {
        Assert.check(mallGoodVO.getId() == null || mallGoodVO.getId() <= 0,ErrorCode.ERR_PARAM_ERROR);
        TMallGoodModel mallGoodModel = buildTMallGoodModel(null, mallGoodVO);
        //更新商品实体
        mallGoodMapper.updateMallGoodById(mallGoodModel);
    }

    public Page<MallGoodListVO> getGoods(Integer pageNo, Integer pageSize, String goodName,Integer categoryId,String priceOrderStr,String saleNumStr) {
        Page<MallGoodListVO> goods = mallGoodMapper.getOnSaleGoodsLikeName(goodName,categoryId,priceOrderStr,saleNumStr,new RowBounds(pageNo, pageSize));
        setMallGoodListVOSCny(goods);
        //记录搜索历史
        if(StringUtils.isNotBlank(goodName)){
            Integer id = ShiroUtils.getUser().getId();
            //搜索历史最大值
            int configValue = configService.getConfigValue(ConfigKey.MALL_SEARCH_MAX, 10);
            List<TSearchHistoryModel> tSearchHistoryModels = searchHistoryMapper.queryList(id);
            //过滤出当前相同的搜索条目
            List<TSearchHistoryModel> filter = tSearchHistoryModels.stream().filter(tSearchHistoryModel -> tSearchHistoryModel.getKeyword().equals(goodName)).collect(Collectors.toList());
            if(filter != null && filter.size() >= 1){
                TSearchHistoryModel tSearchHistoryModel = filter.get(0);
                tSearchHistoryModel.setAddTime(new Date());
                searchHistoryMapper.update(tSearchHistoryModel);
            }else{
                if(tSearchHistoryModels.size() >= configValue){//删除时间最久一条记录
                    searchHistoryMapper.delete(tSearchHistoryModels.get(configValue-1).getId());
                }
                TSearchHistoryModel tSearchHistoryModel = new TSearchHistoryModel();
                tSearchHistoryModel.setKeyword(goodName).setAddTime(new Date()).setUserId(id);
                searchHistoryMapper.save(tSearchHistoryModel);
            }
        }
        return goods;
    }

    public Page<TMallGoodModel> getGoodsList(String userName,String goodName,Integer status, Integer isDelete,Integer pageNo, Integer pageSize){
        return mallGoodMapper.getGoodsList(userName, goodName,status,isDelete, new RowBounds(pageNo, pageSize));
    }

    public TMallGoodModel getGoodInfo(Integer id) {
        TMallGoodModel goodInfo = mallGoodMapper.getGoodById(id);
        return goodInfo;
    }
    public MallGoodVO getGoodInfoAndImg(Integer id){
        TMallGoodModel goodInfo = getGoodInfo(id);
        Assert.check(goodInfo == null,ErrorCode.ERR_MALL_GOOD_NO_EXSIT);
        List<TImageModel> images = imageService.getImgByRefIdAndType(id,MallGoodService.IMG_SLIDESHOW);
        List<String> imgUrl = images.stream().map(TImageModel::getImgUrl).collect(Collectors.toList());
        MallGoodVO mallGoodVO = new MallGoodVO().setImages(imgUrl);
        BeanUtils.copyProperties(goodInfo,mallGoodVO);

        ChangeConfig eng11Config = changeConfigMapper.getChangeConfig(Coin.ENG11, Coin.CNY);
        ChangeConfig bstsConfig = changeConfigMapper.getChangeConfig(Coin.BSTS, Coin.CNY);

        Assert.check(eng11Config == null,ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
        Assert.check(bstsConfig == null,ErrorCode.ERR_DB_CONFIG_NOT_EXIST);

        BigDecimal eng11 = mallGoodVO.getCny().divide(eng11Config.getRate(),8,RoundingMode.HALF_UP);
        mallGoodVO.setPrice(eng11);
        mallGoodVO.setEng11(eng11);
        BigDecimal bsts = mallGoodVO.getCny().divide(bstsConfig.getRate(),8,RoundingMode.HALF_UP);
        mallGoodVO.setBsts(bsts);
        //BigDecimal cny = rate.multiply(mallGoodVO.getPrice());
        //mallGoodVO.setCny(cny);
        return mallGoodVO;
    }
    public Page<MallGoodListVO> getUserGoods(Integer pageNo, Integer pageSize, Integer userId, Integer status) {
        Page<MallGoodListVO> mallGoodListVOS= mallGoodMapper.getGoodsByUserIdAndStatus(userId, status, new RowBounds(pageNo, pageSize));
        setMallGoodListVOSCny(mallGoodListVOS);
        return mallGoodListVOS;
    }

    public void editGoodStock(Integer goodId, Integer stock) {
        TMallGoodModel goodInfo = new TMallGoodModel().setId(goodId).setStock(stock);
        int i = mallGoodMapper.updateMallGoodById(goodInfo);
        Assert.check(i != 1,ErrorCode.ERR_RECORD_UPDATE);
    }

    public void editGoodStatus(Integer goodId, Integer type) {
        TMallGoodModel goodInfo = new TMallGoodModel().setId(goodId);
        TMallGoodModel good = mallGoodMapper.getGoodById(goodId);
        //  如果下架商品
        if(type == GoodStauts.DOWN.getValue()){
            Assert.check(mallOrderMapper.getUnfinishedCount(goodId) != 0,ErrorCode.ERR_MALL_ORDER_UNFINISHED );
            goodInfo.setStatus(GoodStauts.DOWN.getValue());
        }if(type == DELETE){
            Assert.check(mallOrderMapper.getUnfinishedCount(goodId) != 0,ErrorCode.ERR_MALL_ORDER_UNFINISHED );
            goodInfo.setIsDelete(1);
            goodInfo.setStatus(GoodStauts.DOWN.getValue());
        }else if(type == UP){
            //库存小于等于0，不可上架
            Assert.check(getGoodInfo(goodId).getStock() <= 0,ErrorCode.ERR_MALL_GOOD_UNDERSTOCK);
            //上架商品的数量检查
            Integer goodMax = configService.getMallGoodMax();
            int goodCount = mallGoodMapper.getMallGoodCountByUserId(good.getUserId());
            Assert.check(goodCount>=goodMax.intValue(), ErrorCode.ERR_MALL_GOOD_MAX);
            goodInfo.setIsDelete(0);
            goodInfo.setStatus(GoodStauts.UP.getValue());
        }else if(type == UNDELETE){
            goodInfo.setIsDelete(0);
        }
        int i = mallGoodMapper.updateMallGoodById(goodInfo);
        Assert.check(i != 1,ErrorCode.ERR_RECORD_UPDATE);
    }
    public void setMallGoodListVOSCny(Page<MallGoodListVO> mallGoodListVOS){
        ChangeConfig eng11Config = changeConfigMapper.getChangeConfig(Coin.ENG11, Coin.CNY);
        ChangeConfig bstsConfig = changeConfigMapper.getChangeConfig(Coin.BSTS, Coin.CNY);

        Assert.check(eng11Config == null,ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
        Assert.check(bstsConfig == null,ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
       // BigDecimal
        for (int i = 0; i < mallGoodListVOS.size(); i++) {
            MallGoodListVO mallGoodListVO = mallGoodListVOS.get(i);

            BigDecimal price = mallGoodListVO.getCny().divide(eng11Config.getRate(), 8, RoundingMode.HALF_UP);
            mallGoodListVO.setPrice(price);
            BigDecimal eng11 = mallGoodListVO.getCny().divide(eng11Config.getRate(),8,RoundingMode.HALF_UP);
            mallGoodListVO.setPrice(eng11);
            mallGoodListVO.setEng11(eng11);
            BigDecimal bsts = mallGoodListVO.getCny().divide(bstsConfig.getRate(),8,RoundingMode.HALF_UP);
            mallGoodListVO.setBsts(bsts);
          //  BigDecimal cny = rate.multiply(mallGoodListVO.getPrice());
          //  mallGoodListVO.setCny(cny);
        }
    }

    public void downGoodsByUserId(Integer id) {
        mallGoodMapper.changeGoodsStatusByUserId(id,GoodStauts.DOWN.getValue());
    }
    public void upGoodsByUserId(Integer id) {
        mallGoodMapper.changeGoodsStatusByUserId(id,GoodStauts.UP.getValue());
    }


    public int getMallGoodStack(Integer goodId){
        TMallGoodModel tMallGoodModel = mallGoodMapper.getGoodById(goodId);
        if(tMallGoodModel!=null){
            return tMallGoodModel.getStock();
        }
        return 0;
    }

}
