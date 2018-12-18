package com.cmd.wallet.webadmin.controller;

import cn.stylefeng.roses.core.base.controller.BaseController;
import com.cmd.wallet.common.enums.AdvertisementStatus;
import com.cmd.wallet.common.model.Coin;
import com.cmd.wallet.common.model.TMallGoodModel;
import com.cmd.wallet.common.vo.AdResVO;
import com.cmd.wallet.common.vo.CoinVO;
import com.cmd.wallet.service.AdService;
import com.cmd.wallet.webadmin.common.ErrorTip;
import com.cmd.wallet.webadmin.common.PageResponse;
import com.cmd.wallet.webadmin.common.PageUtil;
import com.github.pagehelper.Page;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/advertise")
public class AdvertiseController extends BaseController {

    private String PREFIX = "/webadmin/advertise/";

    @Autowired
    AdService adService;

    @RequestMapping("")
    public String index() {
        return PREFIX + "index.html";
    }

    /**
     * 获取广告列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public PageResponse<AdResVO> list(@ApiParam("广告状态") @RequestParam(required = false) Integer status,
                                      @ApiParam("Shuffling_figure：首页轮播图，ROLLING：滚动公告") @RequestParam(required = false) String position,
                                      @ApiParam(value = "分页参数， 从1开始", required = false) @RequestParam(required = false) Integer offset,
                                      @ApiParam(value = "每页记录数", required = false) @RequestParam(required = false) Integer limit) {

        int pageNo = PageUtil.offsetToPage(offset, limit);
        int pageSize = limit;

        Page<AdResVO> list = adService.getAdResVOPageList(status, position, pageNo, pageSize);
        return new PageResponse<AdResVO>(list);
    }

    /**
     * 跳转广告增加页面
     */
    @RequestMapping("/advertise_add")
    public String advertiseAdd() {
        return PREFIX + "advertise_add.html";
    }

    /**
     * 新增广告配置
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(AdResVO adResVO) {
        AdResVO rollingAdResVO = adService.getAdResVOByPosition(adResVO.getPosition());
        if(rollingAdResVO!=null){
            ErrorTip errorTip  = new ErrorTip(201,"已经存在该语言类型滚动公告，增加失败");
            return errorTip;
        }
        adService.add(adResVO);
        return SUCCESS_TIP;
    }


    /**
     * 跳转到修改广告配置
     */
    @RequestMapping("/advertise_update/{id}")
    public String coinUpdate(@PathVariable Integer id, Model model) {
        AdResVO adResVO = adService.getAdDetail(id);
        model.addAttribute("item",adResVO);
        return PREFIX + "advertise_edit.html";
    }


    /**
     * 修改公告配置
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(AdResVO adResVO) {
        adResVO.setLastTime(new Date());
        adService.updateADInfo(adResVO);
        return SUCCESS_TIP;
    }


    /**
     * 删除公告配置
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam Integer id) {
        AdResVO adResVO = adService.getAdDetail(id);
        if( adResVO.getStatus()== AdvertisementStatus.SHOW){
            ErrorTip errorTip  = new ErrorTip(201,"禁止删除已上线的广告！");
            return errorTip;
        }
        adService.delById(id);
        return SUCCESS_TIP;
    }

}
