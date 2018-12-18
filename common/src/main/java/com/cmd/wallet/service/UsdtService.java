package com.cmd.wallet.service;

import com.cmd.wallet.blockchain.usdt.OmniClient;
import com.cmd.wallet.common.constants.CoinCategory;
import com.cmd.wallet.common.constants.ConfigKey;
import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.model.Coin;

import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.common.vo.UserCoinVO;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

//用于对比特币以及比特币山寨币系列的币种进行一些操作
@Service
public class UsdtService {
    private static Log log = LogFactory.getLog(UsdtService.class);
    @Autowired
    private UserService userService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private UserCoinService userCoinService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private ReceivedCoinService receivedCoinService;
    // 交易确认的区块数
    private int txConfirmCount = 3;

    /**
     *  同步所有币的记录
     */
    public void syncNewTransactions() {
        List<Coin> coins = coinService.getCoinsByCategory(CoinCategory.USDT);
        for(Coin coin : coins) {
            try {
                syncNewTransactions(coin.getName());
            }catch (Exception ex) {
                log.error("", ex);
            }
        }
    }

    /**
     * 同步一个币的新交易
     * @param coinName  币种名称
     * @return  返回同步的区块个数
     * @throws Exception  _
     */
    public int syncNewTransactions(String coinName) throws Exception {
        log.info("begin sync New usdt Transactions conname=" + coinName);
        Coin coin = coinService.getCoinByName(coinName);
        if(coin == null) {
            log.warn("do not find coin of name:" + coinName);
            return 0;
        }
        if(!CoinCategory.USDT.equals(coin.getCategory())) {
            log.error("coin category not match(want usdt),cannot sync block chain,name=" + coinName);
            return 0;
        }
        String url = "http://" + coin.getServerUser() + ':' + coin.getServerPassword() + "@" + coin.getServerAddress() + ":" + coin.getServerPort() + "/";
        OmniClient client = new OmniClient(url, 31);

        int blockNum = client.getBlockCount();
        // 获取上次同步到的区块高度，从上次同步到的高度+1开始同步
        String syncKey = ConfigKey.BC_LAST_SYNC_BLOCK_PRE + coinName;
        //int lastSyncBlock = configService.getConfigValue(syncKey, 0);
        int lastSyncBlock = configService.getConfigValue(syncKey, blockNum > 2000 ? blockNum - 2000 : 0);
        int curBlock = lastSyncBlock + 1;
        //int toBlock = blockNum - txConfirmCount;
        int toBlock = blockNum;
        if(curBlock >= toBlock) {
            log.info("no need to sync eth toBlock=" + toBlock + ",blockNum=" + blockNum );
            return 0;
        }

        // 开始同步老的没有完成的交易
        log.info("begin syn not confirm transactions,confirm count=" + txConfirmCount);
        syncOldTransaction(coin, client);
        log.info("end syn not confirm transactions");

        log.info("begin sync,from=" + curBlock + ",toBlock=" + toBlock + ",coin=" + coin.getName() + ",base url=" + url);
        int count = 0;
        while(curBlock < toBlock) {
            try {
                syncBlock(coin, client, curBlock);
                curBlock++;
                count++;
                if(count >= 1000) {
                    log.warn("syn much usdt block,break,count:" + count);
                    break;
                }
            } catch (Exception ex) {
                log.error("sync usdt block failed:" + ex.getMessage(), ex);
                break;
            }
        }
        configService.setConfigValue(syncKey, Integer.toString(curBlock - 1));
        log.info("end syncNewTransactions,now lastSyncBlock=" + (curBlock - 1) + ",coin name=" + coinName + ",sync count=" + count);
        return count;
    }

    /**
     * 同步一个区块的交易
     * @param coin     币种
     * @param client   客户端对象
     * @param blockHeight  同步的区块高度
     */
    private void syncBlock(Coin coin, OmniClient client, int blockHeight) {
        List<String> txids = client.omni_listBlockTransactions(blockHeight);
        for(String transactionHash : txids) {
            syncTransaction(coin, client, transactionHash);
        }
    }

