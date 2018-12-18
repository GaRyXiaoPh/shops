package com.cmd.wallet.webadmin.controller;

import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.roses.core.base.controller.BaseController;
import com.cmd.wallet.common.model.*;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.common.vo.MallGoodVO;
import com.cmd.wallet.service.ImageService;
import com.cmd.wallet.service.MallCategoryService;
import com.cmd.wallet.service.MallGoodService;
import com.cmd.wallet.service.UserService;
import com.cmd.wallet.webadmin.common.PageResponse;
import com.cmd.wallet.webadmin.common.PageUtil;
import com.github.pagehelper.Page;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品列表控制器
 *
 * @author fengshuonan
 * @Date 2018-12-06 12:10:31
 */
@Controller
@RequestMapping("/tMallGood")
public class TMallGoodController extends BaseController {

    private String PREFIX = "/webadmin/tMallGood/";

    @Autowired
    private MallGoodService mallGoodService;
    @Autowired
    private UserService userService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private MallCategoryService mallCategoryService;

    /**
     * 跳转到商品列表首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "tMallGood.html";
    }

    /**
     * 跳转到添加商品列表
     */
    @RequestMapping("/tMallGood_add")
    public String tMallGoodAdd() {
        return PREFIX + "tMallGood_add.html";
    }

    /**
     * 跳转到修改商品列表
     */
    @RequestMapping("/tMallGood_update/{tMallGoodId}")
    public String tMallGoodUpdate(@PathVariable Integer tMallGoodId, Model model) {
        TMallGoodModel goodInfo = mallGoodService.getGoodInfo(tMallGoodId);
        List<TImageModel> images = imageService.getImgByRefIdAndType(tMallGoodId,MallGoodService.IMG_SLIDESHOW);
        List<String> imgUrl = images.stream().map(TImageModel::getImgUrl).collect(Collectors.toList());
        MallGoodVO mallGoodVO = new MallGoodVO().setImages(imgUrl);
        BeanUtils.copyProperties(goodInfo,mallGoodVO);
        User user = userService.getUserByUserId(goodInfo.getUserId());
        mallGoodVO.setUserName(user.getUserName());
        model.addAttribute("item",mallGoodVO);
        LogObjectHolder.me().set(mallGoodVO);
        return PREFIX + "tMallGood_edit.html";
    }

    /**
     * 获取商品列表列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(@ApiParam("状态") @RequestParam(required = false) Integer status,
                       @ApiParam("用户名") @RequestParam(required = false) String userName,
                       @ApiParam("商品名称") @RequestParam(required = false) String goodName,
                       @ApiParam("是否删除") @RequestParam(required = false) Integer isDelete,
                       @ApiParam(value = "分页参数， 从1开始", required = true) @RequestParam(required = true) Integer offset,
                       @ApiParam(value = "每页记录数", required = true) @RequestParam(required = true) Integer limit) {

        Page<TMallGoodModel> pg = mallGoodService.getGoodsList(userName,goodName,status,isDelete, PageUtil.offsetToPage(offset, limit), limit);
        return new PageResponse<TMallGoodModel>(pg);

    }


    @ApiImplicitParams({@ApiImplicitParam(name="goodId",value="商品ID"),@ApiImplicitParam(name="type",value="类型，0：下架，1：删除并且下架，2：上架，3：恢复删除")})
    @ApiOperation("修改商品状态")
    @PostMapping("/edit-status")
    @ResponseBody
    public CommonResponse editGoodStatus(@RequestParam("goodId") Integer goodId, @RequestParam("type") Integer type){
        mallGoodService.editGoodStatus(goodId,type);
        return new CommonResponse();
    }

    @ApiOperation("获取分类列表")
    @GetMapping("/categories")
    @ResponseBody
    public CommonResponse<List<MallCategory>> categories(){
        return new CommonResponse<>(mallCategoryService.getAllMallCategory());
    }


//    /**
//     * 商品列表详情
//     */
//    @RequestMapping(value = "/detail/{tMallGoodId}")
//    @ResponseBody
//    public Object detail(@PathVariable("tMallGoodId") Integer tMallGoodId) {
//        return tMallGoodService.selectById(tMallGoodId);
//    }
}
