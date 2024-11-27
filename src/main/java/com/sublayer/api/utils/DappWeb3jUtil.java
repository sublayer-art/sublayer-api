package com.sublayer.api.utils;

import com.alibaba.fastjson2.JSONObject;
import com.sublayer.api.domain.vo.ERCTokenInfo;
import com.sublayer.api.domain.vo.EventValuesExt;
import com.sublayer.api.storage.IpfsStorage;
import com.sublayer.api.utils.contract.ERC1155;
import com.sublayer.api.utils.contract.ERC20;
import com.sublayer.api.utils.contract.ERC721;
import com.sublayer.api.utils.contract.Royalties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.EventValues;
import org.web3j.abi.datatypes.Event;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.BatchRequest;
import org.web3j.protocol.core.BatchResponse;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.Contract;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class DappWeb3jUtil {

    private static final Logger logger = LoggerFactory.getLogger(DappWeb3jUtil.class);

    public static final String SUPPORT_ROYALTIES_CODE = "0xb7799584";

    private static Web3j web3j;

    public static void initWeb3j(String url) {
        web3j = Web3j.build(new HttpService(url));
    }

    public static ERCTokenInfo getErc20Info(String address) throws Exception {
        TransactionManager transactionManager = new ClientTransactionManager(web3j, address);
        ERC20 erc20 = ERC20.load(address, web3j, transactionManager, new DefaultGasProvider());
        ERCTokenInfo info = new ERCTokenInfo();
        String symbol = erc20.symbol().sendAsync().get();
        info.setContractSymbol(symbol);
        String name = erc20.name().sendAsync().get();
        info.setContractName(name);
        BigInteger deceimals = erc20.decimals().sendAsync().get();
        info.setContractDecimals(deceimals.intValue());
        return info;
    }

    public static String getErc721Uri(String token, String tokenId) {
        TransactionManager transactionManager = new ReadonlyTransactionManager(web3j, token);
        ERC721 contract721 = ERC721.load(token, web3j, transactionManager, new DefaultGasProvider());
        try {
            return contract721.tokenURI(new BigInteger(tokenId)).sendAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Fetch ERC721 token uri error", e);
            return null;
        }
    }

    public static String getErc1155Uri(String token, String tokenId) {
        TransactionManager transactionManager = new ReadonlyTransactionManager(web3j, token);
        ERC1155 contract1155 = ERC1155.load(token, web3j, transactionManager, new DefaultGasProvider());

        try {
            return contract1155.uri(new BigInteger(tokenId)).sendAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Fetch ERC1155 token uri error", e);
            return null;
        }
    }

    public static String getName(String token) {
        TransactionManager transactionManager = new ReadonlyTransactionManager(web3j, token);
        ERC721 contract721 = ERC721.load(token, web3j, transactionManager, new DefaultGasProvider());
        try {
            return contract721.name().sendAsync().get();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getSymbol(String token) {
        TransactionManager transactionManager = new ReadonlyTransactionManager(web3j, token);
        ERC721 contract721 = ERC721.load(token, web3j, transactionManager, new DefaultGasProvider());
        try {
            return contract721.symbol().sendAsync().get();
        } catch (Exception e) {
            return null;
        }
    }

    public static ERCTokenInfo processUri(String uri) throws IOException {
        if (null != uri) {
            ERCTokenInfo info = new ERCTokenInfo();
            info.setUri(uri);
            if (uri.toLowerCase().startsWith("ipfs:/")) {
                String[] arr = uri.split("/");
                uri = arr[arr.length - 1];
                if (uri.length() == 46) {
                    String tokenInfoStr = null;
                    try {
                        tokenInfoStr = IpfsStorage.getIpfsData(uri);
                    } catch (Exception e) {
                        logger.error("Fetch ipfs info error", e);
                        return null;
                    }
                    if (null != tokenInfoStr) {
                        info.setContent(tokenInfoStr);
                        try {
                            JSONObject obj = JSONObject.parseObject(tokenInfoStr);
                            info.setProperties(obj.getString("attributes"));
                            info.setName(obj.getString("name"));
                            info.setDescription(obj.getString("description"));
                        } catch (Exception e) {
                            logger.info("None json data from ipfs =>" + uri);
                        }
                    }
                }
            } else if (uri.toLowerCase().startsWith("http")) {
                String content = HttpUtils.post(uri);
                if (StringUtils.isEmpty(content)) {
                    info.setContent("");
                } else {
                    info.setContent(content);
                    try {
                        JSONObject obj = JSONObject.parseObject(content);
                        info.setProperties(obj.getString("attributes"));
                        info.setName(obj.getString("name"));
                        info.setDescription(obj.getString("description"));
                    } catch (Exception e) {
                        logger.info("None json data from ipfs =>" + uri);
                    }
                }
            }
            return info;
        }
        return null;
    }

    /**
     * @param token
     * @param tokenId
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<BigInteger> getRoyalties(String token, String tokenId) {
        TransactionManager transactionManager = new ReadonlyTransactionManager(web3j, token);
        Royalties royalties = Royalties.load(token, web3j, transactionManager, new DefaultGasProvider());
        try {
            return royalties.getFeeBps(new BigInteger(tokenId)).sendAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Fetch royalties error", e);
            return null;
        }
    }

    public static Boolean isValidAddress(String address){
        return WalletUtils.isValidAddress(address);
    }

    /**
     * @param token
     * @return
     */
    public static Boolean isSupportRoyalties(String token) {
        TransactionManager transactionManager = new ReadonlyTransactionManager(web3j, token);
        Royalties royalties = Royalties.load(token, web3j, transactionManager, new DefaultGasProvider());
        try {
            return royalties.supportsInterface(Numeric.hexStringToByteArray(SUPPORT_ROYALTIES_CODE)).sendAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Fetch royalties error", e);
            return false;
        }
    }

    public static ERCTokenInfo processNftUri(String uri) throws IOException {
        if (null != uri) {
            ERCTokenInfo info = new ERCTokenInfo();
            info.setUri(uri);
            if (uri.toLowerCase().startsWith("ipfs:/")) {
                String[] arr = uri.split("/");
                uri = arr[arr.length - 1];
                if (uri.length() == 46) {
                    String tokenInfoStr = null;
                    try {
                        tokenInfoStr = IpfsStorage.getIpfsData(uri);
                    } catch (Exception e) {
                        logger.error("Fetch ipfs info error", e);
                        return null;
                    }
                    if (null != tokenInfoStr) {
                        info.setContent(tokenInfoStr);
                        try {
                            JSONObject obj = JSONObject.parseObject(tokenInfoStr);
                            info.setProperties(obj.getString("attributes"));
                            info.setName(obj.getString("name"));
                            info.setDescription(obj.getString("description"));
                        } catch (Exception e) {
                            logger.info("None json data from ipfs =>" + uri);
                        }
                    }
                }
            } else if (uri.toLowerCase().startsWith("http")) {
                String content = HttpUtils.post(uri);
                if (StringUtils.isEmpty(content)) {
                    info.setContent("");
                } else {
                    info.setContent(content);
                    try {
                        JSONObject obj = JSONObject.parseObject(content);
                        info.setProperties(obj.getString("attributes"));
                        info.setName(obj.getString("name"));
                        info.setDescription(obj.getString("description"));
                    } catch (Exception e) {
                        logger.info("None json data from ipfs =>" + uri);
                    }
                }
            }
            return info;
        }
        return null;
    }

    public static List<EventValuesExt> decodeLog(List<EthLog.LogResult> logList, String topic, Event event, Map<BigInteger, EthBlock.Block> blockMap) {
        List<EventValuesExt> list = new ArrayList<>();
        if (null != logList && !logList.isEmpty()) {
            logList.stream().forEach(log-> {
                if(((Log) log.get()).getTopics().contains(topic)) {
                    list.add(decodeLog(log, event, blockMap));
                }
            });
        }
        return list;
    }

    private static EventValuesExt decodeLog(EthLog.LogResult<Log> logResult, Event event, Map<BigInteger, EthBlock.Block> blockMap) {
        EventValues eventValues = Contract.staticExtractEventParameters(event, logResult.get());
        EthBlock.Block block = blockMap.get(logResult.get().getBlockNumber());
        EventValuesExt val = new EventValuesExt(eventValues, logResult.get().getTransactionHash(), logResult.get().getAddress(),
                logResult.get().getBlockNumber(), block.getTimestamp());
        return val;
    }

    public static List<EthBlock.Block> getBlockList(BigInteger start, BigInteger end) throws Exception{
        return DappWeb3jUtil.getBlockList(start, end, false);
    }

    public static List<EthBlock.Block> getBlockList(BigInteger start, BigInteger end, Boolean full) throws Exception{
        BatchRequest batchRequest = web3j.newBatch();
        Integer _start = Integer.valueOf(start.toString());
        Integer _end = Integer.valueOf(end.toString());
        for(int i = _start; i < _end + 1; i++){
            DefaultBlockParameter param = DefaultBlockParameter.valueOf(BigInteger.valueOf(i));
            batchRequest.add(web3j.ethGetBlockByNumber(param, full));
        }
        List<EthBlock.Block> blockList;
        try {
            BatchResponse batchResponse = batchRequest.send();
            List<EthBlock> responseList = (List<EthBlock>) batchResponse.getResponses();
            if(responseList.size() == 1){
                EthBlock block = responseList.get(0);
                if(block.hasError()){
                    throw new Exception(block.getError().getMessage());
                }
            }
            blockList = responseList.stream().map(r -> r.getBlock())
                    .sorted(Comparator.comparing(b -> b.getNumber().longValue())).collect(Collectors.toList());
        }catch (Exception e){
            throw new Exception(e);
        }
        return blockList;
    }

    public static EthLog getEthLogs(BigInteger start, BigInteger end) throws InterruptedException, ExecutionException, IOException {
        EthFilter filter = new EthFilter(new DefaultBlockParameterNumber(start), new DefaultBlockParameterNumber(end),
                new ArrayList<>());
        EthLog log = web3j.ethGetLogs(filter).send();
        return log;
    }

    public static EthLog getEthLogs(BigInteger start, BigInteger end, List<String> address) throws InterruptedException, ExecutionException, IOException {
        EthFilter filter = new EthFilter(new DefaultBlockParameterNumber(start), new DefaultBlockParameterNumber(end),
                address);
        EthLog log = web3j.ethGetLogs(filter).send();
        return log;
    }

    public static BigInteger getLastBlock() throws IOException {
        if(null == web3j){
            throw new RuntimeException("web3j is null");
        }
        EthBlockNumber ebn = web3j.ethBlockNumber().send();
        if(ebn.hasError()) {
            throw new RuntimeException("get block number error");
        } else {
            return ebn.getBlockNumber();
        }
    }
}
