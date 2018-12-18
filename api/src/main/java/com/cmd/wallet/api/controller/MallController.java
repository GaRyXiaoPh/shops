package com.cmd.wallet.api.controller;

import com.cmd.wallet.common.model.MallCategory;
import com.cmd.wallet.common.model.TSearchHistoryModel;
import com.cmd.wallet.common.model.User;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.service.MallCategoryService;
import com.cmd.wallet.service.SearchHistoryService;
import com.cmd.wallet.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商城相关接口
 */
@Api(tags = "商城模块")
@RestController
@RequestMapping("mall")
public class MallController {
    @Autowired
    private MallCategoryService mallCategoryService;
    @Autowired
    private SearchHistoryService searchHistoryService;
    @Autowired
    private UserService userService;

    @ApiOperation("获取分类列表")
    @GetMapping("/categories")
    public CommonResponse<List<MallCategory>> categories(){
        return new CommonResponse<>(mallCategoryService.getAllMallCategory());
    }

    @ApiOperation("获取商家分类列表")
    @GetMapping("/shoper-categories")
    public CommonResponse<List<MallCategory>> getShoperCategories(){
        Integer userId = ShiroUtils.getUser().getId();
        User user = userService.getUserByUserId(userId);
        List<MallCategory> mallList= mallCategoryService.getAllMallCategory();
        if(user.getBrandPermit().equals("1")) {
            return new CommonResponse<>(mallList);
        }else{
            //888为特殊模块
            if(mallList!=null&&mallList.size()>0){
                for(int i=0;i<mallList.size();i++){
                    if(mallList.get(i).getId().equals("888")){
                        mallList.remove(i);
                        break;
                    }
                }
            }
            return new CommonResponse<>(mallList);
        }
    }


    @ApiOperation("获取当前用户的搜索历史")
    @GetMapping("/search-history")
    public CommonResponse<List<TSearchHistoryModel>> searchHistory() {
        return new CommonResponse<>(searchHistoryService.queryList(ShiroUtils.getUser().getId()));
    }



}
