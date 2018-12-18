package com.cmd.wallet.webadmin.controller;

import cn.stylefeng.roses.core.base.controller.BaseController;
import com.cmd.wallet.common.model.ChangeConfig;
import com.cmd.wallet.common.model.Coin;
import com.cmd.wallet.common.vo.CoinVO;
import com.cmd.wallet.service.ChangeConfigService;
import com.cmd.wallet.service.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/coin")
public class CoinController extends BaseController {
    private String PREFIX = "/webadmin/coin/";

    @Autowired
    private CoinService coinService;
    @Autowired
    private ChangeConfigService changeConfigService;

    @RequestMapping("")
    public String index() {
        return PREFIX + "index.html";
    }

    /**
     * 获取币种配置列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(String condition) {
        List<Coin> list = coinService.getCoin();
        return list;
    }

    /**
     * 跳转到添加币种配置
     */
    @RequestMapping("/coin_add")
    public String coinAdd() {
        return PREFIX + "coin_add.html";
    }

    /**
     * 新增币种配置
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(CoinVO coin) {
        coinService.add(coin);
        return SUCCESS_TIP;
    }

    /**
     * 跳转到修改币种配置
     */
    @RequestMapping("/coin_update/{coinId}")
    public String coinUpdate(@PathVariable Integer coinId, Model model) {
        Coin coin = coinService.getCoinById(coinId);
        CoinVO vo = coinService.getCoinInfo(coin.getName());
        model.addAttribute("item",vo);
        return PREFIX + "coin_edit.html";
    }

    /**
     * 修改币种配置
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(CoinVO coin) {
        coinService.updateCoin(coin);
        return SUCCESS_TIP;
    }

    /**
     * 删除币种配置
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam Integer id) {
        Coin coin = coinService.getCoinById(id);
        coinService.deleteCoin(coin.getName());
        return SUCCESS_TIP;
    }


    //////////////////////////////////////////////////////////////
    @RequestMapping(value = "/rate")
    public Object rateIndex() {
        return PREFIX + "coinRateIndex.html";
    }

    /**
     * 获取币种配置列表
     */
    @RequestMapping(value = "/rate/list")
    @ResponseBody
    public Object rateList(String condition) {
        List<ChangeConfig> list = changeConfigService.getChangeConfigList();
        return list;
    }

    /**
     * 跳转到添加币种配置
     */
    @RequestMapping("/rate/coin_add")
    public String rateAdd() {
        return PREFIX + "coinRate_add.html";
    }

    /**
     * 新增币种配置
     */
    @RequestMapping(value = "/rate/add")
    @ResponseBody
    public Object rateAdd(ChangeConfig config) {
        changeConfigService.add(config);
        return SUCCESS_TIP;
    }

    /**
     * 跳转到修改币种配置
     */
    @RequestMapping("/rate/coin_update/{id}")
    public String rateUpdate(@PathVariable Integer id, Model model) {
        ChangeConfig config = changeConfigService.getChangeConfigById(id);
        model.addAttribute("item", config);
        return PREFIX + "coinRate_edit.html";
    }

    /**
     * 修改币种配置
     */
    @RequestMapping(value = "/rate/update")
    @ResponseBody
    public Object rateUpdate(ChangeConfig config) {
        changeConfigService.updateChangeConfig(new ChangeConfig().setId(config.getId()).setRate(config.getRate()));
        return SUCCESS_TIP;
    }

    @RequestMapping(value = "/rate/delete")
    @ResponseBody
    public Object rateDelete(@RequestParam Integer id) {
        changeConfigService.del(id);
        return SUCCESS_TIP;
    }
}
