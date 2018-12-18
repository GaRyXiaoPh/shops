package com.cmd.wallet.webadmin.controller;


import cn.stylefeng.roses.core.base.controller.BaseController;
import com.cmd.wallet.common.mapper.ConfigLevelMapper;
import com.cmd.wallet.common.model.Coin;
import com.cmd.wallet.common.model.Config;
import com.cmd.wallet.common.model.ConfigLevel;
import com.cmd.wallet.common.vo.CoinVO;
import com.cmd.wallet.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/params")
public class ParamsController extends BaseController {

    @Autowired
    ConfigService configService;
    @Autowired
    ConfigLevelMapper configLevelMapper;

    private String PREFIX = "/webadmin/params/";


    @RequestMapping("/config")
    public String index() {
        return PREFIX + "config.html";
    }

    @RequestMapping(value = "/config/list")
    @ResponseBody
    public Object list(String condition) {
        List<Config> list = configService.getConfigList();
        return list;
    }

    @RequestMapping("/config/config_add")
    public String configAdd() {
        return PREFIX + "config_add.html";
    }

    @RequestMapping(value = "/config/add")
    @ResponseBody
    public Object add(Config config) {
        configService.setConfigValue(config.getConfName(), config.getConfValue());
        return SUCCESS_TIP;
    }

    @RequestMapping("/config/config_update/{id}")
    public String configUpdate(@PathVariable Integer id, Model model) {
        Config config=configService.getConfigById(id);
        model.addAttribute("item", config);
        return PREFIX + "config_edit.html";
    }

    //修改币种配置
    @RequestMapping(value = "/config/update")
    @ResponseBody
    public Object update(Config config) {
        configService.setConfigValue(config.getConfName(), config.getConfValue());
        return SUCCESS_TIP;
    }

    @RequestMapping(value = "/config/delete")
    @ResponseBody
    public Object delete(@RequestParam Integer id) {
        return SUCCESS_TIP;
    }




    @RequestMapping("/level")
    public String indexLevel() {
        return PREFIX + "level.html";
    }

    @RequestMapping(value = "/level/list")
    @ResponseBody
    public Object listLevel(String condition) {
        List<ConfigLevel> list = configLevelMapper.getConfigList();
        return list;
    }

    @RequestMapping("/level/level_update/{id}")
    public String levelUpdate(@PathVariable Integer id, Model model) {
        ConfigLevel config=configLevelMapper.getConfigByName(id);
        model.addAttribute("item", config);
        return PREFIX + "level_edit.html";
    }

    //修改币种配置
    @RequestMapping(value = "/level/update")
    @ResponseBody
    public Object level_update(ConfigLevel config) {
        configLevelMapper.updateLevelConfig(config.getLevel(), config.getRate(), config.getConsume());
        return SUCCESS_TIP;
    }
}
