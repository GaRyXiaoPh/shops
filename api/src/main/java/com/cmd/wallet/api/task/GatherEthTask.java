package com.cmd.wallet.api.task;

import com.cmd.wallet.common.constants.CoinCategory;
import com.cmd.wallet.common.constants.ConfigKey;
import com.cmd.wallet.common.mapper.CoinConfigMapper;
import com.cmd.wallet.common.model.*;
import com.cmd.wallet.service.*;
import com.cmd.wallet.common.constants.CoinCategory;
import com.cmd.wallet.common.model.Coin;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  将以太坊上各个地址的钱汇聚到一个目的地址
 */
@Component
public class GatherEthTask {
    private Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private ConfigService configService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private EthService ethService;
    @Autowired
    private ReceivedCoinService receivedCoinService;
    @Autowired
    private CoinConfigMapper coinConfigMapper;

    private Thread gatherThread;

    @Scheduled(cron = "0 30 3 * * ?")
    public synchronized void gatherEthTimer() {
        if(gatherThread != null && gatherThread.isAlive()) {
            logger.error("gatherEthTimer ia alive, cannot start new work");
            return;
        }
        gatherThread = new Thread("gatherEthTimer"){
            public void run() {
                gatherEthThread();
            }
        };
        gatherThread.start();
    }
    public void gatherEthThread() {
        try {
            logger.info("begin gatherEthTimer");
            gatherEth();
            logger.info("end gatherEthTimer");
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    /**
     * 检查资产是否足够，不够的时候直接返回0
     * @param web3
     * @param address
     * @param ignoreBalance  可用余额小于这个金额将会忽略，直接返回0
     * @return  余额大于指定金额将会返回余额， 否则返回0
     */
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

    private void gatherEth() {
        // 获取汇聚的地址
        List<Coin> ethCoins = coinService.getCoinsByCategory(CoinCategory.ETH);
        if(ethCoins.size() == 0) {
            logger.info("coin eth not found");
            return;
        }
        for(Coin coin : ethCoins) {
            try {
                gatherOneEth(coin);
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
    }


    private void gatherOneEth(Coin coin) {
        String coinBaseAddress = coin.getCoinBase();
        if(coinBaseAddress == null || coinBaseAddress.trim().length() == 0) {
            logger.info("eth coin base address not found");
            return;
        }
        logger.info("begin gatherOneEth " + ReflectionToStringBuilder.toString(coin));
        coinBaseAddress = coinBaseAddress.trim();

        CoinConfig coinConfig = coinConfigMapper.getCoinConfigByName(coin.getName());
        if (coinConfig==null || coinConfig.getGatherEnable()!=1){
            logger.info("coin config is null or gather enable close");
            return;
        }

        // 获取用户余额超过某个金额的时候进行回收
        BigDecimal gatherMinBalance = coinConfig.getGatherMin();
        logger.info("gatherMinBalance=" + gatherMinBalance);
        // 获取用户余额需要保留的余额
        BigDecimal gatherRemainBalance = new BigDecimal(0.01);
        logger.info("gatherRemainBalance=" + gatherRemainBalance);
        if(gatherMinBalance.compareTo(gatherRemainBalance) <= 0) {
            logger.info("gatherMinBalance <= gatherRemainBalance, can not work");
            return;
        }

        HashSet<String> sendAddress = new HashSet<String>();
        // 验证地址，要去地址里面必须有钱，此方法最验证最准确
        Web3j web3 = Web3j.build(new HttpService("http://" + coin.getServerAddress() + ":" + coin.getServerPort()));
        if(checkAndGetBalance(web3, coinBaseAddress, new BigDecimal("0.01")).compareTo(BigDecimal.ZERO) == 0) {
            logger.info("coinBaseAddress not valid.it must has 0.01 eth,coinBaseAddress=" + coinBaseAddress);
            return;
        }
        logger.info("coinBaseAddress=" + coinBaseAddress);
        sendAddress.add(coinBaseAddress);

        // 获取所有以太坊地址，如果地址的币大于指定数量，那么收集起来
        int lastId = configService.getConfigValue(ConfigKey.GATHER_LAST_RECV_ID_PRE + coin.getName(), 0);
        int beginLastId = lastId;
        int count = 0;
        logger.info("begin analyze last id=" + lastId);
        BigDecimal totalEth = new BigDecimal("0");
        while(true) {
            ReceivedCoin receivedCoin = receivedCoinService.getNextReceiveFromExternal(lastId, coin.getName());
            if(receivedCoin == null) {
                break;
            }
            lastId = receivedCoin.getId();
            if(sendAddress.contains(receivedCoin.getAddress())) {
                configService.setConfigValue(ConfigKey.GATHER_LAST_RECV_ID_PRE + coin.getName(), Integer.toString(lastId));
                logger.warn("address already check. address:" + receivedCoin.getAddress());
                continue;
            }
            BigDecimal balance = checkAndGetBalance(web3, receivedCoin.getAddress(), gatherMinBalance);
            if(balance.equals(BigDecimal.ZERO)) {
                configService.setConfigValue(ConfigKey.GATHER_LAST_RECV_ID_PRE + coin.getName(), Integer.toString(lastId));
                continue;
            }
            BigDecimal transfer = balance.subtract(gatherRemainBalance);
            logger.info("begin transfer from " + receivedCoin.getAddress() + ",to " + coinBaseAddress + ",value=" + transfer);
            EthAddress ethAddress = ethService.getEthAddressByAddress(receivedCoin.getAddress()) ;
            if(ethAddress == null) {
                logger.warn("do not find eth address:" + receivedCoin.getAddress());
                configService.setConfigValue(ConfigKey.GATHER_LAST_RECV_ID_PRE + coin.getName(), Integer.toString(lastId));
                continue;
            }
            //String hash = null;
            sendAddress.add(receivedCoin.getAddress());
            String hash = ethService.sendToAddress(ethAddress, coin.getName(), coinBaseAddress, transfer.doubleValue());
            logger.info("end transfer from " + receivedCoin.getAddress() + ",to " + coinBaseAddress + ",value=" + transfer + ",hash=" + hash);
            totalEth = totalEth.add(transfer);
            count++;
            configService.setConfigValue(ConfigKey.GATHER_LAST_RECV_ID_PRE + coin.getName(), Integer.toString(lastId));

            // 测试，休眠30秒避免造成过大压力
            try {
                Thread.sleep(1000 * 30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("gather eth count=" + count + ",total eth=" + totalEth + ",beginLastId=" + beginLastId + ",now lastId=" + lastId);
    }
}
