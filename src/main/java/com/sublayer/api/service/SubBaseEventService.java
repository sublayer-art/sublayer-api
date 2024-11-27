package com.sublayer.api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sublayer.api.domain.vo.EventValuesExt;
import com.sublayer.api.entity.SubTxOrder;
import com.sublayer.api.service.impl.BaseService;
import com.sublayer.api.utils.DappWeb3jUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Event;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubBaseEventService {
    @Autowired
    private BaseService baseService ;

    List<EventValuesExt> sortLog(List<EventValuesExt> list) {
        if (null == list) {
            return new ArrayList<>();
        }
        Collections.sort(list, new Comparator<EventValuesExt>() {
            public int compare(EventValuesExt val1, EventValuesExt val2) {
                return val1.getBlockNumber().compareTo(val2.getBlockNumber());
            }
        });
        return list;
    }

    List<EventValuesExt> filterValues(List<EventValuesExt> valueList, List<String> txList) {
        if(null == valueList) {
            return new ArrayList<>();
        }
        return valueList.stream().filter(value->!txList.contains(value.getTxHash())).collect(Collectors.toList());
    }

    List<String> getTxHashList(Set<String> hashList) {
        QueryWrapper<SubTxOrder> wrapper = new QueryWrapper<>();
        wrapper.in(SubTxOrder.TX_HASH, hashList);
        List<SubTxOrder> txList = this.baseService.findByCondition(SubTxOrder.class, wrapper);
        return txList.stream().map(SubTxOrder::getTxHash).collect(Collectors.toList());
    }

    List<EventValuesExt> getEventListByTopic(String filterTopic, String topic, Event event, List<EthLog.LogResult> allLogs, Map<BigInteger, EthBlock.Block> blockMap){
        List<EthLog.LogResult> eventLogs = allLogs
                .stream()
                .filter(log-> ((Log) (log.get())).getTopics().contains(filterTopic))
                .collect(Collectors.toList());
        return this.parseEvent(eventLogs, topic, event, blockMap);
    }

    List<EventValuesExt> getEventList(String address, String topic, Event event, List<EthLog.LogResult> allLogs, Map<BigInteger, EthBlock.Block> blockMap) {
        String addressLowerCase = address.toLowerCase();
        List<EthLog.LogResult> eventLogs = allLogs
                .stream()
                .filter(log->addressLowerCase.equals(((Log) (log.get())).getAddress()))
                .collect(Collectors.toList());
        return this.parseEvent(eventLogs, topic, event, blockMap);
    }

    List<EventValuesExt> getEventList(List<String> addressList, String topic, Event event, List<EthLog.LogResult> allLogs, Map<BigInteger, EthBlock.Block> blockMap) {
        Set<String> addressSet = addressList.stream().map(a -> a = a.toLowerCase()).collect(Collectors.toSet());
        List<EthLog.LogResult> eventLogs = allLogs
                .stream()
                .filter(log->addressSet.contains(((Log) (log.get())).getAddress()))
                .collect(Collectors.toList());
        return this.parseEvent(eventLogs, topic, event, blockMap);
    }

    List<EventValuesExt> parseEvent(List<EthLog.LogResult> eventLogs, String topic, Event event, Map<BigInteger, EthBlock.Block> blockMap){
        if (eventLogs.isEmpty()) {
            return new ArrayList<EventValuesExt>();
        }
        List<EventValuesExt> list = DappWeb3jUtil.decodeLog(eventLogs, topic, event, blockMap);
        if (list.isEmpty()) {
            return new ArrayList<EventValuesExt>();
        }
        Set<String> TxHashSet = list.stream().map(log-> log.getTxHash()).collect(Collectors.toSet());

        List<String> txList = this.getTxHashList(TxHashSet);

        List<EventValuesExt> eventList = this.sortLog(this.filterValues(list,txList));
        return eventList;
    }

    List<String> filterInvalidContract(List<String> contracts) {
        Iterator<String> it = contracts.iterator();
        String contract = null;
        while(it.hasNext()) {
            contract = it.next();
            if(StringUtils.isEmpty(contract)) {
                it.remove();
            }else if(!DappWeb3jUtil.isValidAddress(contract)){
                it.remove();
            }
        }
        return contracts;
    }
}
