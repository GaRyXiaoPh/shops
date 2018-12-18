package com.cmd.wallet.blockchain.eth;

import com.cmd.wallet.common.exception.ServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.response.NoOpProcessor;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public class ETHHelper {
    private static Logger logger = LoggerFactory.getLogger(ETHHelper.class);

    private static final BigInteger gasLimit = Convert.toWei(BigDecimal.valueOf(900000), Convert.Unit.WEI).toBigInteger();

    private String keystore = "./wallet";

    private String coinName;

    private Web3j web3;

    public ETHHelper(String client, String name) {
        this.web3 = Web3j.build(new HttpService(client));
        this.coinName = name;
    }

    public String getCoinName(){
        return coinName;
    }

    //根据秘钥文件名获操作钱包
    //创建钱包
    public String createWallet(String password) {
        File destinationDirectory = new File(keystore);
        try {
            return WalletUtils.generateNewWalletFile(password, destinationDirectory, true);
        } catch (Exception e) {
           throw new ServerException("创建以太坊钱包错误，错误原因：" + e.getMessage(), e);
        }
    }

    //获取凭证
    public Credentials getCredentials(String fileName, String password) {
        try {
            return WalletUtils.loadCredentials(password, keystore + "/" + fileName);
        } catch (Exception e) {
            throw new ServerException("加载以太坊钱包错误，错误原因：" + e.getMessage(), e);
        }
    }

    public int contractDecimal(String contractAddress) {
        try {
            Credentials credentials = Credentials.create("d3e9d64f6c244156b77c58bb4dc7a766006453ab6d470175c9b952edf19d11bc");
            ETHCoin ethCoin = new ETHCoin(contractAddress, web3, credentials, new BigInteger("1000"), new BigInteger("10000"));
            return ethCoin.decimals().send().intValue();
        } catch (Exception e) {
            throw new ServerException("以太坊智能合约decimal错误，错误原因：" + e.getMessage(), e);
        }
    }

    //以太坊智能合约获取余额
    public BigDecimal contractBalance(String fileName, String password, String contractAddress) {
        try {
            Credentials credentials = this.getCredentials(fileName, password);
            ETHCoin ethCoin = ETHCoin.load(contractAddress,
                    web3,
                    new FastRawTransactionManager(web3, credentials, new NoOpProcessor(web3)),
                    Contract.GAS_PRICE,
                    Contract.GAS_LIMIT);
            BigInteger balance  = ethCoin.balanceOf(credentials.getAddress()).send();
            int decimal = ethCoin.decimals().send().intValue();
            if (decimal<=0) throw new Exception("decimal<=0");

            return BigDecimal.valueOf(balance.doubleValue()).divide(BigDecimal.TEN.pow(decimal));
        } catch (Exception e) {
            throw new ServerException("以太坊代币获取余额错误，错误原因："+e.getMessage(), e);
        }
    }

    // 以太坊只能合约转账
    public String contractTransfer(String fileName, String password, String contractAddress, String to, double value)  {
        try {
            Credentials credentials = this.getCredentials(fileName, password);
            ETHCoin ethCoin = ETHCoin.load(contractAddress,
                    web3,
                    new FastRawTransactionManager(web3, credentials, new NoOpProcessor(web3)),
                    Contract.GAS_PRICE,
                    Contract.GAS_LIMIT);

            int decimal = ethCoin.decimals().send().intValue();
            if (decimal<=0) throw new Exception("decimal<=0");

            TransactionReceipt receipt = ethCoin.transfer(to, BigDecimal.valueOf(value).multiply(BigDecimal.TEN.pow(decimal)).toBigInteger()).send();
            return receipt.getTransactionHash();
        } catch (Exception e) {
            throw new ServerException("以太坊智能合约转账错误，错误原因：" + e.getMessage(), e);
        }
    }

    // 以太坊只能合约转账,指定price, limit
    public String contractTransfer(String fileName, String password, String contractAddress, String to, double value, BigInteger gasPrice, BigInteger gasLimit)  {
        try {
            Credentials credentials = this.getCredentials(fileName, password);

            ETHCoin ethCoin = ETHCoin.load(contractAddress,
                    web3,
                    new FastRawTransactionManager(web3, credentials, new NoOpProcessor(web3)),
                    gasPrice,
                    gasLimit);
            int decimal = ethCoin.decimals().send().intValue();
            if (decimal<=0) throw new Exception("decimal<=0");

            TransactionReceipt receipt = ethCoin.transfer(to, BigDecimal.valueOf(value).multiply(BigDecimal.TEN.pow(decimal)).toBigInteger()).send();
            return receipt.getTransactionHash();
        } catch (Exception e) {
            throw new ServerException("以太坊智能合约转账错误，错误原因：" + e.getMessage(), e);
        }
    }

    //获取ETH余额
    public BigDecimal getBalance(String fileName, String password) {
        try {
            Credentials credentials = this.getCredentials(fileName, password);
            EthGetBalance getBalance = web3.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
            return Convert.fromWei(getBalance.getBalance().toString(), Convert.Unit.ETHER);
        } catch (Exception e) {
            throw new ServerException("获取以太坊余额错误，错误原因：" + e.getMessage(), e);
        }
    }

    //转账ETH
    public String transfer(String fileName, String password, String to, double value)  {
        try {
            Credentials credentials = this.getCredentials(fileName, password);

            Transfer transfer = new Transfer(web3, new FastRawTransactionManager(web3, credentials, new NoOpProcessor(web3)));
            TransactionReceipt receipt = transfer.sendFunds(to, BigDecimal.valueOf(value), Convert.Unit.ETHER).send();

            return receipt.getTransactionHash();
        } catch (Exception e) {
            throw new ServerException("以太坊转账错误，错误原因：" + e.getMessage(), e);
        }
    }

    //转账ETH,指定price, limit
    public String transfer(String fileName, String password, String to, double value, BigInteger gasPrice, BigInteger gasLimit) {
        try {
            Credentials credentials = this.getCredentials(fileName, password);

            Transfer transfer = new Transfer(web3, new FastRawTransactionManager(web3, credentials, new NoOpProcessor(web3)));
            TransactionReceipt receipt = transfer.sendFunds(to, BigDecimal.valueOf(value), Convert.Unit.ETHER, gasPrice, gasLimit).send();

            return receipt.getTransactionHash();
        } catch (Exception e) {
            throw new ServerException("以太坊转账错误，错误原因：" + e.getMessage(), e);
        }
    }


    //--------------------------------------------------------------------------------------
    //根据秘钥内容操作钱包
    public String createWalletEx(String password) {
        File destinationDirectory = new File(keystore);
        try {
            String fileName = WalletUtils.generateNewWalletFile(password, destinationDirectory, true);
            if (fileName!=null) {
                File file = new File(keystore + "/" + fileName);
                Long length = file.length();
                byte[] filecontent = new byte[length.intValue()];
                FileInputStream in = new FileInputStream(file);
                in.read(filecontent);
                in.close();
                return new String(filecontent);
            }
            return null;
        } catch (Exception e) {
            throw new ServerException("创建以太坊钱包错误，错误原因：" + e.getMessage(), e);
        }
    }
    public String getCredentialsByFileName(String fileName){
        try {
            if (fileName!=null) {
                File file = new File(keystore + "/" + fileName);
                Long length = file.length();
                byte[] filecontent = new byte[length.intValue()];
                FileInputStream in = new FileInputStream(file);
                in.read(filecontent);
                in.close();
                return new String(filecontent);
            }
            return null;
        } catch (Exception e) {
            throw new ServerException("获取以太坊钱包错误，错误原因：" + e.getMessage(), e);
        }
    }

    //以太坊智能合约获取余额
    public BigDecimal contractBalanceEx(String content, String password, String contractAddress) {
        try {
            Credentials credentials = EthUtils.loadCredentialsByContect(password, content);;
            ETHCoin ethCoin = ETHCoin.load(contractAddress,
                    web3,
                    new FastRawTransactionManager(web3, credentials, new NoOpProcessor(web3)),
                    Contract.GAS_PRICE,
                    Contract.GAS_LIMIT);
            BigInteger balance  = ethCoin.balanceOf(credentials.getAddress()).send();
            int decimal = ethCoin.decimals().send().intValue();
            if (decimal<=0) throw new Exception("decimal<=0");

            logger.warn(""+credentials.getAddress()+":"+balance);
            return BigDecimal.valueOf(balance.doubleValue()).divide(BigDecimal.TEN.pow(decimal));
        } catch (Exception e) {
            throw new ServerException("以太坊代币获取余额错误，错误原因："+e.getMessage(), e);
        }
    }

    // 以太坊只能合约转账
    public String contractTransferEx(String content, String password, String contractAddress, String to, double value) {
        try {
            BigInteger gasPrice = web3.ethGasPrice().send().getGasPrice();
            Credentials credentials = EthUtils.loadCredentialsByContect(password, content);
            ETHCoin ethCoin = ETHCoin.load(contractAddress,
                    web3,
                    new FastRawTransactionManager(web3, credentials, new NoOpProcessor(web3)),
                    //BigInteger.valueOf(45000000000L),
                    Contract.GAS_PRICE,
                    Contract.GAS_LIMIT);

            int decimal = ethCoin.decimals().send().intValue();
            if (decimal<=0) throw new Exception("decimal<=0");

            logger.warn("From:"+credentials.getAddress()+",To:"+to+",value:"+value+",decimal:"+decimal+",gasPrice:"+gasPrice);
            TransactionReceipt receipt = ethCoin.transfer(to, BigDecimal.valueOf(value).multiply(BigDecimal.TEN.pow(decimal)).toBigInteger()).send();
            return receipt.getTransactionHash();
        } catch (Exception e) {
            logger.error("To:"+to+",value:"+value+"以太坊智能合约转账错误，错误原因：" + e.getMessage(), e);
            if (e.getMessage().equalsIgnoreCase("timeout")){
                return "timeout";
            }
            throw new ServerException("以太坊智能合约转账错误，错误原因：" + e.getMessage(), e);
        }
    }

    public int contractDecimalEx(String content, String password, String contractAddress){
        try {
            Credentials credentials = EthUtils.loadCredentialsByContect(password, content);
            ETHCoin ethCoin = new ETHCoin(contractAddress, web3, credentials, new BigInteger("1000"), new BigInteger("10000"));
            return ethCoin.decimals().send().intValue();
        }catch (Exception e){
            throw new ServerException("以太坊智能合约decimal错误，错误原因：" + e.getMessage(), e);
        }
    }

    // 以太坊只能合约转账指定price,limit
    public String contractTransferEx(String content, String password, String contractAddress, String to, double value, BigInteger gasPrice, BigInteger gasLimit) {
        try {
            Credentials credentials = EthUtils.loadCredentialsByContect(password, content);
            ETHCoin ethCoin = ETHCoin.load(contractAddress,
                    web3,
                    new FastRawTransactionManager(web3, credentials, new NoOpProcessor(web3)),
                    gasPrice,
                    gasLimit);
            int decimal = ethCoin.decimals().send().intValue();
            if (decimal<=0) throw new Exception("decimal<=0");

            TransactionReceipt receipt = ethCoin.transfer(to, BigDecimal.valueOf(value).multiply(BigDecimal.TEN.pow(decimal)).toBigInteger()).send();
            return receipt.getTransactionHash();
        } catch (Exception e) {
            logger.error("To:"+to+",value:"+value+"以太坊智能合约转账错误，错误原因：" + e.getMessage(), e);
            throw new ServerException("以太坊智能合约转账错误，错误原因：" + e.getMessage(), e);
        }
    }

    //获取ETH余额
    public BigDecimal getBalanceEx(String content, String password) {
        try {
            Credentials credentials = EthUtils.loadCredentialsByContect(password, content);;
            EthGetBalance getBalance = web3.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
            return Convert.fromWei(getBalance.getBalance().toString(), Convert.Unit.ETHER);
        } catch (Exception e) {
            throw new ServerException("获取以太坊余额错误，错误原因：" + e.getMessage(), e);
        }
    }

    //转账ETH
    public String transferEx(String content, String password, String to, double value) {
        try {
            Credentials credentials = EthUtils.loadCredentialsByContect(password, content);

            Transfer transfer = new Transfer(web3, new FastRawTransactionManager(web3, credentials, new NoOpProcessor(web3)));
            TransactionReceipt receipt = transfer.sendFunds(to, BigDecimal.valueOf(value), Convert.Unit.ETHER).send();

            return receipt.getTransactionHash();
        } catch (Exception e) {
            throw new ServerException("以太坊转账错误，错误原因：" + e.getMessage(), e);
        }
    }

    //转账ETH指定price,limit
    public String transferEx(String content, String password, String to, double value, BigInteger gasPrice, BigInteger gasLimit) {
        try {
            Credentials credentials = EthUtils.loadCredentialsByContect(password, content);

            Transfer transfer = new Transfer(web3, new FastRawTransactionManager(web3, credentials, new NoOpProcessor(web3)));
            TransactionReceipt receipt = transfer.sendFunds(to, BigDecimal.valueOf(value), Convert.Unit.ETHER, gasPrice, gasLimit).send();
            return receipt.getTransactionHash();
        } catch (Exception e) {
            throw new ServerException("以太坊转账错误，错误原因：" + e.getMessage(), e);
        }
    }


    //------------------------------------------------------------------------------------------------
    //判断地址是否有效
    public boolean isValidAddress(String address) {
        return WalletUtils.isValidAddress(address);
    }

    //转换
    public BigInteger toBigInteger(double v, int decimal){
        return BigDecimal.valueOf(v).multiply(BigDecimal.TEN.pow(decimal)).toBigInteger();
    }
    public BigDecimal toBigDecimal(BigInteger v, int decimal){
        return BigDecimal.valueOf(v.doubleValue()).divide(BigDecimal.TEN.pow(decimal));
    }

    //获取交易
    public Transaction getTransaction(String transactionHash) {
        try {
            return web3.ethGetTransactionByHash(transactionHash).send().getResult();
        } catch (Exception e) {
            throw new ServerException("获取以太坊交易错误，错误原因：" + e.getMessage(), e);
        }
    }

    public TransactionReceipt getTransactionReceipt(String transactionHash) {
        try {
             Optional<TransactionReceipt> optional = web3.ethGetTransactionReceipt(transactionHash).send().getTransactionReceipt();
            return optional.orElse(null);
        } catch (Exception e) {
            throw new ServerException("获取以太坊交易错误，错误原因：" + e.getMessage(), e);
        }
    }

    public EthBlock.Block getLatestBlock() {
        try {
            return web3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true).send().getBlock();
        } catch (Exception e) {
            throw new ServerException("获取以太坊区块错误，错误原因：" + e.getMessage(), e);
        }
    }

    public EthBlock.Block getBlock(BigInteger blockNumber) {
        try {
            return web3.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), true).send().getBlock();
        } catch (Exception e) {
            throw new ServerException("获取以太坊区块错误，错误原因：" + e.getMessage(), e);
        }
    }

    public BigInteger getBlockNumber(){
        try {
            return web3.ethBlockNumber().send().getBlockNumber();
        } catch (Exception e) {
            throw new ServerException("获取以太坊区块数，错误原因：" + e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws Exception {
        ETHHelper ethHelper = new ETHHelper("http://127.0.0.1:8331", "eth");
        ethHelper.keystore = "C://";
        String password = "123456";
        String fileName = "UTC--2018-09-12T14-36-37.86000000Z--76541fef12de9d814c5b0bcf06d94f8d24a92051.json";//ethHelper.createWallet(password);
        Credentials credentials = ethHelper.getCredentials(fileName, password);
        ethHelper.contractTransfer(fileName, password, "0xc83a0e4bc765bc78d7054118e1aee342f26fa81a", "0xbbf881099fa26dbc12673adcafe9550f19729026", 1);
        System.out.println("fileName:" + fileName + ", address:" + credentials.getAddress());

        /*ETHHelper ethHelper = new ETHHelper("http://127.0.0.1:8331", "eth");
        BigInteger num = ethHelper.getBlockNumber();


        EthBlock.Block blk = ethHelper.getBlock(BigInteger.valueOf(4000000));
        System.out.println(blk.getHash());

        List< EthBlock.TransactionResult > list = blk.getTransactions();
        for (int j=0; j<list.size(); j++){
            Transaction transaction = (Transaction)list.get(j);
            TransactionReceipt receipt = ethHelper.getTransactionReceipt(transaction.getHash());
            int tmp = 0;
        }*/
    }
}