    private void syncTransaction(Coin coin, OmniClient client, String transactionHash) {
        Map<String, Object> trans = client.omni_getTransaction(transactionHash);
        if(log.isTraceEnabled()) {
            log.trace("sync transaction:" + transactionHash + ", content=" + ReflectionToStringBuilder.toString(trans));
        }
        if (!trans.containsKey("blocktime"))
            return;
        if (!trans.containsKey("sendingaddress") || !trans.containsKey("referenceaddress") || !trans.containsKey("propertyid"))
            return;
        if (!trans.containsKey("txid") || !trans.containsKey("amount") || !trans.containsKey("type"))
            return;
        if (!"Simple Send".equals(trans.get("type").toString()))
            return;

        String valid = trans.get("valid").toString();
        if(!valid.equalsIgnoreCase("true")) {
            log.info("transactionHash:" + transactionHash + " is not valid");
            return;
        }
        String propertyid = trans.get("propertyid").toString();
        if(!propertyid.equals("31")) {
            return;
        }
        String hash = trans.get("txid").toString();
        String timereceived = trans.get("blocktime").toString();
        String fromAddress = trans.get("sendingaddress").toString();
        String toAddress = trans.get("referenceaddress").toString();
        String amount = trans.get("amount").toString();
        //String fee = trans.get("fee").toString();

        //if (!mTrans.containsKey("confirmations"))
        //    return;
        int status = 1;
        int confirm = Integer.parseInt(trans.get("confirmations").toString());
        if(confirm < txConfirmCount) {
            status =  confirm - txConfirmCount;
            if(status < -99) {
                log.warn("syncTransaction status==" + status + ",confirmCount=" + confirm + ",ConfirmCount "
                        + txConfirmCount + ",trans=" + ReflectionToStringBuilder.toString(trans));
                status = -99;
            }
        }

        // 判断是否是平台地址
        Integer userId = userCoinService.getUserIdByCoinNameAndAddress(coin.getName(), toAddress);
        if (userId==null) return;
        // 检查数据通过
        log.info("find valid transaction:" + transactionHash + ", content=" + ReflectionToStringBuilder.toString(trans));

        // 如果交易已经存在，那么忽略
        /*if(transferService.isTransactionExists(coin.getName(), transactionHash)) {
            log.info("transaction:" + transactionHash + " already exists");
            return;
        }*/
        // 如果是内部转账，忽略
        Integer sendUserId = userCoinService.getUserIdByCoinNameAndAddress(coin.getName(), fromAddress);
        if(sendUserId != null) {
            log.info("inner transfer,ignore,tx=" + transactionHash);
            return;
        }


        // 如果交易已经存在，那么忽略
        if(receivedCoinService.isTransactionExist(coin.getName(), transactionHash)) {
            log.info("transaction:" + transactionHash + " already exists");
            return;
        }

        BigDecimal recv = new BigDecimal(amount);
        BigDecimal fee = BigDecimal.ZERO;
        if(coin.getReceivedFee() < 1 && coin.getReceivedFee() > 0) {
            fee = recv.multiply(new BigDecimal(Float.toString(coin.getReceivedFee()))).setScale(8, RoundingMode.HALF_UP);
        }
        receivedCoinService.addTransaction(userId, coin.getName(), toAddress, transactionHash, recv, fee, Integer.parseInt(timereceived), 1, fromAddress);
    }

    // 同步老的交易
    private void syncOldTransaction(Coin syncCoin, OmniClient client) {
        log.info("sync btc old transction of coin " + syncCoin.getName());
        List<String> oldTransactions = receivedCoinService.getAllNotConfirmCoinTxids(syncCoin.getName());
        for(String txId : oldTransactions) {
            log.info("begin syn old usdt transaction " + txId);
            try {
                syncTransaction(syncCoin, client, txId);
            } catch (Exception ex) {
                log.error("", ex);
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    private OmniClient getRpcClient(String coinName){
        Coin coin = coinService.getCoinByName(coinName);
        Assert.check(coin==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
        Assert.check(!CoinCategory.USDT.equals(coin.getCategory()), ErrorCode.ERR_PARAM_ERROR);

        String url = "http://" + coin.getServerUser() + ':' + coin.getServerPassword() + "@" + coin.getServerAddress() + ":" + coin.getServerPort() + "/";
        try{
            return new OmniClient(url, 31);
        } catch (Exception e){
            return null;
        }
    }

    public String getAccountAddress(int userId, String coinName){
        UserCoinVO userCoin = userCoinService.getUserCoinByUserIdAndCoinName(userId, coinName);
        if (userCoin!=null && userCoin.getBindAddress()!=null && userCoin.getBindAddress().length()>0){
            return userCoin.getBindAddress();
        }
        OmniClient client = this.getRpcClient(coinName);
        if (client!=null){
            String address = client.omni_getAccountAddress(coinName+userId);
            if (address != null){
                UserCoinVO tmp = userCoinService.getUserCoinByUserIdAndCoinName(userId, coinName);
                if (tmp==null){
                    userCoinService.addUserCoin(userId, coinName, address);
                } else {
                    userCoinService.updateUserCoinAddress(userId, coinName, address);
                }
                return address;
            }
        }
        return null;
    }

    public double getAccountBalance(int userId, String coinName){
        Coin coin = coinService.getCoinByName(coinName);
        Assert.check(coin==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);

        UserCoinVO userCoin = userCoinService.getUserCoinByUserIdAndCoinName(userId, coinName);
        Assert.check(userCoin==null, ErrorCode.ERR_BTC_CLIENT_RPC_ERROR);

        OmniClient client = this.getRpcClient(coinName);
        Assert.check(client==null, ErrorCode.ERR_BTC_CLIENT_RPC_ERROR);

        return client.omni_getBalance(userCoin.getBindAddress());
    }

    public String sendToAddress(int userId, String coinName, String toAddress, double amount){
        Coin coin = coinService.getCoinByName(coinName);
        Assert.check(coin==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);

        OmniClient client = this.getRpcClient(coinName);
        Assert.check(client==null, ErrorCode.ERR_USDT_CLIENT_RPC_ERROR);

        String coinbase = coin.getCoinBase();
        Assert.check(coinbase==null ||coinbase.length()<=0, ErrorCode.ERR_COIN_BASE_ERROR);
        Assert.check(!client.validateAddress(toAddress).isValid(), ErrorCode.ERR_INVALID_ADDRESS);

        return client.omni_send(coinbase, toAddress, amount);
    }

    public int getTxConfirmCount(String coinName, String txid){
        Coin coin = coinService.getCoinByName(coinName);
        Assert.check(coin==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);

        OmniClient client = this.getRpcClient(coinName);
        Map<String, Object> m = client.omni_getTransaction(txid);
        String confirm = m.get("confirmations").toString();
        if (confirm!=null){
            return Integer.parseInt(confirm);
        }else{
            log.info("Txid:"+txid+":null");
        }
        return 0;
    }

    public boolean isAddressValid(String coinName, String address){
        Coin coin = coinService.getCoinByName(coinName);
        Assert.check(coin==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);

        OmniClient client = this.getRpcClient(coinName);
        return client.validateAddress(address).isValid();
    }

}
