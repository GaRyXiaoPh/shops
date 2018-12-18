package com.cmd.wallet.api.task;

import com.cmd.wallet.blockchain.usdt.OmniClient;
import com.cmd.wallet.common.constants.CoinCategory;
import com.cmd.wallet.common.constants.ConfigKey;
import com.cmd.wallet.common.model.Coin;
import com.cmd.wallet.common.model.ReceivedCoin;
import com.cmd.wallet.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.List;

/**
 *  将以太坊上各个地址的钱汇聚到一个目的地址
 */
@Component
public class GatherUsdtTask {
    private Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private ConfigService configService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private UsdtService usdtService;
    @Autowired
    private ReceivedCoinService receivedCoinService;

    private Thread gatherThread;

    @Scheduled(cron = "0 0 5 * * ?")
    public synchronized void gatherUsdtTimer() {
        if(gatherThread != null && gatherThread.isAlive()) {
            log.error("gatherUsdtTimer ia alive, cannot start new work");
            return;
        }
        gatherThread = new Thread("gatherUsdtTimer"){
            public void run() {
                gatherUsdtThread();
            }
        };
        gatherThread.start();
    }
    public void gatherUsdtThread() {
        try {
            log.info("begin gatherUsdtTimer");
            gatherUsdt();
            log.info("end gatherUsdtTimer");
        } catch (Exception ex) {
            log.error("", ex);
        }
    }

    /**
     * 检查资产是否足够，不够的时候直接返回0
     * @param client
     * @param address
     * @param ignoreBalance  可用余额小于这个金额将会忽略，直接返回0
     * @return  余额大于指定金额将会返回余额， 否则返回0
     */
    private double checkAndGetBalance(OmniClient client, String address, double ignoreBalance) {
        try {
            double balance = client.omni_getBalance(address);
            if(balance >= ignoreBalance) {
                return balance;
            }
        } catch (Exception ex) {
            log.warn("", ex);
        }
        return 0;
    }


    private void gatherUsdt() throws Exception {
        // 获取汇聚的地址
        List<Coin> usdtCoins = coinService.getCoinsByCategory(CoinCategory.USDT);
        if(usdtCoins.size() == 0) {
            log.info("coin usdt not found");
            return;
        }
        Coin coin = usdtCoins.get(0);
        String coinBaseAddress = coin.getCoinBase();
        if(coinBaseAddress == null || coinBaseAddress.trim().length() == 0) {
            log.info("usdt coin base address not found");
            return;
        }
        coinBaseAddress = coinBaseAddress.trim();
        // 获取用户余额超过某个金额的时候进行回收
        String gatherMinBalanceStr = configService.getConfigValue(ConfigKey.USDT_GATHER_MIN_BALANCE, "150");
        double gatherMinBalance = Double.parseDouble(gatherMinBalanceStr);
        log.info("gatherMinBalance=" + gatherMinBalance);

        HashSet<String> sendAddress = new HashSet<String>();
        // 验证地址，要去地址里面必须有钱，此方法最验证最准确
        String url = "http://" + coin.getServerUser() + ':' + coin.getServerPassword() + "@" + coin.getServerAddress() + ":" + coin.getServerPort() + "/";
        OmniClient client = new OmniClient(url, 31);
        if(checkAndGetBalance(client, coinBaseAddress, 1) < 1) {
            log.info("coinBaseAddress not valid.it must has 1 usdt,coinBaseAddress=" + coinBaseAddress);
            return;
        }
        log.info("coinBaseAddress=" + coinBaseAddress);
        sendAddress.add(coinBaseAddress);

        // 获取所有以太坊地址，如果地址的币大于指定数量，那么收集起来
        int lastId = configService.getConfigValue(ConfigKey.GATHER_LAST_RECV_ID_PRE + coin.getName(), 0);
        int beginLastId = lastId;
        int count = 0;
        log.info("begin analyze last id=" + lastId);
        double totalUsdt = 0;
        while(true) {
            ReceivedCoin receivedCoin = receivedCoinService.getNextReceiveFromExternal(lastId, coin.getName());
            if(receivedCoin == null) {
                break;
            }
            lastId = receivedCoin.getId();
            if(sendAddress.contains(receivedCoin.getAddress())) {
                configService.setConfigValue(ConfigKey.GATHER_LAST_RECV_ID_PRE + coin.getName(), Integer.toString(lastId));
                log.warn("address already check. address:" + receivedCoin.getAddress());
                continue;
            }
            double balance = checkAndGetBalance(client, receivedCoin.getAddress(), gatherMinBalance);
            if(balance < gatherMinBalance) {
                continue;
            }
            log.info("begin transfer usdt from " + receivedCoin.getAddress() + ",to " + coinBaseAddress + ",value=" + balance);
            // 先把0.01btc转入这个账号，然后全部把usdt转出
            double btcValue = 0.001;
            String hash = client.sendToAddress(receivedCoin.getAddress(), btcValue);
            if(hash.length() < 16) {
                log.error("send btc failed");
                throw new RuntimeException("send btc failed");
            }
            log.info("send btc to address:" + receivedCoin.getAddress() + ",hash=" + hash + ",value=" + btcValue);
            // 休眠5秒再继续
            try {
                Thread.sleep(1000 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 发送所有的usdt到收集方
            hash = client.omni_send(receivedCoin.getAddress(), coinBaseAddress, balance);
            if(hash.length() < 16) {
                log.error("send usdt failed");
                throw new RuntimeException("send usdt failed");
            }
            //String hash = null;
            sendAddress.add(receivedCoin.getAddress());
            log.info("end transfer usdt from " + receivedCoin.getAddress() + ",to " + coinBaseAddress + ",value=" + balance + ",hash=" + hash);
            totalUsdt = totalUsdt + balance;
            count++;
            configService.setConfigValue(ConfigKey.GATHER_LAST_RECV_ID_PRE + coin.getName(), Integer.toString(lastId));
            // 测试，休眠30秒避免造成过大压力
            try {
                Thread.sleep(1000 * 30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("gather usdt count=" + count + ",total usdt=" + totalUsdt + ",beginLastId=" + beginLastId + ",now lastId=" + lastId);
        // 转0.02个btc到主账号
        if(totalUsdt > 0) {
            String hash = client.sendToAddress(coinBaseAddress, 0.02);
        }
    }
}
