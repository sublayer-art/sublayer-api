package com.sublayer.api.task;

import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.dto.ConfigContract;
import com.sublayer.api.domain.vo.AllLogsVo;
import com.sublayer.api.entity.SubTxOrder;
import com.sublayer.api.service.*;
import com.sublayer.api.utils.DappWeb3jUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@EnableAsync
public class ScheduleTask {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleTask.class);

    public static Boolean isProcessing = false;

    @Autowired
    SubEventService subEventService;

    @Autowired
    private SubSystemService subSystemService;

    @Autowired
    SubTxOrderService subTxOrderService;

    @Autowired
    SubGasTrackerService subGasTrackerService;

    @Autowired
    SubContractService subContractService;

    @Scheduled(cron = "*/6 * * * * ?")
    private void startProcessEvent() {
        if (isProcessing.booleanValue()) {
            logger.info("task is in processing status");
            return;
        }
        synchronized (isProcessing) {
            if (isProcessing.booleanValue()) {
                logger.info("task is in processing status");
                return;
            }
            isProcessing = true;
        }
        logger.info("task starting");
        try {
            String startStr = subSystemService.getKeyValue(Constants.LAST_BLOCK);
            if (null == startStr) {
                throw new Exception("have no config last_block");
            }
            BigInteger start = new BigInteger(startStr);

            String blockConfirmation = subSystemService.getKeyValue(Constants.BLOCK_CONFIRMATION);
            BigInteger end = DappWeb3jUtil.getLastBlock().subtract(new BigInteger(blockConfirmation));
            start = start.add(BigInteger.ONE);

            if (start.compareTo(end) > 0) {
                return;
            }
            Long MaxBlockOneTime = Long.valueOf(subSystemService.getKeyValue(Constants.MAX_BLOCK_ONE_TIME));
            if (end.subtract(start).longValue() > MaxBlockOneTime) {
                end = start.add(BigInteger.valueOf(MaxBlockOneTime));
            }

            logger.info("From: " + start + ", to: " + end);
            AllLogsVo allLogsVo = this.getEthLogs(start, end);
            List<EthLog.LogResult> allLogs = allLogsVo.getAllLogs();
            end = allLogsVo.getEnd();
            allLogs = this.filterExistedLog(allLogs, start, end);
            Map<BigInteger, EthBlock.Block> blockMap = this.getBlockInfo(start, end);

            subEventService.process(allLogs, blockMap);

        } catch (Exception e) {
            logger.error("Task error", e);
        } finally {
            isProcessing = false;
        }
    }

    @Scheduled(cron = "*/60 * * * * ?")
    private void startGasTracker() {
        try {
            BigInteger lastBlock = DappWeb3jUtil.getLastBlock();
            BigInteger start = lastBlock.subtract(new BigInteger("3"));
            if(start.compareTo(new BigInteger("1")) < 0){
                start = new BigInteger("1");
            }
            List<EthBlock.Block> blockList = DappWeb3jUtil.getBlockList(start, lastBlock, true);
            List<BigInteger> gasPriceList = new ArrayList<>();
            for(EthBlock.Block block: blockList){
                List<EthBlock.TransactionResult> transactionHashList = block.getTransactions();
                for(EthBlock.TransactionResult transactionResult : transactionHashList){
                    gasPriceList.add(((EthBlock.TransactionObject)transactionResult).getGasPrice());
                }
            }
            Collections.sort(gasPriceList);

            subGasTrackerService.getGasTracker(gasPriceList, lastBlock);
        }catch (Exception e){
            logger.error("gasTracker task error", e);
        }
    }

    private AllLogsVo getEthLogs(BigInteger start, BigInteger end) throws Exception {
        @SuppressWarnings("rawtypes")
        List<String> list = subContractService.findAllAddress();
        ConfigContract configContract = subSystemService.getConfigContract();
        String exchangeAddress = configContract.getNftExchangeAddress();
        list.add(exchangeAddress);

        EthLog log = DappWeb3jUtil.getEthLogs(start, end, list);

        //EthLog log = DappWeb3jUtil.getEthLogs(start, end);
        if (log.hasError()) {
            if(log.getError().getCode() == -32005){
                BigInteger size = end.subtract(start);
                if (start.equals(end)) {
                    throw new RuntimeException("Get logs error");
                }
                size = size.divide(new BigInteger("2"));
                BigInteger _end = start.add(size);
                return this.getEthLogs(start, _end);
            }else {
                throw new RuntimeException(String.valueOf(log.getError().getMessage()));
            }
        }
        List<EthLog.LogResult> allLogs = log.getLogs();
        AllLogsVo allLogsVo = new AllLogsVo(allLogs, end);
        return allLogsVo;
    }

    private Map<BigInteger, EthBlock.Block> getBlockInfo(BigInteger start, BigInteger end) throws Exception {
        List<EthBlock.Block> blockList = DappWeb3jUtil.getBlockList(start, end);
        Map<BigInteger, EthBlock.Block> map = new HashMap<>();
        for (EthBlock.Block block : blockList) {
            map.put(block.getNumber(), block);
        }
        return map;
    }

    private List<EthLog.LogResult> filterExistedLog(List<EthLog.LogResult> allLogs, BigInteger start, BigInteger end) {
        List<SubTxOrder> txOrderList = subTxOrderService.getList(Integer.valueOf(start.toString()), Integer.valueOf(end.toString()));
        Set<String> hashSet = txOrderList.stream().map(SubTxOrder::getTxHash).collect(Collectors.toSet());
        return allLogs
                .stream()
                .filter(log -> !hashSet.contains(((Log) (log.get())).getTransactionHash()))
                .collect(Collectors.toList());
    }
}
