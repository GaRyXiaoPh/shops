package com.cmd.wallet.api.controller;

import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.response.CommonListResponse;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.common.vo.MallGoodListVO;
import com.cmd.wallet.common.vo.MallGoodVO;
import com.cmd.wallet.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 商品相关接口
 */
@Api(tags = "商品模块")
@RestController
@RequestMapping("/good")
public class GoodController {
    @Autowired
    private MallGoodService mallGoodService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private final static String SUBMIT = "submit_";

    @ApiOperation("上架商品")
    @PostMapping("/add")
    public CommonResponse postGood(@RequestBody MallGoodVO mallGoodVO){
        //校验重复提交
        preventResubmission("/good/add",ShiroUtils.getUser().getId());
        Integer salesPermit = ShiroUtils.getUser().getSalesPermit();
        Assert.check(salesPermit == 0,ErrorCode.ERR_MALL_GOOD_UNPERMIT);
        Assert.check(mallGoodVO.getStock() <= 0,ErrorCode.ERR_MALL_GOOD_UNDERSTOCK);
        mallGoodService.publishGood(mallGoodVO);
        return new CommonResponse();
    }
    @ApiOperation("编辑商品")
    @PostMapping("/edit")
    public CommonResponse putGood(@RequestBody MallGoodVO mallGoodVO){
        Integer salesPermit = ShiroUtils.getUser().getSalesPermit();
        Assert.check(salesPermit == 0,ErrorCode.ERR_MALL_GOOD_UNPERMIT);
        Assert.check(mallGoodVO.getStock() <= 0,ErrorCode.ERR_MALL_GOOD_UNDERSTOCK);
        mallGoodService.editGood(mallGoodVO);
        return new CommonResponse();
    }

    @ApiOperation("获取商品详情")
    @GetMapping("/info")
    public CommonResponse<MallGoodVO> getGood(@RequestParam("id") Integer id){
        MallGoodVO mallGoodVO = mallGoodService.getGoodInfoAndImg(id);
        //商品删除不输出
        Assert.check(MallGoodService.DELETE == mallGoodVO.getIsDelete(),ErrorCode.ERR_MALL_GOOD_DELETE);
        return new CommonResponse(mallGoodVO);
    }
    @ApiImplicitParams({@ApiImplicitParam(name="goodId",value="商品ID"),@ApiImplicitParam(name="type",value="下架类型，0：下架，1：下架并删除,2:上架")})
    @ApiOperation("上下架商品")
    @PostMapping("/edit-good-status")
    public CommonResponse editGood(@RequestParam("goodId") Integer goodId, @RequestParam("type") Integer type){
        //校验重复提交
        preventResubmission("/good/edit-good-status",ShiroUtils.getUser().getId());
        Integer salesPermit = ShiroUtils.getUser().getSalesPermit();
        Assert.check(salesPermit == 0,ErrorCode.ERR_MALL_GOOD_UNPERMIT);
        mallGoodService.editGoodStatus(goodId,type);
        return new CommonResponse();
    }

    @ApiImplicitParams({@ApiImplicitParam(name="pageNo",value="页数",defaultValue = "1"),@ApiImplicitParam(name="pageSize",value="一页的条数",defaultValue = "10"),@ApiImplicitParam(name="goodName",value="商品名称"),@ApiImplicitParam(name="categoryId",value="分类ID")})
    @ApiOperation("获取商品列表")
    @GetMapping("/goods")
    public CommonListResponse<MallGoodListVO> getGoods(@RequestParam(value = "pageNo",defaultValue = "1")Integer pageNo,
                                                       @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                                       @RequestParam(value = "goodName",required = false)String goodName,
                                                       @RequestParam(value = "priceOrderStr :pricAsc：价格小到大，priceDesc 价格大到小排列,默认为 null ",required = false)String priceOrderStr,
                                                       @RequestParam(value = "saleNumStr: saleNumAsc：销量小到大，saleNumDesc 销量大到小排列",required = false)String saleNumStr,
                                                       @RequestParam(value = "categoryId",required = false)Integer categoryId){
        if(priceOrderStr!=null && !priceOrderStr.equals("")){
            if(priceOrderStr.equals("pricAsc")){
                priceOrderStr = "asc";
            }else if(priceOrderStr.equals("priceDesc")){
                priceOrderStr = "desc";
            }else{
                priceOrderStr = null;
            }
        }
        if(saleNumStr!=null && !saleNumStr.equals("")){
            if(saleNumStr.equals("saleNumAsc")){
                saleNumStr = "asc";
            }else if(saleNumStr.equals("saleNumDesc")){
                saleNumStr = "desc";
            }else{
                saleNumStr = null;
            }
        }

        return new CommonListResponse<>().fromPage(mallGoodService.getGoods(pageNo,pageSize,goodName,categoryId,priceOrderStr,saleNumStr));
    }

    @ApiImplicitParams({@ApiImplicitParam(name="pageNo",value="页数",defaultValue = "1"),@ApiImplicitParam(name="pageNo",value="一页的条数",defaultValue = "10"),@ApiImplicitParam(name="status",value="状态：0:下架；1:在售")})
    @ApiOperation("获取我的商品列表")
    @GetMapping("/my")
    public CommonListResponse<MallGoodListVO> getGoodsMy(@RequestParam(value = "pageNo",defaultValue = "1")Integer pageNo,
                                                         @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                                         @RequestParam(value = "status",defaultValue = "1")Integer status ){
        Integer userId = ShiroUtils.getUser().getId();
        return new CommonListResponse<>().fromPage(mallGoodService.getUserGoods(pageNo,pageSize,userId,status));
    }
    @ApiImplicitParams({@ApiImplicitParam(name="pageNo",value="页数",defaultValue = "1"),@ApiImplicitParam(name="pageNo",value="一页的条数",defaultValue = "10"),@ApiImplicitParam(name="status",value="状态：0:下架；1:在售")})
    @ApiOperation("获取商家的商品列表")
    @GetMapping("/seller")
    public CommonListResponse<MallGoodListVO> getGoodsSeller(@RequestParam(value = "pageNo",defaultValue = "1")Integer pageNo,
                                                             @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                                             @RequestParam(value = "sellerId")Integer sellerId
    ){
        return new CommonListResponse<>().fromPage(mallGoodService.getUserGoods(pageNo,pageSize,sellerId,1));
    }
    @ApiOperation("编辑商品库存")
    @ApiImplicitParams({@ApiImplicitParam(name="goodId",value="商品ID"),
            @ApiImplicitParam(name="stock",value="库存")})
    @PostMapping("/stock")
    public CommonResponse putGoodStock(@RequestParam("goodId") Integer goodId, @RequestParam("stock") Integer stock){
        preventResubmission("/good/stock",ShiroUtils.getUser().getId());
        Integer salesPermit = ShiroUtils.getUser().getSalesPermit();
        Assert.check(salesPermit == 0,ErrorCode.ERR_MALL_GOOD_UNPERMIT);
        Assert.check(stock <= 0,ErrorCode.ERR_MALL_GOOD_UNDERSTOCK);
        mallGoodService.editGoodStock(goodId,stock);
        return new CommonResponse();
    }
    public void preventResubmission(String url,Integer userId){
        String key = SUBMIT + userId.toString() + url;
        String s = redisTemplate.opsForValue().get(key);
        redisTemplate.opsForValue().set(key,key,2L,TimeUnit.SECONDS);
        Assert.check(s != null,ErrorCode.ERR_REPEAT_SUBMIT);//重复提交
    }

}
