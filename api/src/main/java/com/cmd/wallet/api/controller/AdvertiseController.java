package com.cmd.wallet.api.controller;


import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.common.vo.AdResVO;
import com.cmd.wallet.service.AdService;
import com.cmd.wallet.service.ConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;


/**
 * Created by Administrator on 2018/5/31.
 */
@Api(tags = "广告管理")
@RequestMapping("/advertise")
@RestController
public class AdvertiseController {

    @Autowired
    private AdService adService;
    @Autowired
    private ConfigService configService;


    @ApiOperation(value = "查询广告列表")
    @GetMapping("")
    public CommonResponse<List<AdResVO>> getAdList(
            @ApiParam(value = "en_US: 英文, zh_CN: 中文简体 zh_TW:中文繁体") @RequestParam(required = false) String locale,
            @ApiParam(value = "Shuffling_figure：首页轮播图，ROLLING：滚动公告") @RequestParam(required = true) String position
    ) {
        if (StringUtils.isBlank(locale)) {
            locale = Locale.SIMPLIFIED_CHINESE.toString();
        }
        List<AdResVO> voList = adService.queryADList(locale,position);
        return new CommonResponse<>(voList);
    }

    @ApiOperation(value = "查询广告详情")
    @GetMapping("detail")
    public CommonResponse<AdResVO> getAdList(@ApiParam(value = "广告id") @RequestParam Integer adId) {
        AdResVO adResVO = adService.getAdDetail(adId);
        return new CommonResponse(adResVO);
    }

}
