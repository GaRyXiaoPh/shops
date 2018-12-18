package com.cmd.wallet.service;

import com.cmd.wallet.blockchain.bitcoin.JSON;
import com.cmd.wallet.blockchain.eth.ETHCoin;
import com.cmd.wallet.blockchain.eth.ETHHelper;
import com.cmd.wallet.common.constants.CoinCategory;
import com.cmd.wallet.common.constants.ConfigKey;
import com.cmd.wallet.common.constants.ErrorCode;
import com.cmd.wallet.common.mapper.EthAddressMapper;
import com.cmd.wallet.common.model.Coin;
import com.cmd.wallet.common.model.EthAddress;
import com.cmd.wallet.common.utils.Assert;
import com.cmd.wallet.common.utils.RandomUtil;
import com.cmd.wallet.common.vo.UserCoinVO;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

@Service
public class EthService {
    private static Logger logger = LoggerFactory.getLogger(EthService.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserCoinService userCoinService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private EthAddressMapper ethAddressMapper;
    @Autowired
    private ReceivedCoinService receivedCoinService;

    private static int txConfirmCount = 12;



    //////////////////////////////////////////////////////////////////////////////
    //同步所有以太坊系列的交易
    public void syncNewTransactions() {
        // 根据主机和端口进行分开同步
        List<Coin> coins = coinService.getAllEthCoins();
        List<String> completeCoins = new ArrayList<String>();
        for(Coin coin : coins) {
            if(coin.getServerAddress() == null || coin.getServerAddress().trim().length() == 0) {
                continue;
            }
            String idStr = coin.getServerAddress().trim() + ":" + coin.getServerPort();
            if(completeCoins.contains(idStr)) {
                continue;
            }
            try {
                syncNewTransactions(coin.getServerAddress(), coin.getServerPort());
            }catch (Exception ex) {
                logger.error("", ex);
            }
            completeCoins.add(idStr);
        }
    }

    private void updateEthDecimal(String host, int port, Coin coin) throws Exception {
        Web3j web3 = Web3j.build(new HttpService("http://" + host + ":" + port));
        Credentials credentials = Credentials.create("d3e9d64f6c244156b77c58bb4dc7a766006453ab6d470175c9b952edf19d11bc");
        ETHCoin ethCoin = new ETHCoin(coin.getContractAddress(), web3, credentials, new BigInteger("1000"), new BigInteger("10000"));
        int decimals = ethCoin.decimals().send().intValue();
        coin.setDecimals(decimals);
        logger.info("coin " + coin.getName() + " decimals=" + decimals);
    }

    //同步所有指定服务器和端口的以太坊币的新交易
    public int syncNewTransactions(String host, int port) throws Exception {
        logger.info("begin syncNewTransactions(eth), host=" + host + ",port=" + port);
        List<Coin> coins = coinService.getAllEthCoins();
        if(coins == null || coins.size() == 0) {
            logger.warn("do not find any eth coins");
            return 0;
        }
        HashMap<String, Coin> coinMap = new HashMap<String, Coin>();
        Coin ethCoin = null;
        for (Coin coin: coins) {
            // 这只合约地址，简化后面的判断
            if(CoinCategory.TOKEN.equals(coin.getCategory())) {
                if(coin.getContractAddress() != null && coin.getContractAddress().trim().length() > 16 && coin.getServerAddress() != null) {
                    if(host.equals(coin.getServerAddress().trim()) && port == coin.getServerPort()) {
                        coinMap.put(coin.getContractAddress().trim().toLowerCase(), coin);
                        updateEthDecimal(host, port, coin);
                    }
                }
            } else {
                if(ethCoin != null) {
                    logger.error("find duplicate eth coin");
                }
                ethCoin = coin;
            }
        }
        if(coinMap.size() == 0) {
            logger.warn("do not find any match coins, host=" + host + ",port=" + port);
        }
        logger.info("sync eth coins:" + JSON.stringify(coinMap) + ",eth=" + ReflectionToStringBuilder.toString(ethCoin != null ? ethCoin : "none"));
        Web3j web3 = Web3j.build(new HttpService("http://" + host + ":" + port));

        // 获取上次同步到的区块高度，从上次同步到的高度+1开始同步
        String syncKey = ConfigKey.BC_LAST_SYNC_ETH_PRE + host + ":" + port;
        int blockNum = web3.ethBlockNumber().send().getBlockNumber().intValue();
        // 如果没有值，从昨天开始同步
        int lastSyncBlock = configService.getConfigValue(syncKey, blockNum > 2000 ? blockNum - 2000 : 0);
        int curBlock = lastSyncBlock + 1;
        int toBlock = blockNum - txConfirmCount;
        logger.info("begin sync,from=" + curBlock + ",toBlock=" + toBlock + ",host=" + host + ",port=" + port);
        int count = 0;
        while(curBlock < toBlock) {
            try {
                syncBlock(ethCoin, coinMap, web3, curBlock);
                count++;
                curBlock++;
                if(count >= 1000) {
                    logger.warn("syn much block,break,count:" + count);
                    break;
                }
            } catch (Exception ex) {
                logger.error("sync eth block failed:" + ex.getMessage(), ex);
                break;
            }
        }
        configService.setConfigValue(syncKey, Integer.toString(curBlock - 1));
        logger.info("end syncNewTransactions,now lastSyncBlock=" + (curBlock - 1)  + ",host=" + host + ",port=" + port + ",sync count=" + count);
        return count;
    }

    /**
     * 同步一个区块的交易
     * @param ethCoin     币种
     * @param coinMap     智能合约的币
     * @param web3     客户端对象
     * @param blockNumber  同步的区块号
     */
    private void syncBlock(Coin ethCoin, HashMap<String, Coin> coinMap, Web3j web3, int blockNumber) throws IOException {
        EthBlock.Block block = web3.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)), true).send().getBlock();
        List< EthBlock.TransactionResult > list = block.getTransactions();
        for (int i=0; i<list.size(); i++) {
            Transaction transaction = (Transaction)list.get(i);
            syncTransaction(ethCoin, coinMap, web3, transaction, block);
        }
    }

    private void syncTransaction(Coin ethCoin, HashMap<String, Coin> coinMap, Web3j web3, Transaction transaction, EthBlock.Block block) throws IOException {
        if (BigInteger.valueOf(0).equals(transaction.getValue()) && !"0x".equals(transaction.getInput().toString())) {
            syncTokenTransaction(coinMap, web3, transaction, block);
        } else {
            if(ethCoin == null) return;
            if(transaction.getTo() == null || !CoinCategory.ETH.equals(ethCoin.getCategory())) {
                return;
            }
            // 判断是否是平台地址
            Integer userId = userCoinService.getUserIdByCoinNameAndAddress(ethCoin.getName(), transaction.getTo());
            if (userId==null) return;
            // 检查数据通过
            logger.info("find valid transaction,coin=" + ethCoin.getName() + ", content=" + ReflectionToStringBuilder.toString(transaction));
            // 如果是内部转账，属于收集币的行为，忽略
            Integer sendUserId = userCoinService.getUserIdByCoinNameAndAddress(ethCoin.getName(), transaction.getFrom());
            if(sendUserId != null) {
                logger.info("inner transfer,ignore,tx=" + transaction.getHash());
                return;
            }

            // 如果交易已经存在，那么忽略
            if(receivedCoinService.isTransactionExist(ethCoin.getName(), transaction.getHash())) {
                logger.info("transaction:" + transaction.getHash() + " already exists");
                return;
            }
            // 增加交易，直接成功
            BigDecimal value = new BigDecimal(transaction.getValue());
            value = value.divide(new BigDecimal("1000000000000000000"), 8, RoundingMode.HALF_UP);
            BigDecimal fee = BigDecimal.ZERO;
            if(ethCoin.getReceivedFee() < 1 && ethCoin.getReceivedFee() > 0) {
                fee = value.multiply(new BigDecimal(Float.toString(ethCoin.getReceivedFee()))).setScale(8, RoundingMode.HALF_UP);
            }
            receivedCoinService.addTransaction(userId, ethCoin.getName(), transaction.getTo(), transaction.getHash(), value, fee, block.getTimestamp().intValue(), 1, transaction.getFrom());
        }
    }

    private void syncTokenTransaction(HashMap<String, Coin> coinMap, Web3j web3, Transaction transaction, EthBlock.Block block) throws IOException {
        if(transaction.getTo() == null) {
            return;
        }
        Coin coin = coinMap.get(transaction.getTo().toLowerCase());
        if(coin == null) {
            return;
        }
        // 处理 "a9059cbb": "transfer(address,uint256)",和 "23b872dd": "transferFrom(address,address,uint256)"这2个函数
        String input = transaction.getInput();
        if(!input.startsWith("0xa9059cbb") && !input.startsWith("0x23b872dd")) {
            return;
        }
        Optional<TransactionReceipt> optional = web3.ethGetTransactionReceipt(transaction.getHash()).send().getTransactionReceipt();
        TransactionReceipt receipt = optional.orElse(null);
        if(receipt == null) {
            logger.warn("get receipt failed:" + ReflectionToStringBuilder.toString(transaction));
            return;
        }
        if(receipt.getGasUsed().compareTo(transaction.getGas()) >= 0) {
            logger.debug("transaction failed:" + ReflectionToStringBuilder.toString(transaction) + ",receipt:" + ReflectionToStringBuilder.toString(receipt));
            return;
        }
        if (receipt.getContractAddress()!=null)
            return;
        for (int j=0 ; j<receipt.getLogs().size(); j++) {
            org.web3j.protocol.core.methods.response.Log ethLog = receipt.getLogs().get(j);
            if (ethLog.getTopics().size() != 3) {
                continue;
            }
            String data = ethLog.getData().substring(2);
            if (data.length() == 0) {
                continue;
            }
            BigDecimal value = new BigDecimal(new BigInteger(data, 16));
            String from = ethLog.getTopics().get(1);
            from = "0x" + from.substring(from.length() - 40);
            String to = ethLog.getTopics().get(2);
            to = "0x" + to.substring(to.length() - 40);

            // 判断是否是平台地址
            Integer userId = userCoinService.getUserIdByCoinNameAndAddress(coin.getName(), to);
            if (userId == null) return;
            // 检查数据通过
            logger.info("find valid transaction,coin=" + coin.getName() + ", content=" + ReflectionToStringBuilder.toString(transaction));
            // 如果交易已经存在，那么忽略
            if(receivedCoinService.isTransactionExist(coin.getName(), transaction.getHash())) {
                logger.info("transaction:" + transaction.getHash() + " already exists");
                return;
            }
            // 增加交易，直接成功
            value = value.divide(new BigDecimal("10").pow(coin.getDecimals()), 8, RoundingMode.HALF_UP);
            BigDecimal fee = BigDecimal.ZERO;
            if(coin.getReceivedFee() < 1 && coin.getReceivedFee() > 0) {
                fee = value.multiply(new BigDecimal(Float.toString(coin.getReceivedFee()))).setScale(8, RoundingMode.HALF_UP);
            }

            receivedCoinService.addTransaction(userId, coin.getName(), to, transaction.getHash(), value, fee, block.getTimestamp().intValue(), 1, transaction.getFrom());
            break;
        }
    }



    //////////////////////////////////////////////////////////////////////////////
    public ETHHelper getRpcClient(String coinName){
        Coin coin = coinService.getCoinByName(coinName);
        Assert.check(coin==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);
        Assert.check(!CoinCategory.ETH.equals(coin.getCategory()) && !CoinCategory.TOKEN.equals(coin.getCategory()), ErrorCode.ERR_PARAM_ERROR);

        String url = "http://" + coin.getServerAddress() + ":" + coin.getServerPort();
        return new ETHHelper(url, coinName);
    }

    public String getAccountAddress(int userId, String coinName){
        //代币和ETH公用一个地址
        EthAddress ethAddress = ethAddressMapper.getEthAddressByUserId(userId);
        if (ethAddress==null){
            ETHHelper client = this.getRpcClient(coinName);
            if (client!=null){
                String pwd = RandomUtil.getCode(10);
                String filename = client.createWallet(pwd);
                String credentials = client.getCredentialsByFileName(filename);
                String address = client.getCredentials(filename, pwd).getAddress();
                if (address != null){
                    ethAddressMapper.add(new EthAddress().setUserId(userId).setAddress(address).setPassword(pwd).setFileName(filename).setCredentials(credentials));
                }
            }
        }
        EthAddress ethAddr = userCoinService.getEthAddressByUserId(userId);
        if (ethAddr!=null){
            UserCoinVO userCoin = userCoinService.getUserCoinByUserIdAndCoinName(userId, coinName);
            if (userCoin==null){
                userCoinService.addUserCoin(userId, coinName, ethAddr.getAddress());
            } else{
                if (userCoin.getBindAddress()==null || userCoin.getBindAddress().trim().length()==0) {
                    userCoinService.updateUserCoinAddress(userId, coinName, ethAddr.getAddress());
                }
            }
            return ethAddr.getAddress();
        }
        return null;
    }

    public double getAccountBalance(int userId, String coinName){
        Coin coin = coinService.getCoinByName(coinName);
        Assert.check(coin==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);

        EthAddress ethAddress = ethAddressMapper.getEthAddressByUserId(userId);
        Assert.check(ethAddress==null, ErrorCode.ERR_COIN_BASE_ERROR);

        ETHHelper client = this.getRpcClient(coinName);
        Assert.check(client==null, ErrorCode.ERR_ETH_CLIENT_RPC_ERROR);

        if (CoinCategory.ETH.equals(coin.getCategory())) {
            return client.getBalanceEx(ethAddress.getCredentials(), ethAddress.getPassword()).doubleValue();
        }else if(CoinCategory.TOKEN.equals(coin.getCategory())){
            return client.contractBalanceEx(ethAddress.getCredentials(), ethAddress.getPassword(), coin.getContractAddress()).doubleValue();
        }
        return 0;
    }

    //发送以太币/以太坊代币到指定地址（使用基本账户发送）
    public String sendToAddress(int userId, String coinName, String toAddress, double amount){
        EthAddress ethAddress = ethAddressMapper.getEthAddressByUserId(userId);
        //Assert.check(ethAddress==null, ErrorCode.ERR_INVALID_ADDRESS);

        return sendToAddress(ethAddress, coinName, toAddress, amount);
    }

    //获取交易确认数
    public int getTxConfirmCount(String coinName, String txid){
        Coin coin = coinService.getCoinByName(coinName);
        Assert.check(coin==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);

        ETHHelper client = this.getRpcClient(coinName);
        Assert.check(client==null, ErrorCode.ERR_ETH_CLIENT_RPC_ERROR);

        BigInteger blockNumber = client.getBlockNumber();
        BigInteger txBlock = client.getTransaction(txid).getBlockNumber();
        Assert.check(txBlock==null || txBlock.intValue()<=0, ErrorCode.ERR_INVALID_TXID);

        return blockNumber.subtract(txBlock).intValue();
    }

    // 发送以太币/以太坊代币到指定地址（使用指定地址发送发送, ethAddress==null则主账户发送）
    public String sendToAddress(EthAddress ethAddress, String coinName, String toAddress, double amount){
        Coin coin = coinService.getCoinByName(coinName);
        Assert.check(coin==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);

        ETHHelper client = this.getRpcClient(coinName);
        Assert.check(client==null, ErrorCode.ERR_ETH_CLIENT_RPC_ERROR);
        Assert.check(!client.isValidAddress(toAddress), ErrorCode.ERR_INVALID_ADDRESS);

        String coinbase = coin.getCoinBase();
        Assert.check(coinbase==null ||coinbase.length()<=0, ErrorCode.ERR_COIN_BASE_ERROR);

        if(ethAddress == null) {
            ethAddress = ethAddressMapper.getEthAddressByAddress(coinbase);
            Assert.check(ethAddress==null, ErrorCode.ERR_COIN_BASE_ERROR);
        }

        if (CoinCategory.ETH.equals(coin.getCategory())) {
            //ETH发送
            return client.transferEx(ethAddress.getCredentials(), ethAddress.getPassword(), toAddress, amount);
        }else if(CoinCategory.TOKEN.equals(coin.getCategory())) {
            //代币发送
            BigDecimal surplus = client.contractBalanceEx(ethAddress.getCredentials(), ethAddress.getPassword(), coin.getContractAddress());
            logger.info("total:"+surplus+","+amount);
            if (surplus.doubleValue()-amount<10) {
                Assert.check(true, ErrorCode.ERR_COIN_BASE_ERROR);
            }else {
                return client.contractTransferEx(ethAddress.getCredentials(), ethAddress.getPassword(), coin.getContractAddress(), toAddress, amount);
            }
        }
        return null;
    }

    public EthAddress getEthAddressByAddress(String address) {
        return ethAddressMapper.getEthAddressByAddress(address);
    }

    public boolean isAddressValid(String coinName, String address) {
        Coin coin = coinService.getCoinByName(coinName);
        Assert.check(coin==null, ErrorCode.ERR_DB_CONFIG_NOT_EXIST);

        ETHHelper client = this.getRpcClient(coinName);
        Assert.check(client==null, ErrorCode.ERR_ETH_CLIENT_RPC_ERROR);
        return client.isValidAddress(address);
    }
}
