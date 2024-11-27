package com.sublayer.api.domain.dto;

import lombok.Data;

import java.math.BigInteger;

@Data
public class TransferLog {
    private String address;
    private BigInteger tokenId;
    private String from;
    private String to;

    private String txHash;

    private BigInteger blockTimestamp;

    private Long blockNumber;
    public TransferLog(String address, BigInteger tokenId, String from, String to, String txHash, BigInteger blockTimestamp, Long blockNumber){
        this.address = address;
        this.tokenId = tokenId;
        this.from = from;
        this.to = to;
        this.txHash = txHash;
        this.blockTimestamp = blockTimestamp;
        this.blockNumber = blockNumber;
    }
}
