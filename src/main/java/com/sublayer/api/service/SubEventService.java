package com.sublayer.api.service;

import com.sublayer.api.constants.Constants;
import com.sublayer.api.domain.vo.EventValuesExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthLog;

import java.math.BigInteger;
import java.util.*;

@Service
public class SubEventService {
    @Autowired
    SubExchangeEventService subExchangeEventService;

    @Autowired
    SubNftEventService subNftEventService;

    @Autowired
    SubTxOrderService subTxOrderService;

    @Autowired
    SubSystemService subSystemService;


    @Transactional(rollbackFor = Exception.class)
    public void process(List<EthLog.LogResult> allLogs, Map<BigInteger, EthBlock.Block> blockMap) throws Exception {
        List<EventValuesExt> list = this.processEvent(allLogs, blockMap);
        this.processTime(blockMap);
        this.saveTxLogList(list);
        this.saveBlockInfo(blockMap);
    }

    private void processTime(Map<BigInteger, EthBlock.Block> blockMap) {
        //获得blockMap中最大的key
        Set<BigInteger> bigIntegers = blockMap.keySet();
        Object[] objects = bigIntegers.toArray();
        Arrays.sort(objects);
        BigInteger maxBlock = new BigInteger(objects[objects.length - 1].toString());
        Long time = blockMap.get(maxBlock).getTimestamp().longValue();
    }

    private void saveTxLogList(List<EventValuesExt> list) {
        Map<String, EventValuesExt> map = new HashMap<>();
        for (EventValuesExt valuesExt : list) {
            map.put(valuesExt.getTxHash(), valuesExt);
        }
        list = new ArrayList<>(map.values());
        subTxOrderService.saveBatch(list);
    }

    private void saveBlockInfo(Map<BigInteger, EthBlock.Block> blockMap) {
        if (blockMap.isEmpty()) {
            return;
        }

        Iterator<BigInteger> it = blockMap.keySet().iterator();
        BigInteger last = BigInteger.ZERO;
        BigInteger tmp = null;
        while (it.hasNext()) {
            tmp = it.next();
            if (last.compareTo(tmp) < 0) {
                last = tmp;
            }
        }
        subSystemService.update(Constants.LAST_BLOCK, last.toString());
    }


    /**
     * 回调过程
     *
     * @param allLogs 事件日志
     * @throws Exception
     */
    private List<EventValuesExt> processEvent(List<EthLog.LogResult> allLogs, Map<BigInteger, EthBlock.Block> blockMap) throws Exception {
        // 处理交易日志
        List<EventValuesExt> list = subExchangeEventService.processEvent(allLogs, blockMap);
        // 处理nft日志
        List<EventValuesExt> list1 = subNftEventService.processEvent(allLogs, blockMap);
        list.addAll(list1);
        return list;
    }
}
