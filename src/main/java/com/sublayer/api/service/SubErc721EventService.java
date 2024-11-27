package com.sublayer.api.service;

import com.sublayer.api.domain.dto.TransferLog;
import com.sublayer.api.domain.vo.EventValuesExt;
import com.sublayer.api.manager.SubContractNftManager;
import com.sublayer.api.utils.DappEventUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthLog;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class SubErc721EventService extends SubBaseEventService{
    private static final Logger logger = LoggerFactory.getLogger(SubErc721EventService.class);

    @Autowired
    SubContractNftManager contractNftManager;

    /**
     * @param addressList
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public List<EventValuesExt> processTransferEvent(
            List<String> addressList,
            List<EthLog.LogResult> allLogs,
            Map<BigInteger, EthBlock.Block> blockMap
    ) throws Exception {
        //根据事件的合约地址与topic过滤logs
        List<EventValuesExt> valueList = this.getEventList(addressList, DappEventUtils.TRANSFER_TOPIC, DappEventUtils.TRANSFER_EVENT, allLogs, blockMap);
        if (valueList.isEmpty()) {
            return valueList;
        }
        for(EventValuesExt value: valueList){
            this.processTransferEvent(value);
        }
        return valueList;
    }

    /**
     * @param eventValues
     * @throws Exception
     */
    private void processTransferEvent(EventValuesExt eventValues) throws Exception {
        logger.info("721 transfer event");
        String from = (String) eventValues.getIndexedValues().get(0).getValue();
        String to = (String) eventValues.getIndexedValues().get(1).getValue();
        BigInteger tokenId = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
        TransferLog log = new TransferLog(eventValues.getAddress(), tokenId, from, to, eventValues.getTxHash(), eventValues.getBlockTimestamp(), eventValues.getBlockNumber().longValue());
        contractNftManager.transfer(log);
    }
}
