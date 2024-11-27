package com.sublayer.api.service;

import com.sublayer.api.domain.vo.EventValuesExt;
import com.sublayer.api.manager.SubContractNftManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthLog;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class SubNftEventService extends SubBaseEventService{
    @Autowired
    SubErc721EventService subErc721EventService;

    @Autowired
    SubContractService subContractService;

    public List<EventValuesExt> processEvent(List<EthLog.LogResult> allLogs, Map<BigInteger, EthBlock.Block> blockMap) throws Exception{
        List<String> addressList = this.getAllAddr();
        addressList = this.filterInvalidContract(addressList);
        List<EventValuesExt> list = subErc721EventService.processTransferEvent(addressList, allLogs, blockMap);
        return list;
    }

    private List<String> getAllAddr() {
        List<String> list = subContractService.findAllAddress();
        return list;
    }
}
