package com.sublayer.api.domain.vo;

import lombok.Data;
import org.web3j.protocol.core.methods.response.EthLog;

import java.math.BigInteger;
import java.util.List;

@Data
public class AllLogsVo {
    private List<EthLog.LogResult> allLogs;
    private BigInteger end;

    public AllLogsVo(List<EthLog.LogResult> allLogs, BigInteger end) {
        this.allLogs = allLogs;
        this.end = end;
    }
}
