package com.cmd.wallet.api.controller;

import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.enums.ArticleStatus;
import com.cmd.wallet.common.enums.ArticleType;
import com.cmd.wallet.common.enums.SalesPermit;
import com.cmd.wallet.common.model.Article;
import com.cmd.wallet.common.model.MallApply;
import com.cmd.wallet.common.model.MallShop;
import com.cmd.wallet.common.model.User;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.common.vo.ArticleVo;
import com.cmd.wallet.service.ArticleService;
import com.cmd.wallet.service.MallShopService;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 商家相关接口
 */
@Api(tags = "商家模块")
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private MallShopService mallShopService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private final static String SUBMIT = "submit_";


    @ApiOperation("获取店铺详情,也作为商家申请状态接口")
    @ApiImplicitParams({@ApiImplicitParam(name="userId",value="商家ID；不传默认为当前用户",required = false)})
    @GetMapping("/info")
    public CommonResponse<MallShop> getShop(@RequestParam(value = "userId",required = false) Integer userId){
        if(userId == null){
            userId = ShiroUtils.getUser().getId();
        }
        return new CommonResponse<>(mallShopService.getMallShopByUserId(userId));
    }
    @ApiOperation("修改店铺详情")
    @PostMapping("/edit")
    public CommonResponse editShop(@RequestBody MallShop mallShop){
        preventResubmission("post:mall/shop/",ShiroUtils.getUser().getId());
        Assert.check(ShiroUtils.getUser().getSalesPermit() == SalesPermit.NO.getValue(),ErrorCode.ERR_MALL_GOOD_UNPERMIT);
        mallShop.setUserId(ShiroUtils.getUser().getId());
        mallShopService.editShop(mallShop);
        return new CommonResponse<>();
    }

    @ApiOperation("编辑店铺公告")
    @PostMapping("/notice")
    public CommonResponse editNoice(@ApiParam("公告内容")@RequestParam String content, @ApiParam("1，显示，2不显示")@RequestParam Integer status){
        Integer id = ShiroUtils.getUser().getId();
        preventResubmission("post:/shop/notice",id);
        Article article = articleService.getArticleShop(id);
        if(article == null){
            ArticleStatus articleStatus = status == 2 ? ArticleStatus.SHOW : ArticleStatus.HIDE;
            ArticleVo article1 = new ArticleVo();
            article1.setContent(content).setCreateTime(new Date()).setCreator(id).setType(ArticleType.SHOP).setStatus(articleStatus).setTitle("11");
            articleService.addArticle(article1);
        }else{
            ArticleStatus articleStatus = status == 2 ? ArticleStatus.SHOW : ArticleStatus.HIDE;
            ArticleVo article1 = new ArticleVo();
            BeanUtils.copyProperties(article,article1);
            article.setContent(content).setStatus(articleStatus);
            articleService.updateArticle(article1);
        }
        return new CommonResponse<>();
    }

    @ApiOperation("店铺公告详情")
    @GetMapping("/notice")
    public CommonResponse<ArticleVo> getNoice(@ApiParam("商家ID")@RequestParam(required = false) Integer sellerId){
        Article article ;
        if(sellerId == null){//如果商家ID不是空的就拿商家的
            Integer id = ShiroUtils.getUser().getId();
            article = articleService.getArticleShop(id);
        }else{
            article = articleService.getArticleShop(sellerId);
        }
        ArticleVo articleVo = null;
        if(article != null && ArticleStatus.SHOW.equals(article.getStatus())){
            articleVo = new ArticleVo();
            BeanUtils.copyProperties(article,articleVo);
        }
        return new CommonResponse<>(articleVo);
    }

    @ApiOperation("申请成为商家")
    @PostMapping("/apply")
    public CommonResponse applyShop(@RequestBody MallApply mallApply){
        User user = ShiroUtils.getUser();
        preventResubmission("post:/shop/apply",user.getId());
        mallShopService.applyShop(user,mallApply);
        return new CommonResponse<>(ErrorCode.ERR_SUCCESS);
    }
    @ApiOperation("申请成为商家结果")
    @GetMapping("/apply")
    public CommonResponse<MallApply> getApplyShop(){
        User user = ShiroUtils.getUser();
        MallApply mallApply = mallShopService.getMallApply(user.getId());
        return new CommonResponse<MallApply>(mallApply);
    }

    public void preventResubmission(String url,Integer userId){
        String key = SUBMIT + userId.toString() + url;
        String s = redisTemplate.opsForValue().get(key);
        redisTemplate.opsForValue().set(key,key,2L,TimeUnit.SECONDS);
        Assert.check(s != null,ErrorCode.ERR_REPEAT_SUBMIT);//重复提交
    }
}
