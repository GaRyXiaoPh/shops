package com.cmd.wallet.api.controller;



import com.cmd.wallet.api.vo.TransferOutRequestVO;
import com.cmd.wallet.common.model.*;
import com.cmd.wallet.common.oauth2.ShiroUtils;
import com.cmd.wallet.common.response.CommonListResponse;
import com.cmd.wallet.common.response.CommonResponse;
import com.cmd.wallet.common.vo.*;
import com.cmd.wallet.service.*;
import com.github.pagehelper.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@Api(tags = "钱包管理")
@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    WalletService walletService;
    @Autowired
    SendCoinService sendCoinService;
    @Autowired
    ReceivedCoinService receivedCoinService;

    @ApiOperation("获取钱包类型")
    @GetMapping("/wallet")
    public CommonResponse<List<WalletVO>> getWallet(){
        return new CommonResponse(walletService.getWallet());
    }

    @ApiOperation("获取钱包币")
    @GetMapping("/wallet-coin")
    public CommonResponse<List<UserCoinVO>> getWalletCoin(@RequestParam("walletName")String walletName){
        return new CommonResponse(walletService.getCoinByWallet(ShiroUtils.getUser().getId(), walletName));
    }

    @ApiOperation("获取钱包余额")
    @GetMapping("/wallet-balance")
    public CommonResponse<BigDecimal> getWallet(@RequestParam("walletName")String walletName) {
        return new CommonResponse(walletService.getCoinBalanceByWallet(ShiroUtils.getUser().getId(), walletName));
    }

    @ApiOperation("获取钱包币余额")
    @GetMapping("/wallet-coin-all")
    public CommonResponse<WalletCoinVO> getWalletCoinAll(@RequestParam("walletName")String walletName) {
        return new CommonResponse(walletService.getWalletCoinAll(ShiroUtils.getUser().getId(), walletName));
    }

    @ApiOperation("获取地址")
    @GetMapping("/address")
    public CommonResponse<String> getAddressByCoinName(@RequestParam("coinName")String coinName) {
        String address = walletService.getAddressByCoinName(ShiroUtils.getUser().getId(), coinName);
        return new CommonResponse(address);
    }

    @ApiOperation("获取某个币种余额")
    @GetMapping("/balance")
    public CommonResponse<UserCoinVO> balance(@RequestParam("coinName")String coinName) {
        return new CommonResponse<>(walletService.getUserCoin(ShiroUtils.getUser().getId(), coinName));
    }

    @ApiOperation("获取币种余额")
    @GetMapping("/all-balance")
    public CommonResponse<List<UserCoinVO>> allBalance() {
        return new CommonResponse(walletService.getUserCoinList(ShiroUtils.getUser().getId()));
    }

    @ApiOperation("是否是内部地址:1内部地址，0外部地址")
    @GetMapping("/innerAddress")
    public CommonResponse<Integer> innerAddress(@RequestParam("coinName")String coinName, @RequestParam("address")String address){
        return new CommonResponse(walletService.innerAddress(coinName, address));
    }

    @ApiOperation("转账(address)")
    @PostMapping("/transfer")
    public CommonResponse transfer(@Valid TransferOutRequestVO transferOutReqVO){
        walletService.transferInner(ShiroUtils.getUser().getId(), transferOutReqVO.getCoinName(), transferOutReqVO.getToAddress(),
                transferOutReqVO.getAmount(), transferOutReqVO.getComment(), "", transferOutReqVO.getPaypassword());
        return new CommonResponse();
    }

    @ApiOperation("提币")
    @PostMapping("/withdraw-coin")
    public CommonResponse withdrawCoin(@Valid TransferOutRequestVO transferOutReqVO) {
        walletService.transferOut(ShiroUtils.getUser().getId(), transferOutReqVO.getCoinName(), transferOutReqVO.getToAddress(),
                transferOutReqVO.getAmount(), transferOutReqVO.getComment(), "", transferOutReqVO.getPaypassword());
        return new CommonResponse();
    }

    @ApiOperation("获取转账记录")
    @GetMapping("/get-send-list")
    public CommonListResponse<SendCoin> getSendList(@RequestParam(required = false)String coinName,
                                                    @RequestParam(required = false)Integer[]status,
                                                    @RequestParam("pageNo")Integer pageNo,
                                                    @RequestParam("pageSize")Integer pageSize){
        if (coinName==null || coinName.trim().length()==0)
            coinName=null;
        Page<SendCoin> pg = sendCoinService.getTransferList(ShiroUtils.getUser().getId(), coinName, status, pageNo, pageSize);
        return new CommonListResponse<>().fromPage(pg);
    }

    @ApiOperation("获取收账记录")
    @GetMapping("/get-recv-list")
    public CommonListResponse<ReceivedCoin> getRecvList(@RequestParam(required = false)String coinName,
                                                        @RequestParam("pageNo")Integer pageNo,
                                                        @RequestParam("pageSize")Integer pageSize){
        if (coinName==null || coinName.trim().length()==0)
            coinName=null;
        Page<ReceivedCoin> pg = receivedCoinService.getTransferList(ShiroUtils.getUser().getId(), coinName, pageNo, pageSize);
        return new CommonListResponse<>().fromPage(pg);
    }

    @ApiOperation("获取流水记录")
    @GetMapping("/get-bill-log")
    public CommonListResponse<UserBillVO> getUserBill(@ApiParam(value = "流水类型：Register,Referrer,Invest,Minward,Comward", required=false) @RequestParam(required = false)String[] reason,
                                                      @RequestParam(name = "coinName", required = false)String coinName,
                                                      @RequestParam("pageNo")Integer pageNo,
                                                      @RequestParam("pageSize")Integer pageSize) {
        if (coinName!=null && coinName.trim().length()==0)
            coinName=null;

        Page<UserBillVO> pg = walletService.getUserBillByReason(ShiroUtils.getUser().getId(), coinName, reason, pageNo, pageSize);
        return new CommonListResponse<>().fromPage(pg);
    }

    @ApiOperation("获取推荐奖励")
    @GetMapping("/get-referrer-reward")
    public CommonListResponse<RewardLogVO> getRewardLog(@RequestParam("pageNo")Integer pageNo,
                                                        @RequestParam("pageSize")Integer pageSize) {
        Page<RewardLogVO> pg = walletService.getRewardLog(ShiroUtils.getUser().getId(), pageNo, pageSize);
        return new CommonListResponse<>().fromPage(pg);
    }

    @ApiOperation("转账手续费配置")
    @GetMapping("/coin-config")
    public CommonResponse<CoinConfig> getCoinConfigList(@RequestParam("coinName")String coinName){
        return new CommonResponse<>(walletService.getCoinConfig(coinName));
    }

    @ApiOperation("获取币种的汇率")
    @GetMapping("/change-config")
    public CommonResponse<ChangeConfig> getChangeConfig(@RequestParam("coinName")String coinName,@RequestParam("changeName")String changeName){
        return new CommonResponse<>(walletService.getChangeConfig(coinName,changeName));
    }

    @ApiOperation("获取币种价格和涨跌")
    @GetMapping("/get-coin-last24")
    public CommonResponse<CoinLastVO> getCoinLast24(@RequestParam("coinName")String coinName, @RequestParam("changeName")String changeName){
        return new CommonResponse<>(walletService.getCoinLast24(coinName, changeName));
    }

    @ApiOperation("获取币种价格和涨跌")
    @GetMapping("/get-all-coin-last24")
    public CommonResponse<List<CoinLastVO>> getAllCoinLast24(){
        return new CommonResponse(walletService.getAllCoinLast24());
    }

    @ApiOperation("获取助记词")
    @GetMapping("/get-user-words")
    public CommonResponse<String> getUserWords(){
        UserWords userWords = walletService.getUserWords(ShiroUtils.getUser().getId());
        String words=null;
        if (userWords!=null){
            words=userWords.getWords();
        }
        return new CommonResponse(words);
    }

    @ApiOperation("确认助记词")
    @PostMapping("/confirm-user-words")
    public CommonResponse confirmUserWords(@RequestParam("words")String words){
        walletService.confirmUserWords(ShiroUtils.getUser().getId(), words);
        return new CommonResponse();
    }

    @ApiOperation("我的累计收益")
    @GetMapping("/get-my-reward")
    public CommonResponse<UserEarningsVO> getMyReward(){
        return new CommonResponse<>(walletService.getUserEarnings(ShiroUtils.getUser().getId()));
    }

    @ApiOperation("我的收益流水")
    @GetMapping("/get-reward-day")
    public CommonListResponse<EarningsDay> getRewardDay(@RequestParam("pageNo")Integer pageNo, @RequestParam("pageSize")Integer pageSize){
        Page<EarningsDay> pg = walletService.getRewardDay(ShiroUtils.getUser().getId(), pageNo, pageSize);
        return new CommonListResponse<>().fromPage(pg);
    }
}
