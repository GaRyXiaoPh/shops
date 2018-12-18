package com.cmd.wallet.api.task;

import com.cmd.wallet.blockchain.eth.ETHHelper;
import com.cmd.wallet.common.constants.CoinCategory;
import com.cmd.wallet.common.constants.ConfigKey;
import com.cmd.wallet.common.mapper.CoinConfigMapper;
import com.cmd.wallet.common.mapper.GatherLogMapper;
import com.cmd.wallet.common.mapper.ReceivedCoinMapper;
import com.cmd.wallet.common.model.*;
import com.cmd.wallet.service.CoinService;
import com.cmd.wallet.service.ConfigService;
import com.cmd.wallet.service.EthService;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;

/**
 *  将以太坊上各个地址的钱汇聚到一个目的地址
 */
@Component
public class GatherTokenTask {
    private Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private ConfigService configService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private EthService ethService;
    @Autowired
    private CoinConfigMapper coinConfigMapper;
    @Autowired
    private ReceivedCoinMapper receivedCoinMapper;
    @Autowired
    private GatherLogMapper gatherLogMapper;

    private Thread gatherThread;

    @Scheduled(cron = "0 0 3,4 * * ?")
    public synchronized void gatherTokenTimer() {
        if(gatherThread != null && gatherThread.isAlive()) {
            logger.error("gatherTokenTimer ia alive, cannot start new work");
            return;
        }
        gatherThread = new Thread("gatherTokenTimer"){
            public void run() {
                gatherTokenThread();
            }
        };
        gatherThread.start();
    }
    public void gatherTokenThread() {
        try {
            logger.info("begin gatherTokenTimer");
            gatherToken();
            logger.info("end gatherTokenTimer");
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    private void gatherToken() {
        // 获取汇聚的地址
        List<Coin> ethCoins = coinService.getCoinsByCategory(CoinCategory.TOKEN);
        if(ethCoins.size() == 0) {
            logger.info("coin token not found");
            return;
        }
        for(Coin coin : ethCoins) {
            try {
                gatherOneToken(coin);
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
    }

    private BigDecimal checkAndGetBalance(Web3j web3, String address, BigDecimal ignoreBalance) {
        try {
            EthGetBalance getBalance = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            BigDecimal balance = Convert.fromWei(getBalance.getBalance().toString(), Convert.Unit.ETHER);
            if(balance.compareTo(ignoreBalance) >= 0) {
                return balance;
            }
        } catch (Exception ex) {
            logger.warn("", ex);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal checkAndGetTokenBalance(EthAddress coinBaseEthAddress, EthAddress ethAddress, String coinName, String contractAddress, BigDecimal ignoreBalance){
        try {
            ETHHelper helper = ethService.getRpcClient(coinName);
            BigDecimal balance = helper.contractBalanceEx(ethAddress.getCredentials(), ethAddress.getPassword(), contractAddress);
            if (balance.compareTo(ignoreBalance) >= 0){
                return balance;
            }
        } catch (Exception ex){
            logger.warn("", ex);
        }
        return BigDecimal.ZERO;
    }

    private String checkAndSendEthBalance(EthAddress coinBaseEthAddress, EthAddress ethAddress, String coinName, double balance){
        try {
            ETHHelper helper = ethService.getRpcClient(coinName);
            return helper.transferEx(coinBaseEthAddress.getCredentials(), coinBaseEthAddress.getPassword(), ethAddress.getAddress(), balance);
        } catch (Exception ex){
            logger.warn("", ex);
        }
        return "";
    }

    private String checkAndSendTokenBalance(EthAddress coinBaseEthAddress, EthAddress ethAddress, String coinName, String contractAddress, double balance){
        try {
            ETHHelper helper = ethService.getRpcClient(coinName);
            BigInteger gasPrice = BigInteger.valueOf(22000000000L);
            BigInteger gasLimit = BigInteger.valueOf(120000);
            return helper.contractTransferEx(ethAddress.getCredentials(), ethAddress.getPassword(), contractAddress, coinBaseEthAddress.getAddress(), balance, gasPrice, gasLimit);
        } catch (Exception ex){
            logger.warn("", ex);
        }
        return "";
    }

    private void gatherOneToken(Coin coin) {
        logger.info("begin gatherOneEth " + ReflectionToStringBuilder.toString(coin));

        String coinBaseAddress = coin.getCoinBase();
        if(coinBaseAddress == null || coinBaseAddress.trim().length() == 0) {
            logger.info("eth coin base address not found");
            return;
        }
        coinBaseAddress = coinBaseAddress.trim();

        String contractAddress = coin.getContractAddress();
        if (contractAddress == null || contractAddress.trim().length() ==0){
            logger.info("eth contract address not found");
            return;
        }
        contractAddress = contractAddress.trim();

        CoinConfig coinConfig = coinConfigMapper.getCoinConfigByName(coin.getName());
        if (coinConfig==null || coinConfig.getGatherEnable()!=1){
            logger.info("coin config is null or gather enable close");
            return;
        }

        // 获取用户余额超过某个金额的时候进行回收
        BigDecimal gatherMinBalance = coinConfig.getGatherMin();
        logger.info(coinConfig.getCoinName()+ " gatherMinBalance=" + gatherMinBalance);
        if (gatherMinBalance==null || gatherMinBalance.compareTo(BigDecimal.ZERO)<=0){
            logger.info("gatherMinBalance <= 0");
            return;
        }
        BigDecimal gatherBaseBalance = coinConfig.getGatherBase();
        logger.info(coinConfig.getCoinName()+ " gatherBaseBalance=" + gatherBaseBalance);
        if (gatherBaseBalance==null || gatherBaseBalance.compareTo(BigDecimal.ZERO)<=0){
            logger.info("gatherMinBalance <= 0");
            return;
        }

        // 验证地址，要去地址里面必须有钱，此方法最验证最准确
        Web3j web3 = Web3j.build(new HttpService("http://" + coin.getServerAddress() + ":" + coin.getServerPort()));
        if(checkAndGetBalance(web3, coinBaseAddress, new BigDecimal("0.1")).compareTo(BigDecimal.ZERO) == 0) {
            logger.info("coinBaseAddress not valid.it must has 0.1 eth,coinBaseAddress=" + coinBaseAddress);
            return;
        }

        EthAddress coinBaseEthAddress = ethService.getEthAddressByAddress(coinBaseAddress);
        if (coinBaseEthAddress==null){
            logger.info("coinBaseAddress not valid.it is not exist platform,coinBaseAddress=" + coinBaseAddress);
            return;
        }

        logger.info("coinBaseAddress=" + coinBaseAddress);
        HashSet<String> sendAddress = new HashSet<String>();
        sendAddress.add(coinBaseAddress);

        // 获取所有以太坊地址，如果地址的币大于指定数量，那么收集起来
        int lastId = configService.getConfigValue(ConfigKey.GATHER_LAST_RECV_ID_PRE + coin.getName(), 0);
        int beginLastId = lastId;
        int count = 0;
        logger.info("begin analyze last id=" + lastId);

        while(true) {
            ReceivedCoin receivedCoin = receivedCoinMapper.getNextReceiveFromExternal(lastId, coin.getName());
            if(receivedCoin == null) {
                break;
            }
            lastId = receivedCoin.getId();

            //如果已经统计过的过滤
            boolean bexist = false;
            for (String k:sendAddress){
                if (k.equalsIgnoreCase(receivedCoin.getAddress())){
                    bexist = true;
                    break;
                }
            }
            if (bexist){
                logger.warn("address already check. address:" + receivedCoin.getAddress());
                continue;
            }

            EthAddress ethAddress = ethService.getEthAddressByAddress(receivedCoin.getAddress());
            if(ethAddress == null) {
                logger.warn("do not find eth address:" + receivedCoin.getAddress());
                continue;
            }

            //获取代币余额
            BigDecimal tokenBalance = checkAndGetTokenBalance(coinBaseEthAddress, ethAddress, receivedCoin.getCoinName(), contractAddress, gatherMinBalance);
            if(tokenBalance.equals(BigDecimal.ZERO)) {
                continue;
            }

            //对应的地址上必须有0.005个ETH才能转币
            BigDecimal ethBalance = checkAndGetBalance(web3, receivedCoin.getAddress(), gatherBaseBalance);
            if (!ethBalance.equals(BigDecimal.ZERO)) {
                continue;
            }
            String hash = checkAndSendEthBalance(coinBaseEthAddress, ethAddress, coin.getName(), gatherBaseBalance.doubleValue());
            if (hash==null || hash.length()<10){
                logger.info("ETH:------>,error, transfer eth,"+hash+ "," + gatherBaseBalance+","+ethAddress.getAddress());
                continue;
            }

            gatherLogMapper.add(new GatherLog().setFromAddress(coinBaseAddress).setToAddress(ethAddress.getAddress()).setCoinName("ETH").setTxid(hash).setType(1).setAmount(gatherBaseBalance));
            sendAddress.add(ethAddress.getAddress());

            // 测试，休眠30秒避免造成过大压力
            count++;
            try {
                Thread.sleep(1000 * 60);
            }catch (Exception e){ ; }
        }
        logger.info("gather eth count=" + count + ",beginLastId=" + beginLastId + ",now lastId=" + lastId);

        lastId = beginLastId;
        sendAddress.clear();
        sendAddress.add(coinBaseAddress);
        count=0;
        while(true) {
            ReceivedCoin receivedCoin = receivedCoinMapper.getNextReceiveFromExternal(lastId, coin.getName());
            if(receivedCoin == null) {
                break;
            }
            lastId = receivedCoin.getId();

            //如果已经统计过的过滤
            boolean bexist = false;
            for (String k:sendAddress){
                if (k.equalsIgnoreCase(receivedCoin.getAddress())){
                    bexist = true;
                    break;
                }
            }
            if (bexist){
                logger.warn("address already check. address:" + receivedCoin.getAddress());
                configService.setConfigValue(ConfigKey.GATHER_LAST_RECV_ID_PRE + coin.getName(), Integer.toString(lastId));
                continue;
            }

            EthAddress ethAddress = ethService.getEthAddressByAddress(receivedCoin.getAddress());
            if(ethAddress == null) {
                logger.warn("do not find eth address:" + receivedCoin.getAddress());
                configService.setConfigValue(ConfigKey.GATHER_LAST_RECV_ID_PRE + coin.getName(), Integer.toString(lastId));
                continue;
            }

            //获取代币余额
            BigDecimal tokenBalance = checkAndGetTokenBalance(coinBaseEthAddress, ethAddress, receivedCoin.getCoinName(), contractAddress, gatherMinBalance);
            if(tokenBalance.equals(BigDecimal.ZERO)) {
                configService.setConfigValue(ConfigKey.GATHER_LAST_RECV_ID_PRE + coin.getName(), Integer.toString(lastId));
                continue;
            }

            //检查并转账代币
            String hash = checkAndSendTokenBalance(coinBaseEthAddress, ethAddress, coin.getName(), contractAddress, tokenBalance.doubleValue());
            if (hash==null || hash.length()<10){
                logger.error("TOKEN:----->error, transfer token "+coin.getName()+","+ethAddress.getAddress()+","+tokenBalance);
                continue;
            }
            sendAddress.add(ethAddress.getAddress());
            gatherLogMapper.add(new GatherLog().setFromAddress(ethAddress.getAddress()).setToAddress(coinBaseEthAddress.getAddress()).setCoinName(coin.getName()).setTxid(hash).setType(1).setAmount(tokenBalance));
            configService.setConfigValue(ConfigKey.GATHER_LAST_RECV_ID_PRE + coin.getName(), Integer.toString(lastId));

            // 测试，休眠30秒避免造成过大压力
            count++;
            try {
                Thread.sleep(1000 * 60);
            } catch (Exception e) { e.printStackTrace();  }
        }
        logger.info("gather token count=" + count + ",beginLastId=" + beginLastId + ",now lastId=" + lastId);
    }
}
