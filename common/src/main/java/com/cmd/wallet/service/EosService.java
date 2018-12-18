package com.cmd.wallet.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cmd.wallet.blockchain.eos.EosApiClientFactory;
import com.cmd.wallet.blockchain.eos.EosApiRestClient;
import com.cmd.wallet.blockchain.eos.domain.common.ActionTrace;
import com.cmd.wallet.blockchain.eos.domain.common.transaction.PackedTransaction;
import com.cmd.wallet.blockchain.eos.domain.common.transaction.SignedPackedTransaction;
import com.cmd.wallet.blockchain.eos.domain.common.transaction.TransactionAction;
import com.cmd.wallet.blockchain.eos.domain.common.transaction.TransactionAuthorization;
import com.cmd.wallet.blockchain.eos.domain.response.chain.AbiJsonToBin;
import com.cmd.wallet.blockchain.eos.domain.response.chain.Block;
import com.cmd.wallet.blockchain.eos.domain.response.chain.ChainInfo;
import com.cmd.wallet.blockchain.eos.domain.response.chain.account.Account;
import com.cmd.wallet.blockchain.eos.domain.response.chain.transaction.PushedTransaction;
import com.cmd.wallet.blockchain.eos.domain.response.history.action.Action;
import com.cmd.wallet.blockchain.eos.domain.response.history.action.Actions;
import com.cmd.wallet.blockchain.eos.domain.response.history.transaction.Transaction;
import com.cmd.wallet.common.constants.CoinCategory;
import com.cmd.wallet.common.constants.ConfigKey;
import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.mapper.EthAddressMapper;
import com.cmd.wallet.common.model.Coin;
import com.cmd.wallet.common.model.UserCoin;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.common.utils.DateUtil;
import com.cmd.wallet.common.vo.UserCoinVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class EosService {
    private static Logger logger = LoggerFactory.getLogger(EosService.class);

    @Autowired
    private UserCoinService userCoinService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private ReceivedCoinService receivedCoinService;

    public static final String EOSIO = "eosio";
    public static final String EOSIO_TOKEN = "eosio.token";
    public static final Integer EOS_TOTLE = 10;
    private static final String TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    //建议用active账户
//    private static String ownerAccountPublicKey = "EOS6MRyAjQq8ud7hVNYcfnVPJqcVpscN5So8BhtHuGYqET5GDW5CV";


    public EosApiRestClient initEosApiRestClient(){
        EosApiRestClient client = null;
        Coin coin = coinService.getCoinByName(CoinCategory.EOS);
        if(coin == null){
            return client;
        }
        String serverAddress = coin.getServerAddress();
        if(StringUtils.isBlank(serverAddress)){
            return client;
        }
        Assert.check(StringUtils.isEmpty(serverAddress), ErrorCode.ERR_PARAM_ERROR);
        JSONObject jsonObject = JSONObject.parseObject(serverAddress);
        String walletUrl = jsonObject.getString("wallet_url");
        String chainUrl = jsonObject.getString("chain_url");
        if(StringUtils.isNotBlank(walletUrl) && StringUtils.isNotBlank(chainUrl)){
            if(jsonObject!= null && jsonObject.size() == 1){
                client = EosApiClientFactory.newInstance(walletUrl).newRestClient();
            }else{
                client = EosApiClientFactory.newInstance(walletUrl,chainUrl,chainUrl).newRestClient();
            }
        }
        return client;
    }

    public void getActions(Coin coin, int index){
        //记录处理到第几条数据
        int reIndex = index;
        //CoinConstant.EOS_TOTLE 代表每次查询几条数据
        Actions actions = initEosApiRestClient().getActions(coin.getCoinBase(), index, EOS_TOTLE);
        if (actions == null){
            logger.info("getActions actions is null! index:"+index);
            return;
        }

        List<Action> list = actions.getActions();
        if (list==null || list.size() == 0) {
            logger.info("getActions actions list is null! index:"+index);
            return ;
        }

        reIndex += actions.getActions().size();
        list = this.removeDuplicate(list);
        for (Action action : list) {
            ActionTrace actionTrace = action.getActionTrace();
            String account = actionTrace.getAct().getAccount();
            //校验合约必须为eosio.token
            if(!EOSIO_TOKEN.equals(account)){
                continue;
            }
            //{from=eosqxyx11111, to=eosqxyx22222, quantity=10.0000 EOS, memo=test}
            logger.info("交易详情：{}",action.getActionTrace().getAct().getData().toString());
            JSONObject json = JSONObject.parseObject(JSON.toJSONString(actionTrace.getAct().getData()));
            if (!coin.getCoinBase().equals(json.getString("to"))) {
                logger.info("非充值记录：{}",actionTrace.getTrxId());
                continue;
            }
            String memo = json.getString("memo");
            if (StringUtils.isEmpty(memo)) {
                logger.info("用户ID：{}为空",actionTrace.getTrxId());
                continue;
            }
            // 判断是否是平台地址
            Integer userId = userCoinService.getUserIdByCoinNameAndAddress(coin.getName(), memo);
            if (userId==null) continue;

            // 检查数据通过
            String quantity = json.getString("quantity");
            Block block = initEosApiRestClient().getBlock(action.getBlockNum().toString());
            Date date = DateUtil.getDate(block.getTimeStamp(), TIME_PATTERN);

            BigDecimal recv = BigDecimal.ZERO;
            if(StringUtils.isNotBlank(quantity) && quantity.endsWith(" "+coin.getName())){
                String[] split = quantity.split(" ");
                recv = new BigDecimal(split[0]);
            }
            if(recv.doubleValue() <=  BigDecimal.ZERO.doubleValue())continue;
            BigDecimal fee = BigDecimal.ZERO;
            if(coin.getReceivedFee() < 1 && coin.getReceivedFee() > 0) {
                fee = recv.multiply(new BigDecimal(Float.toString(coin.getReceivedFee()))).setScale(8, RoundingMode.HALF_UP);
            }
            String from = json.getString("from");

            if (receivedCoinService.isTransactionExist(coin.getName(), actionTrace.getTrxId())){
                logger.info("coin name:"+coin.getName()+", trxid:"+actionTrace.getTrxId()+" db exist");
                continue;
            }
            receivedCoinService.addTransaction(userId, coin.getName(), memo, actionTrace.getTrxId(), recv, fee, Integer.valueOf(date.getTime()/1000+""), 1, from);
        }
        if (reIndex > index) {
            logger.info("eos执行完毕");
            configService.setConfigValue(ConfigKey.BC_LAST_SYNC_BLOCK_PRE + coin.getName(), reIndex+"");
        }
    }

    /**
     * 去重
     * @param list
     * @return
     */
    public List<Action> removeDuplicate(List<Action> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getActionTrace().getTrxId()
                        .equals(list.get(i).getActionTrace().getTrxId())) {
                    list.remove(j);
                }
            }
        }
        return list;
    }

    public double getBalance(String account, String coinName){
        EosApiRestClient client = initEosApiRestClient();
        List<String> list = client.getCurrencyBalance(EOSIO_TOKEN, account, coinName);
        if (list != null && list.size() > 0) {
            return Double.parseDouble(list.get(0).replaceAll(" "+coinName, ""));
        }
        return 0.00;
    }

    public String getJsonToBin(String from, String to, Double amount, String memo){
        Map<String, String> args = new HashMap<>(4);
        args.put("from", from);
        args.put("to", to);
        //amount 必须要保留4位小数，不够补0
        DecimalFormat df = new DecimalFormat("0.0000");
        String format = df.format(amount);
        args.put("quantity",  format+" EOS");
        args.put("memo", memo);
        EosApiRestClient client = initEosApiRestClient();
        Assert.check(client == null,ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
        AbiJsonToBin data = client.abiJsonToBin(EOSIO_TOKEN, "transfer", args);
        if (data != null) {
            return data.getBinargs();
        }
        return null;
    }

    /**
     * 发送交易
     * @param toAccount
     * @param amount 保留4位小数点
     * @param memo
     * @return
     */
    public String send(String coinName, String toAccount,double amount,String memo){
        Coin coin = coinService.getCoinByName(coinName);
        Assert.check(coin==null, ErrorCode.ERR_NOT_SUPPORT_COIN);

        EosApiRestClient client = initEosApiRestClient();
        Assert.check(client == null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);

        JSONObject jsonObject = JSONObject.parseObject(coin.getCoinSelfParameter());
        logger.debug(coin.getCoinSelfParameter());
        String wallet_name = jsonObject.getString("wallet_name");
        String wallet_pass = jsonObject.getString("wallet_pass");
        String owner_public_key = jsonObject.getString("owner_public_key");
        String active_public_key = jsonObject.getString("active_public_key");
        String binargs = getJsonToBin(coin.getCoinBase(), toAccount, amount, memo);
        ChainInfo chainInfo = client.getChainInfo();
        Block block = client.getBlock(chainInfo.getHeadBlockId());

        TransactionAuthorization transactionAuthorization = new TransactionAuthorization();
        transactionAuthorization.setActor(coin.getCoinBase());
        //发送者权限等级   这一步要注意账户和权限要对应上
        transactionAuthorization.setPermission("owner");
        //transactionAuthorization.setPermission("active");

        TransactionAction transactionAction = new TransactionAction();
        transactionAction.setAccount(EOSIO_TOKEN);
        transactionAction.setName("transfer");
        transactionAction.setData(binargs);
        transactionAction.setAuthorization(Collections.singletonList(transactionAuthorization));

        //设置交易期限
        Date date =  DateUtil.getDate(block.getTimeStamp(),TIME_PATTERN);
        date = DateUtil.addMin(date, 1);
        String exPiration = DateUtil.getDateTimeString(date, TIME_PATTERN);

        PackedTransaction packedTransaction = new PackedTransaction();
        packedTransaction.setRefBlockPrefix(block.getRefBlockPrefix().toString());
        packedTransaction.setRefBlockNum(block.getBlockNum().toString());
        packedTransaction.setExpiration(exPiration);
        packedTransaction.setRegion("0");
        packedTransaction.setMax_net_usage_words("0");
        packedTransaction.setActions(Collections.singletonList(transactionAction));
        //打开钱包
        client.openWallet(wallet_name);
        //解锁钱包
        client.unlockWallet(wallet_name, wallet_pass);

        SignedPackedTransaction signedPackedTransaction = client.signTransaction(packedTransaction, Collections.singletonList(owner_public_key), chainInfo.getChainId());
        PushedTransaction pushedTransaction= client.pushTransaction("none", signedPackedTransaction);
        Assert.check(pushedTransaction == null, ErrorCode.ERR_TRANSFER_FAIL);
        logger.info("EOS转账成功：transactionId:{}",pushedTransaction.getTransactionId());
        return pushedTransaction.getTransactionId();
    }

    /**
     * EOS充值交易处理
     */
    public void eosJob(){
        //代表的是账户记录处理到第几条，不是区块高度
        List<Coin> coins = coinService.getCoinsByCategory(CoinCategory.EOS);
        for (Coin coin : coins) {
            String syncKey = ConfigKey.BC_LAST_SYNC_BLOCK_PRE + coin.getName();
            int eosIndex = configService.getConfigValue(syncKey, 0);
            logger.info("EOS当前处理记录数：{}", eosIndex);
            this.getActions(coin, eosIndex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    public String getAccountAddress(int userId, String coinName){
        String addressTag = UserCoin.getAddressTag(userId);
        UserCoinVO userCoin = userCoinService.getUserCoinByUserIdAndCoinName(userId, coinName);
        if (userCoin==null){
            userCoinService.addUserCoin(userId, coinName, addressTag);
        } else{
            if (userCoin.getBindAddress()==null || userCoin.getBindAddress().trim().length()==0) {
                userCoinService.updateUserCoinAddress(userId, coinName, addressTag);
            }
        }
        return addressTag;
    }

    public String sendToAddress(int userId, String coinName, String toAddress, double amount){
        return null;
    }

    public String sendToAddress(int userId, String coinName, String toAddress, double amount, String comment){
        return send(coinName, toAddress, amount, comment);
    }

    public boolean isAddressValid(String coinName, String address) {
        Coin coin = coinService.getCoinByName(coinName);
        Assert.check(coin==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
        Account account = null;
        try {
            EosApiRestClient client = initEosApiRestClient();
            Assert.check(client == null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
            account = client.getAccount(address);
        }catch (Exception e){
            logger.error(""+e);
        }
        return account != null ? true : false;
    }

    public int getTxConfirmCount(String coinName, String txid) {
        Coin coin = coinService.getCoinByName(coinName);
        Assert.check(coin==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
        EosApiRestClient client = initEosApiRestClient();
        Assert.check(client==null, ErrorCode.ERR_ETH_CLIENT_RPC_ERROR);
        ChainInfo chainInfo = client.getChainInfo();
        BigDecimal blockNumber = new BigDecimal(chainInfo.getHeadBlockNum());
        Transaction transaction = client.getTransaction(txid);
        Integer blockNum = transaction.getBlockNum();
        Assert.check(blockNum==null || blockNum.intValue()<=0, ErrorCode.ERR_INVALID_TXID);
        return blockNumber.subtract(BigDecimal.valueOf(blockNum)).intValue();
    }

}
