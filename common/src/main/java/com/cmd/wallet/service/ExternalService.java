package com.cmd.wallet.service;

import com.cmd.wallet.common.utils.HttpUtil;
import com.cmd.wallet.common.utils.JSONUtil;
import com.google.gson.Gson;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExternalService {
    private static Logger logger = LoggerFactory.getLogger(ExternalService.class);

    static String strHuobi="http://api.huobipro.com/market/detail?symbol="; //btcusdt
    static String strUsd2Cny="http://op.juhe.cn/onebox/exchange/currency?key=3c1ed7d0474662b222d95b384d4b3469&from=USD&to=CNY";
    static String strBittrex="https://bittrex.com/api/v1.1/public/getticker?market=";//USDT-DOGE
    static String strFeixiaohao="https://api.feixiaohao.com/coinhisdata"; // /dogecoin/1540527822000/1540527883000, /bitcoin, /ethereum, /tether
    static String strCoinnew="http://www.coinnew.net/api/market/stats-list?settlementCurrency=EC";

    @Data
    @Accessors(chain=true)
    public static class MarketDetail{
        BigDecimal openPrice;
        BigDecimal closePrice;
        BigDecimal highPrice;
        BigDecimal lowPrice;
        BigDecimal volumn;
        BigDecimal changeRate;
    };

    //symbol=btcusdt,ltcusdt
    public static MarketDetail getMarketDetail(String symbol){
        String rst = HttpUtil.HttpGet(strHuobi+symbol);
        //rst = "{\"status\":\"ok\",\"ch\":\"market.btcusdt.detail\",\"ts\":1527837873786,\"tick\":{\"amount\":15732.157271907080440621,\"open\":7559.410000000000000000,\"close\":7572.920000000000000000,\"high\":7615.350000000000000000,\"id\":8418266759,\"count\":115482,\"low\":7433.000000000000000000,\"version\":8418266759,\"vol\":118511321.289422495715227267620000000000000000}}";
        Map<String, Object> m = JSONUtil.JsonToMap(rst);
        if (!m.containsKey("tick")){
            return null;
        }

        Map<String, Object> tick = (Map)m.get("tick");
        if (!tick.containsKey("close") || !tick.containsKey("open") || !tick.containsKey("high")){
            return null;
        }
        return new MarketDetail().setClosePrice(new BigDecimal(tick.get("close").toString()))
                .setOpenPrice(new BigDecimal(tick.get("open").toString()))
                .setHighPrice(new BigDecimal(tick.get("high").toString()))
                .setLowPrice(new BigDecimal(tick.get("low").toString()))
                .setVolumn(new BigDecimal(tick.get("vol").toString()));
    }

    //非小号获取行情 /dogecoin/1540527822000/1540527883000, /bitcoin, /ethereum, /tether
    public static MarketDetail getMarketLast24(String coin) {
        Long dtEnd = (new Date().getTime() /1000)*1000;
        Long dtStart = dtEnd-24*60*60*1000;
        String rst = HttpUtil.HttpGet(strFeixiaohao+"/"+coin+"/"+dtStart.toString()+"/"+dtEnd.toString());
        Map<String, Object> m = JSONUtil.JsonToMap(rst);
        if (!m.containsKey("price_usd")){
            return null;
        }
        List<Object> list = (List)m.get("price_usd");
        int iSize = list.size();
        List<Object> infoStart = (List<Object>)list.get(iSize-1);
        List<Object> infoEnd = (List<Object>)list.get(0);
        return new MarketDetail().setOpenPrice( new BigDecimal((infoStart.get(1).toString())))
                .setClosePrice(new BigDecimal(infoEnd.get(1).toString()));
    }

    //行情https://bittrex.com/api/v1.1/public/getticker?market=USDT-DOGE
    public static MarketDetail getMarketByBittrex(String symbol){
        String rst = HttpUtil.HttpGet(strBittrex+symbol);
        Map<String, Object> m = JSONUtil.JsonToMap(rst);
        if (!m.containsKey("success") && !m.containsKey("result")){
            return null;
        }
        String success = m.get("success").toString();
        if (!"true".equalsIgnoreCase(success)){
            return null;
        }
        Map<String, Object> tick = (Map)m.get("result");
        if (!tick.containsKey("Last")){
            return null;
        }
        return new MarketDetail().setClosePrice(new BigDecimal(tick.get("Last").toString()));
    }

    //行情coinnew 交易所
    public static Map<String, MarketDetail> getCoinnewAll(){
        String rst = HttpUtil.HttpGet(strCoinnew);
        Map<String, Object> m = JSONUtil.JsonToMap(rst);
        if (!m.containsKey("statusCode")){
            return null;
        }
        Map<String, MarketDetail> rMap = new HashMap<>();
        List<Object> list = (List)m.get("content");
        for (Object obj: list){
            MarketDetail md = new MarketDetail();
            Map<String, Object> mobj = (Map)obj;
            //mobj.get("coinName").toString();
            //mobj.get("settlementCurrency").toString();
            //mobj.get("latestCnyPrice").toString();
            //mobj.get("changeRate").toString();
            //mobj.get("firstPrice").toString();
            if (mobj.get("firstPrice")!=null)
                md.setOpenPrice(new BigDecimal(mobj.get("firstPrice").toString()));
            if (mobj.get("lastPrice")!=null)
                md.setClosePrice(new BigDecimal(mobj.get("lastPrice").toString()));
            if (mobj.get("changeRate")!=null)
                md.setChangeRate(new BigDecimal(mobj.get("changeRate").toString()));
            rMap.put(mobj.get("coinName").toString(), md);
        }
        return rMap;
    }

    //获取汇率
    public static BigDecimal getUsd2Cny(){
        String rst = HttpUtil.HttpGet(strUsd2Cny);
        Map<String, Object> m = JSONUtil.JsonToMap(rst);
        if (!m.containsKey("error_code") || !m.containsKey("result")){
            return null;
        }
        if (!"0".equals(m.get("error_code").toString())){
            return null;
        }
        List<Object> result =  (List)m.get("result");
        for (int i = 0; i < result.size(); i++){
            Map<String, Object> info = (Map)result.get(i);
            if ("USD".equals(info.get("currencyF"))
                    && "CNY".equals(info.get("currencyT"))){
                return new BigDecimal(info.get("result").toString());
            }
        }
        return null;
    }

    public static void main(String[] args) {
        ExternalService.MarketDetail mt = ExternalService.getMarketByBittrex("USDT-BTC");
        BigDecimal price = ExternalService.getUsd2Cny();
        MarketDetail detail = ExternalService.getMarketDetail("btcusdt");
        int tmp = 90;
    }
}
